package com.healthstep.controller;

import com.healthstep.model.WorkoutLog;
import com.healthstep.repository.WorkoutLogRepository;
import com.healthstep.service.LeaderboardService;
import com.healthstep.service.ChallengeService;
import com.healthstep.sync.SyncHub;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/workout")
public class WorkoutController {

    private final WorkoutLogRepository repo;
    private final LeaderboardService leaderboard;
    private final SyncHub hub;
    private final ChallengeService challengeService;

    public WorkoutController(WorkoutLogRepository repo, LeaderboardService leaderboard, SyncHub hub, ChallengeService challengeService) {
        this.repo = repo;
        this.leaderboard = leaderboard;
        this.hub = hub;
        this.challengeService = challengeService;
    }

    // sanity ping
    @GetMapping("/ping")
    public String ping() { return "workout-ok"; }

    // SINGLE handler for POST /workout/add
    @PostMapping("/add")
    public WorkoutLog add(@RequestBody WorkoutLog log) {
        log.setDate(LocalDate.now());

        // Optional: estimate calories if not supplied
        if (log.getCalories() == null) {
            int time = Math.max(0, log.getTime());
            String t = (log.getType() == null ? "" : log.getType()).toLowerCase();
            int estimate = switch (t) {
                case "running"  -> (int) Math.round(time * 11.0); // rough kcal/min
                case "walking"  -> (int) Math.round(time * 4.0);
                case "cycling"  -> (int) Math.round(time * 8.0);
                case "strength" -> (int) Math.round(time * 6.0);
                default         -> (int) Math.round(time * 5.0);
            };
            log.setCalories(estimate);
        }

        WorkoutLog saved = repo.save(log);

        // Scoring: +1 per 10 minutes, +1 extra if distance >= 5km (null-safe)
        // after saving WorkoutLog saved
        int mins = Math.max(0, saved.getTime());                 // required
        Double dist = saved.getDistance();
        int pts = (mins / 10) + ((dist != null && dist >= 5.0) ? 1 : 0);
        leaderboard.addPoints(saved.getUserId(), pts, LeaderboardService.Kind.WORKOUT, mins);
        challengeService.updateProgress(saved.getUserId(), "WORKOUT", mins);

        hub.publish(saved.getUserId(), "workout");

        return saved;
    }

    // Today’s workouts for a user
    @GetMapping("/{userId}")
    public List<WorkoutLog> getTodayWorkouts(@PathVariable Long userId) {
        return repo.findByUserIdAndDate(userId, LocalDate.now());
    }

    // Path-style variant: POST /workout/{userId}/add
    @PostMapping("/{userId}/add")
    public WorkoutLog addForUser(@PathVariable Long userId, @RequestBody WorkoutLog log) {
        log.setUserId(userId);
        log.setDate(LocalDate.now());

        // Optional: estimate calories if not supplied (same as above)
        if (log.getCalories() == null) {
            int time = Math.max(0, log.getTime());
            String t = (log.getType() == null ? "" : log.getType()).toLowerCase();
            int estimate = switch (t) {
                case "running"  -> (int) Math.round(time * 11.0);
                case "walking"  -> (int) Math.round(time * 4.0);
                case "cycling"  -> (int) Math.round(time * 8.0);
                case "strength" -> (int) Math.round(time * 6.0);
                default         -> (int) Math.round(time * 5.0);
            };
            log.setCalories(estimate);
        }

        WorkoutLog saved = repo.save(log);

        int mins = Math.max(0, saved.getTime());
        Double dist = saved.getDistance();
        int pts = (mins / 10) + ((dist != null && dist >= 5.0) ? 1 : 0);
        leaderboard.addPoints(saved.getUserId(), pts, LeaderboardService.Kind.WORKOUT, mins);
        challengeService.updateProgress(saved.getUserId(), "WORKOUT", mins);

        hub.publish(saved.getUserId(), "workout");
        return saved;
    }
}
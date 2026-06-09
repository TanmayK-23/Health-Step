package com.healthstep.service;

import com.healthstep.model.SleepLog;
import com.healthstep.model.WaterLog;
import com.healthstep.model.WorkoutLog;
import com.healthstep.repository.SleepLogRepository;
import com.healthstep.repository.WaterLogRepository;
import com.healthstep.repository.WorkoutLogRepository;
import com.healthstep.sync.SyncHub;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Random;

@Service
public class WearableSyncService {

    private final WaterLogRepository waterRepo;
    private final SleepLogRepository sleepRepo;
    private final WorkoutLogRepository workoutRepo;
    private final LeaderboardService leaderboardService;
    private final ChallengeService challengeService;
    private final SyncHub syncHub;

    public WearableSyncService(WaterLogRepository waterRepo, SleepLogRepository sleepRepo,
                               WorkoutLogRepository workoutRepo, LeaderboardService leaderboardService,
                               ChallengeService challengeService, SyncHub syncHub) {
        this.waterRepo = waterRepo;
        this.sleepRepo = sleepRepo;
        this.workoutRepo = workoutRepo;
        this.leaderboardService = leaderboardService;
        this.challengeService = challengeService;
        this.syncHub = syncHub;
    }

    @Transactional
    public void syncMockData(Long userId, String source) {
        Random rand = new Random();
        LocalDate today = LocalDate.now();

        // 1. Mock Water Sync (add 500-1500ml)
        int waterAmount = 500 + (rand.nextInt(3) * 500); // 500, 1000, or 1500
        WaterLog waterLog = new WaterLog();
        waterLog.setUserId(userId);
        waterLog.setAmount(waterAmount);
        waterLog.setDate(today);
        waterRepo.save(waterLog);
        
        int waterPts = waterAmount / 250;
        leaderboardService.addPoints(userId, waterPts, LeaderboardService.Kind.WATER, waterAmount);
        challengeService.updateProgress(userId, "WATER", waterAmount);
        syncHub.publish(userId, "water");

        // 2. Mock Workout Sync (add 30-60 mins walking/running)
        int workoutMins = 30 + rand.nextInt(31); // 30 to 60 mins
        WorkoutLog workoutLog = new WorkoutLog();
        workoutLog.setUserId(userId);
        workoutLog.setType(source.equalsIgnoreCase("apple") ? "Running" : "Walking");
        workoutLog.setTime(workoutMins);
        workoutLog.setDistance(workoutMins / 10.0); // Rough estimate
        workoutLog.setCalories(workoutMins * 8);
        workoutLog.setDate(today);
        workoutRepo.save(workoutLog);

        int workoutPts = (workoutMins / 10) + 1;
        leaderboardService.addPoints(userId, workoutPts, LeaderboardService.Kind.WORKOUT, workoutMins);
        challengeService.updateProgress(userId, "WORKOUT", workoutMins);
        syncHub.publish(userId, "workout");

        // 3. Mock Sleep Sync (add 6-8 hours) if not already logged today
        if (sleepRepo.findByUserIdAndDate(userId, today).isEmpty()) {
            int sleepMins = 360 + rand.nextInt(121); // 6 to 8 hours
            SleepLog sleepLog = new SleepLog();
            sleepLog.setUserId(userId);
            sleepLog.setSleepStart("23:00");
            
            // Format end time
            int endH = (23 + (sleepMins / 60)) % 24;
            int endM = sleepMins % 60;
            sleepLog.setSleepEnd(String.format("%02d:%02d", endH, endM));
            sleepLog.setDuration(sleepMins);
            sleepLog.setDate(today);
            sleepRepo.save(sleepLog);

            int sleepHrs = sleepMins / 60;
            int sleepPts = Math.min(16, sleepHrs) * 2;
            leaderboardService.addPoints(userId, sleepPts, LeaderboardService.Kind.SLEEP, sleepMins);
            challengeService.updateProgress(userId, "SLEEP", sleepMins);
            syncHub.publish(userId, "sleep");
        }
        
        // Notify clients that data changed overall (so AI insights can trigger)
        syncHub.publish(userId, "sync");
    }
}

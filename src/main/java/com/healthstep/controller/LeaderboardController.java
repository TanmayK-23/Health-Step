package com.healthstep.controller;

import com.healthstep.repository.LeaderboardRepository.TodayRow;
import com.healthstep.service.LeaderboardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboard;

    public LeaderboardController(LeaderboardService leaderboard) {
        this.leaderboard = leaderboard;
    }

    @GetMapping("/today")
    public List<TodayRow> today() {
        return leaderboard.topToday(20); // top 20
    }
}
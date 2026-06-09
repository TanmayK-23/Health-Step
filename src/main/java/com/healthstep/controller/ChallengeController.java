package com.healthstep.controller;

import com.healthstep.model.Challenge;
import com.healthstep.model.UserChallenge;
import com.healthstep.service.ChallengeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @GetMapping("/active")
    public List<Challenge> getActiveChallenges() {
        return challengeService.getActiveChallenges();
    }

    @GetMapping("/user/{userId}")
    public List<UserChallenge> getUserChallenges(@PathVariable Long userId) {
        return challengeService.getUserChallenges(userId);
    }

    @PostMapping("/{challengeId}/join/{userId}")
    public UserChallenge joinChallenge(@PathVariable Long challengeId, @PathVariable Long userId) {
        return challengeService.joinChallenge(userId, challengeId);
    }
}

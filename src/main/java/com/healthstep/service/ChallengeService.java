package com.healthstep.service;

import com.healthstep.model.Challenge;
import com.healthstep.model.UserChallenge;
import com.healthstep.repository.ChallengeRepository;
import com.healthstep.repository.UserChallengeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final UserChallengeRepository userChallengeRepository;

    public ChallengeService(ChallengeRepository challengeRepository, UserChallengeRepository userChallengeRepository) {
        this.challengeRepository = challengeRepository;
        this.userChallengeRepository = userChallengeRepository;
    }

    @PostConstruct
    public void seedChallenges() {
        if (challengeRepository.count() == 0) {
            Challenge c1 = new Challenge();
            c1.setTitle("7-Day Hydration Streak");
            c1.setDescription("Drink at least 15,000 ml of water over the next 7 days.");
            c1.setType("WATER");
            c1.setTargetValue(15000);
            c1.setStartDate(LocalDate.now());
            c1.setEndDate(LocalDate.now().plusDays(7));
            challengeRepository.save(c1);

            Challenge c2 = new Challenge();
            c2.setTitle("Workout Warrior");
            c2.setDescription("Complete 120 minutes of workout.");
            c2.setType("WORKOUT");
            c2.setTargetValue(120);
            c2.setStartDate(LocalDate.now());
            c2.setEndDate(LocalDate.now().plusDays(7));
            challengeRepository.save(c2);
            
            Challenge c3 = new Challenge();
            c3.setTitle("Sleep Champion");
            c3.setDescription("Get 50 hours of sleep this week.");
            c3.setType("SLEEP");
            c3.setTargetValue(50 * 60); // minutes
            c3.setStartDate(LocalDate.now());
            c3.setEndDate(LocalDate.now().plusDays(7));
            challengeRepository.save(c3);
        }
    }

    public List<Challenge> getActiveChallenges() {
        return challengeRepository.findActiveChallenges(LocalDate.now());
    }

    public List<UserChallenge> getUserChallenges(Long userId) {
        return userChallengeRepository.findByUserId(userId);
    }

    @Transactional
    public UserChallenge joinChallenge(Long userId, Long challengeId) {
        Optional<UserChallenge> existing = userChallengeRepository.findByUserIdAndChallengeId(userId, challengeId);
        if (existing.isPresent()) {
            return existing.get();
        }
        UserChallenge uc = new UserChallenge();
        uc.setUserId(userId);
        uc.setChallengeId(challengeId);
        uc.setProgress(0);
        uc.setCompleted(false);
        return userChallengeRepository.save(uc);
    }

    @Transactional
    public void updateProgress(Long userId, String type, int amountAdded) {
        List<UserChallenge> activeUserChallenges = userChallengeRepository.findByUserIdAndCompletedFalse(userId);
        for (UserChallenge uc : activeUserChallenges) {
            challengeRepository.findById(uc.getChallengeId()).ifPresent(challenge -> {
                if (challenge.getType().equalsIgnoreCase(type) && !uc.isCompleted()) {
                    uc.setProgress(uc.getProgress() + amountAdded);
                    if (uc.getProgress() >= challenge.getTargetValue()) {
                        uc.setProgress(challenge.getTargetValue());
                        uc.setCompleted(true);
                    }
                    userChallengeRepository.save(uc);
                }
            });
        }
    }
}

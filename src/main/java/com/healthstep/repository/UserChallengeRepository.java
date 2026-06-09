package com.healthstep.repository;

import com.healthstep.model.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long> {
    List<UserChallenge> findByUserId(Long userId);
    Optional<UserChallenge> findByUserIdAndChallengeId(Long userId, Long challengeId);
    List<UserChallenge> findByUserIdAndCompletedFalse(Long userId);
}

package com.healthstep.repository;

import com.healthstep.model.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    
    @Query("SELECT c FROM Challenge c WHERE c.startDate <= :today AND c.endDate >= :today")
    List<Challenge> findActiveChallenges(LocalDate today);
}

package com.healthstep.repository;

import com.healthstep.model.SleepLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SleepLogRepository extends JpaRepository<SleepLog, Long> {
    List<SleepLog> findByUserIdAndDate(Long userId, LocalDate date);
}
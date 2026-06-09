package com.healthstep.repository;

import com.healthstep.model.WaterLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WaterLogRepository extends JpaRepository<WaterLog, Long> {
    List<WaterLog> findByUserIdAndDate(Long userId, LocalDate date);
}
package com.healthstep.service;

import com.healthstep.model.NutritionLog;
import com.healthstep.model.SleepLog;
import com.healthstep.model.WaterLog;
import com.healthstep.model.WorkoutLog;
import com.healthstep.repository.NutritionLogRepository;
import com.healthstep.repository.SleepLogRepository;
import com.healthstep.repository.WaterLogRepository;
import com.healthstep.repository.WorkoutLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InsightService {

    private final WaterLogRepository waterRepo;
    private final SleepLogRepository sleepRepo;
    private final WorkoutLogRepository workoutRepo;
    private final NutritionLogRepository nutritionRepo;

    public InsightService(WaterLogRepository waterRepo, SleepLogRepository sleepRepo,
                          WorkoutLogRepository workoutRepo, NutritionLogRepository nutritionRepo) {
        this.waterRepo = waterRepo;
        this.sleepRepo = sleepRepo;
        this.workoutRepo = workoutRepo;
        this.nutritionRepo = nutritionRepo;
    }

    public String generateDailyInsight(Long userId) {
        LocalDate today = LocalDate.now();

        // Fetch data
        List<WaterLog> waterLogs = waterRepo.findByUserIdAndDate(userId, today);
        int totalWater = waterLogs.stream().mapToInt(w -> w.getAmount() != null ? w.getAmount() : 0).sum();

        List<SleepLog> sleepLogs = sleepRepo.findByUserIdAndDate(userId, today);
        int totalSleepMins = sleepLogs.stream().mapToInt(s -> s.getDuration() != null ? s.getDuration() : 0).sum();
        double sleepHours = totalSleepMins / 60.0;

        List<WorkoutLog> workoutLogs = workoutRepo.findByUserIdAndDate(userId, today);
        int totalWorkoutMins = workoutLogs.stream().mapToInt(w -> w.getTime() != null ? w.getTime() : 0).sum();

        List<NutritionLog> nutritionLogs = nutritionRepo.findByUserIdAndDate(userId, today);
        int totalKcal = nutritionLogs.stream().mapToInt(n -> n.getKcal() != null ? n.getKcal() : 0).sum();
        
        // Heuristic Engine Logic
        
        // Rule 1: No data
        if (totalWater == 0 && sleepHours == 0 && totalWorkoutMins == 0 && totalKcal == 0) {
            return "Welcome! Log some data today to receive your first AI-driven health insight.";
        }

        // Rule 2: Dehydration Risk
        if (totalWorkoutMins > 30 && totalWater < 1000) {
            return "You had a solid workout but your hydration is very low. Drink at least 2 glasses of water to aid recovery and prevent fatigue!";
        }

        // Rule 3: Sleep & Energy
        if (sleepHours > 0 && sleepHours < 6 && totalKcal > 2500) {
            return "Your sleep was short last night. Try to avoid heavy, high-calorie meals today to prevent an afternoon energy crash.";
        }
        
        // Rule 4: Great Sleep
        if (sleepHours >= 7.5 && totalWorkoutMins == 0) {
            return "You got great sleep last night! Your body is fully recovered. Today is a perfect day for a challenging workout.";
        }
        
        // Rule 5: Hydration Focus
        if (totalWater > 0 && totalWater < 1500 && totalWorkoutMins == 0 && sleepHours >= 6) {
            return "You are a bit behind on your hydration. Drink a glass of water right now to keep your metabolism active!";
        }
        
        // Rule 6: Excellent Consistency
        if (totalWater >= 2000 && sleepHours >= 7 && totalWorkoutMins >= 30) {
            return "Incredible consistency! You are hitting your core baseline metrics today. Keep up the momentum!";
        }
        
        // Fallback
        return "You're making healthy choices today. Keep tracking your metrics to unlock more personalized insights.";
    }
}

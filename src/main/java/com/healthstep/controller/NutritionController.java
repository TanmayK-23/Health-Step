package com.healthstep.controller;

import com.healthstep.model.NutritionLog;
import com.healthstep.repository.NutritionLogRepository;
import com.healthstep.service.LeaderboardService;
import com.healthstep.sync.SyncHub;

import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/nutrition")
public class NutritionController {

    // DTO that matches the frontend payload (meal, food, …)
    public static class NutritionItemDto {
        public Long userId;
        public String meal;   // breakfast/lunch/dinner/snacks   <-- frontend key
        public String food;   // item name                        <-- frontend key
        public Integer quantity;
        public Integer kcal;
        public Integer protein;
        public Integer carbs;
        public Integer fat;
    }
    private final NutritionLogRepository repo;


    private final LeaderboardService leaderboard;
    private final SyncHub hub;
    public NutritionController(NutritionLogRepository repo, LeaderboardService leaderboard, SyncHub hub) {
        this.repo = repo;
        this.leaderboard = leaderboard;
        this.hub = hub;
    }


    @GetMapping("/ping")
    public String ping() { return "nutrition-ok"; }

    @PostMapping("/add")
    public NutritionLog addEntry(@RequestBody NutritionItemDto dto) {
        NutritionLog n = new NutritionLog();
        n.setUserId(dto.userId);
        n.setDate(LocalDate.now());
        // map JSON names to entity fields/DB columns
        n.setMeal(dto.meal);       // -> meal_type
        n.setFood(dto.food);       // -> item
        n.setQuantity(dto.quantity);
        n.setKcal(dto.kcal != null ? dto.kcal : 0);
        n.setProtein(dto.protein != null ? dto.protein : 0);
        n.setCarbs(dto.carbs != null ? dto.carbs : 0);
        n.setFat(dto.fat != null ? dto.fat : 0);
        NutritionLog saved = repo.save(n);
        if (saved.getUserId() != null) {
            hub.publish(saved.getUserId(), "nutrition");
        }
        return saved;
    }

    @PostMapping("/{userId}/add")
    public NutritionLog addEntryForUser(@PathVariable Long userId, @RequestBody NutritionItemDto dto) {
        NutritionLog n = new NutritionLog();
        n.setUserId(userId);
        n.setDate(LocalDate.now());
        n.setMeal(dto.meal);
        n.setFood(dto.food);
        n.setQuantity(dto.quantity);
        n.setKcal(dto.kcal != null ? dto.kcal : 0);
        n.setProtein(dto.protein != null ? dto.protein : 0);
        n.setCarbs(dto.carbs != null ? dto.carbs : 0);
        n.setFat(dto.fat != null ? dto.fat : 0);
        NutritionLog saved = repo.save(n);
        hub.publish(userId, "nutrition");
        return saved;
    }

    // Today’s entries for a user
    @GetMapping("/{userId}")
    public List<NutritionLog> getTodayEntries(@PathVariable Long userId) {
        return repo.findByUserIdAndDate(userId, LocalDate.now());
    }

    // Today’s totals (kcal/macros) for a user
    @GetMapping("/{userId}/totals")
    public Map<String, Integer> getTodayTotals(@PathVariable Long userId) {
        List<NutritionLog> list = repo.findByUserIdAndDate(userId, LocalDate.now());
        int kcal = 0, protein = 0, carbs = 0, fat = 0;
        for (NutritionLog n : list) {
            kcal += n.getKcal();
            protein += n.getProtein();
            carbs += n.getCarbs();
            fat += n.getFat();
        }
        Map<String, Integer> totals = new HashMap<>();
        totals.put("kcal", kcal);
        totals.put("protein", protein);
        totals.put("carbs", carbs);
        totals.put("fat", fat);
        return totals;
    }

    // DELETE one log by id (optional, handy for future)
    @DeleteMapping("/{logId}")
    public void deleteEntry(@PathVariable Long logId) {
        repo.deleteById(logId);
    }

    // Replace *today’s* rows for this user with what the client sends
    @PostMapping("/{userId}/replaceToday")
    @Transactional
    public List<NutritionLog> replaceToday(@PathVariable Long userId,
                                        @RequestBody List<NutritionItemDto> items) {
        LocalDate today = LocalDate.now();
        repo.deleteByUserIdAndDate(userId, today);

        List<NutritionLog> fresh = items.stream().map(src -> {
            NutritionLog n = new NutritionLog();
            n.setUserId(userId);
            n.setDate(today);
            n.setMeal(src.meal);
            n.setFood(src.food);
            n.setQuantity(src.quantity);
            n.setKcal(src.kcal != null ? src.kcal : 0);
            n.setProtein(src.protein != null ? src.protein : 0);
            n.setCarbs(src.carbs != null ? src.carbs : 0);
            n.setFat(src.fat != null ? src.fat : 0);
            return n;
        }).toList();

        List<NutritionLog> saved = repo.saveAll(fresh);

        // after /nutrition/{userId}/replaceToday saves list
        int entries = saved.size();              // or derive a smarter nutrition score
        leaderboard.addPoints(userId, entries, LeaderboardService.Kind.NUTRITION, entries);

        hub.publish(userId, "nutrition");

        return saved;
    }
}

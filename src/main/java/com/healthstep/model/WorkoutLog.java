package com.healthstep.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "workout_logs")
public class WorkoutLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private LocalDate date;

    // e.g. "Running", "Walking", "Cycling", "Strength"
    private String type;

    // minutes
    private int time;

    // km if applicable (Running/Walking/Cycling); 0 for Strength
    private Double distance;  // wrapper, allows null

    // for Strength: e.g. "Chest", "Legs" (empty otherwise)
    private String muscle;

    // optional quick stat (you can compute client-side or here)
    private Integer calories;

    public WorkoutLog() {}

    public WorkoutLog(Long userId, LocalDate date, String type, int time, double distance, String muscle, Integer calories) {
        this.userId = userId;
        this.date = date;
        this.type = type;
        this.time = time;
        this.distance = distance;
        this.muscle = muscle;
        this.calories = calories;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getTime() { return time; }
    public void setTime(int time) { this.time = time; }
    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }
    public String getMuscle() { return muscle; }
    public void setMuscle(String muscle) { this.muscle = muscle; }
    public Integer getCalories() { return calories; }
    public void setCalories(Integer calories) { this.calories = calories; }
}
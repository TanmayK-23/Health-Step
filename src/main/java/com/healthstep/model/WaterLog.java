package com.healthstep.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "water_logs")
public class WaterLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private int amount; // in ml
    private LocalDate date;

    // Constructors
    public WaterLog() {}
    public WaterLog(Long userId, int amount, LocalDate date) {
        this.userId = userId;
        this.amount = amount;
        this.date = date;
    }

    // Getters and setters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
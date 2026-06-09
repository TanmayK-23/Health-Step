package com.healthstep.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "sleep_logs")
public class SleepLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private LocalDate date;
    private String sleepStart; // e.g. "23:00"
    private String sleepEnd;   // e.g. "07:00"
    private int duration;      // minutes

    public SleepLog() {}

    public SleepLog(Long userId, LocalDate date, String sleepStart, String sleepEnd, int duration) {
        this.userId = userId;
        this.date = date;
        this.sleepStart = sleepStart;
        this.sleepEnd = sleepEnd;
        this.duration = duration;
    }

    // getters and setters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getSleepStart() { return sleepStart; }
    public void setSleepStart(String sleepStart) { this.sleepStart = sleepStart; }
    public String getSleepEnd() { return sleepEnd; }
    public void setSleepEnd(String sleepEnd) { this.sleepEnd = sleepEnd; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
}
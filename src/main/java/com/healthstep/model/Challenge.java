package com.healthstep.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "challenges")
public class Challenge {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;
  private String description;
  private String type; // e.g. "WATER", "WORKOUT", "SLEEP"
  
  private int targetValue; // e.g. 15000 (ml) or 120 (mins)
  
  private LocalDate startDate;
  private LocalDate endDate;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  
  public int getTargetValue() { return targetValue; }
  public void setTargetValue(int targetValue) { this.targetValue = targetValue; }
  
  public LocalDate getStartDate() { return startDate; }
  public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
  
  public LocalDate getEndDate() { return endDate; }
  public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}

package com.healthstep.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
  name = "leaderboard",
  uniqueConstraints = @UniqueConstraint(columnNames = {"userId","date"})
)
public class LeaderboardEntry {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long userId;
  private String username;   // optional
  private int score;
  private LocalDate date;

  // getters/setters …
  public Long getId(){ return id; }
  public void setId(Long id){ this.id=id; }
  public Long getUserId(){ return userId; }
  public void setUserId(Long userId){ this.userId=userId; }
  public String getUsername(){ return username; }
  public void setUsername(String username){ this.username=username; }
  public int getScore(){ return score; }
  public void setScore(int score){ this.score=score; }
  public LocalDate getDate(){ return date; }
  public void setDate(LocalDate date){ this.date=date; }
}
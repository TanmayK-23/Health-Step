package com.healthstep.model;

import jakarta.persistence.*;

@Entity
@Table(
  name = "user_challenges",
  uniqueConstraints = @UniqueConstraint(columnNames = {"userId","challengeId"})
)
public class UserChallenge {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long userId;
  private Long challengeId;
  
  private int progress;
  private boolean completed;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }

  public Long getChallengeId() { return challengeId; }
  public void setChallengeId(Long challengeId) { this.challengeId = challengeId; }

  public int getProgress() { return progress; }
  public void setProgress(int progress) { this.progress = progress; }

  public boolean isCompleted() { return completed; }
  public void setCompleted(boolean completed) { this.completed = completed; }
}

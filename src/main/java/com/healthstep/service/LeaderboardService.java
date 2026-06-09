package com.healthstep.service;

import com.healthstep.repository.LeaderboardRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LeaderboardService {

  public enum Kind { WATER, SLEEP, WORKOUT, NUTRITION, GENERIC }

  private final LeaderboardRepository repo;

  public LeaderboardService(LeaderboardRepository repo) {
    this.repo = repo;
  }

  public List<LeaderboardRepository.TodayRow> topToday(int limit) {
    return repo.topToday().stream().limit(limit).toList();
  }

  /** Simplest: just add points to total_score (no breakdown) */
  public void addPoints(long userId, int delta) {
    addPoints(userId, delta, Kind.GENERIC, 0);
  }

  /**
   * Generic entry point used by ALL controllers.
   * @param userId  the user
   * @param delta   how many points to add to total_score
   * @param kind    which bucket to bump (water/sleep/workout/nutrition/generic)
   * @param amount  the raw amount for that bucket, e.g.
   *                ml for WATER, minutes for SLEEP/WORKOUT, “score units” for NUTRITION
   */
  public void addPoints(long userId, int delta, Kind kind, int amount) {
    int water = 0, sleep = 0, workout = 0, nutri = 0;

    switch (kind) {
      case WATER    -> water   = Math.max(0, amount);
      case SLEEP    -> sleep   = Math.max(0, amount);
      case WORKOUT  -> workout = Math.max(0, amount);
      case NUTRITION-> nutri   = Math.max(0, amount);
      default       -> { /* GENERIC: only total_score */ }
    }
    repo.upsertDeltas(userId, delta, water, sleep, workout, nutri);
  }
}
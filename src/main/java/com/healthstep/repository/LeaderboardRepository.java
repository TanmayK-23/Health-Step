package com.healthstep.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class LeaderboardRepository {

  @PersistenceContext
  private EntityManager em;

  @Transactional
  public void upsertDeltas(long userId, int delta, int waterMl, int sleepMin, int workoutMin, int nutriScore) {
    String sql = """
      INSERT INTO user_score_daily
        (user_id, score_date, water_ml, sleep_min, workout_min, nutri_score, total_score)
      VALUES
        (:userId, CURDATE(), :water, :sleep, :workout, :nutri, :delta)
      ON DUPLICATE KEY UPDATE
        water_ml    = water_ml    + VALUES(water_ml),
        sleep_min   = sleep_min   + VALUES(sleep_min),
        workout_min = workout_min + VALUES(workout_min),
        nutri_score = nutri_score + VALUES(nutri_score),
        total_score = total_score + VALUES(total_score)
      """;
    em.createNativeQuery(sql)
      .setParameter("userId", userId)
      .setParameter("delta", delta)
      .setParameter("water", waterMl)
      .setParameter("sleep", sleepMin)
      .setParameter("workout", workoutMin)
      .setParameter("nutri", nutriScore)
      .executeUpdate();
  }

  public List<TodayRow> topToday() {
    String sql = """
      SELECT
        u.id           AS userId,
        u.username     AS username,
        d.total_score  AS score,
        d.water_ml     AS waterMl,
        d.sleep_min    AS sleepMin,
        d.workout_min  AS workoutMin,
        d.nutri_score  AS nutriScore
      FROM user_score_daily d
      JOIN users u ON u.id = d.user_id
      WHERE d.score_date = CURDATE()
      ORDER BY d.total_score DESC
      """;

    @SuppressWarnings("unchecked")
    List<Object[]> raw = em.createNativeQuery(sql).getResultList();

    List<TodayRow> out = new ArrayList<>(raw.size());
    for (Object[] r : raw) {
      Long userId     = ((Number) r[0]).longValue();
      String username = (String) r[1];
      Integer score   = r[2] == null ? 0 : ((Number) r[2]).intValue();
      Integer waterMl = r[3] == null ? 0 : ((Number) r[3]).intValue();
      Integer sleepMin= r[4] == null ? 0 : ((Number) r[4]).intValue();
      Integer workout = r[5] == null ? 0 : ((Number) r[5]).intValue();
      Integer nutri   = r[6] == null ? 0 : ((Number) r[6]).intValue();
      out.add(new TodayRow(userId, username, score, waterMl, sleepMin, workout, nutri));
    }
    return out;
  }

  // Simple DTO used by service/controller
  public static class TodayRow {
    private final Long userId;
    private final String username;
    private final Integer score;
    private final Integer waterMl;
    private final Integer sleepMin;
    private final Integer workoutMin;
    private final Integer nutriScore;

    public TodayRow(Long userId, String username, Integer score, Integer waterMl, Integer sleepMin, Integer workoutMin, Integer nutriScore) {
      this.userId = userId;
      this.username = username;
      this.score = score;
      this.waterMl = waterMl;
      this.sleepMin = sleepMin;
      this.workoutMin = workoutMin;
      this.nutriScore = nutriScore;
    }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public Integer getScore() { return score; }
    public Integer getWaterMl() { return waterMl; }
    public Integer getSleepMin() { return sleepMin; }
    public Integer getWorkoutMin() { return workoutMin; }
    public Integer getNutriScore() { return nutriScore; }
  }
}
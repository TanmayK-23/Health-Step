package com.healthstep.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "nutrition_logs")
public class NutritionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "meal_type")
    private String meal;

    @Column(name = "item")
    private String food;

    // DB has no quantity column — leave it out or add it in Option B
    // private Integer quantity;
    @Column(name = "quantity")
    private Integer quantity;

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    private Integer kcal;
    private Integer protein;
    private Integer carbs;
    private Integer fat;

    // --- getters/setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getMeal() { return meal; }
    public void setMeal(String meal) { this.meal = meal; }

    public String getFood() { return food; }
    public void setFood(String food) { this.food = food; }

    public Integer getKcal() { return kcal; }
    public void setKcal(Integer kcal) { this.kcal = kcal; }

    public Integer getProtein() { return protein; }
    public void setProtein(Integer protein) { this.protein = protein; }

    public Integer getCarbs() { return carbs; }
    public void setCarbs(Integer carbs) { this.carbs = carbs; }

    public Integer getFat() { return fat; }
    public void setFat(Integer fat) { this.fat = fat; }
}
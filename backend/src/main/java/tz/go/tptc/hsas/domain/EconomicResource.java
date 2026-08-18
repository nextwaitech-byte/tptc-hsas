package tz.go.tptc.hsas.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "economic_resources")
public class EconomicResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 80)
    private String region;

    @Column(name = "period_month", nullable = false)
    private LocalDate periodMonth;

    @Column(name = "refugee_population", nullable = false)
    private int refugeePopulation;

    @Column(name = "aid_budget_allocated", nullable = false)
    private BigDecimal aidBudgetAllocated = BigDecimal.ZERO;

    @Column(name = "aid_budget_disbursed", nullable = false)
    private BigDecimal aidBudgetDisbursed = BigDecimal.ZERO;

    @Column(name = "food_units", nullable = false)
    private int foodUnits;

    @Column(name = "water_units", nullable = false)
    private int waterUnits;

    @Column(name = "shelter_units", nullable = false)
    private int shelterUnits;

    @Column(name = "medical_units", nullable = false)
    private int medicalUnits;

    @Column(name = "job_seekers_registered", nullable = false)
    private int jobSeekersRegistered;

    @Column(name = "jobs_available", nullable = false)
    private int jobsAvailable;

    @Column(name = "daily_cost_per_capita", nullable = false)
    private BigDecimal dailyCostPerCapita = new BigDecimal("3500.00");

    private String notes;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public LocalDate getPeriodMonth() { return periodMonth; }
    public void setPeriodMonth(LocalDate periodMonth) { this.periodMonth = periodMonth; }
    public int getRefugeePopulation() { return refugeePopulation; }
    public void setRefugeePopulation(int refugeePopulation) { this.refugeePopulation = refugeePopulation; }
    public BigDecimal getAidBudgetAllocated() { return aidBudgetAllocated; }
    public void setAidBudgetAllocated(BigDecimal aidBudgetAllocated) { this.aidBudgetAllocated = aidBudgetAllocated; }
    public BigDecimal getAidBudgetDisbursed() { return aidBudgetDisbursed; }
    public void setAidBudgetDisbursed(BigDecimal aidBudgetDisbursed) { this.aidBudgetDisbursed = aidBudgetDisbursed; }
    public int getFoodUnits() { return foodUnits; }
    public void setFoodUnits(int foodUnits) { this.foodUnits = foodUnits; }
    public int getWaterUnits() { return waterUnits; }
    public void setWaterUnits(int waterUnits) { this.waterUnits = waterUnits; }
    public int getShelterUnits() { return shelterUnits; }
    public void setShelterUnits(int shelterUnits) { this.shelterUnits = shelterUnits; }
    public int getMedicalUnits() { return medicalUnits; }
    public void setMedicalUnits(int medicalUnits) { this.medicalUnits = medicalUnits; }
    public int getJobSeekersRegistered() { return jobSeekersRegistered; }
    public void setJobSeekersRegistered(int jobSeekersRegistered) { this.jobSeekersRegistered = jobSeekersRegistered; }
    public int getJobsAvailable() { return jobsAvailable; }
    public void setJobsAvailable(int jobsAvailable) { this.jobsAvailable = jobsAvailable; }
    public BigDecimal getDailyCostPerCapita() { return dailyCostPerCapita; }
    public void setDailyCostPerCapita(BigDecimal dailyCostPerCapita) { this.dailyCostPerCapita = dailyCostPerCapita; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

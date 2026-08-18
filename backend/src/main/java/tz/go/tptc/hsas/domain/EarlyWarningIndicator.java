package tz.go.tptc.hsas.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "early_warning_indicators")
public class EarlyWarningIndicator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "indicator_code", nullable = false, unique = true, length = 40)
    private String indicatorCode;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 80)
    private String domain;

    private String description;

    @Column(name = "threshold_value")
    private BigDecimal thresholdValue;

    @Column(name = "threshold_unit")
    private String thresholdUnit;

    @Column(name = "current_value")
    private BigDecimal currentValue;

    @Column(name = "escalation_level", nullable = false, length = 20)
    private String escalationLevel = "normal";

    @Column(name = "geographical_scope")
    private String geographicalScope;

    @Column(name = "seasonal_note")
    private String seasonalNote;

    @Column(name = "ai_risk_score")
    private BigDecimal aiRiskScore;

    @Column(name = "probability_pct")
    private BigDecimal probabilityPct;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getIndicatorCode() { return indicatorCode; }
    public void setIndicatorCode(String indicatorCode) { this.indicatorCode = indicatorCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getThresholdValue() { return thresholdValue; }
    public void setThresholdValue(BigDecimal thresholdValue) { this.thresholdValue = thresholdValue; }
    public String getThresholdUnit() { return thresholdUnit; }
    public void setThresholdUnit(String thresholdUnit) { this.thresholdUnit = thresholdUnit; }
    public BigDecimal getCurrentValue() { return currentValue; }
    public void setCurrentValue(BigDecimal currentValue) { this.currentValue = currentValue; }
    public String getEscalationLevel() { return escalationLevel; }
    public void setEscalationLevel(String escalationLevel) { this.escalationLevel = escalationLevel; }
    public String getGeographicalScope() { return geographicalScope; }
    public void setGeographicalScope(String geographicalScope) { this.geographicalScope = geographicalScope; }
    public String getSeasonalNote() { return seasonalNote; }
    public void setSeasonalNote(String seasonalNote) { this.seasonalNote = seasonalNote; }
    public BigDecimal getAiRiskScore() { return aiRiskScore; }
    public void setAiRiskScore(BigDecimal aiRiskScore) { this.aiRiskScore = aiRiskScore; }
    public BigDecimal getProbabilityPct() { return probabilityPct; }
    public void setProbabilityPct(BigDecimal probabilityPct) { this.probabilityPct = probabilityPct; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public User getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(User updatedBy) { this.updatedBy = updatedBy; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}

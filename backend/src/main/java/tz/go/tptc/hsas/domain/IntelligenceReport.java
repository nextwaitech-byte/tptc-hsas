package tz.go.tptc.hsas.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "intelligence_reports")
public class IntelligenceReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "report_code", nullable = false, unique = true, length = 30)
    private String reportCode;

    @Column(nullable = false, length = 40)
    private String domain;

    @Column(nullable = false, length = 80)
    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "border_post_id")
    private BorderPost borderPost;

    private String region;
    private String district;

    @Column(name = "camp_name")
    private String campName;

    @Column(name = "reported_at", nullable = false)
    private Instant reportedAt;

    @Column(nullable = false, length = 20)
    private String severity = "medium";

    @Column(name = "threat_level", nullable = false, length = 20)
    private String threatLevel = "yellow";

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false)
    private String description;

    private BigDecimal latitude;
    private BigDecimal longitude;

    @Column(name = "metric_value")
    private BigDecimal metricValue;

    @Column(name = "metric_unit")
    private String metricUnit;

    @Column(name = "metric_label")
    private String metricLabel;

    @Column(name = "responsible_agency")
    private String responsibleAgency;

    @Column(name = "actions_taken")
    private String actionsTaken;

    @Column(name = "resources_required")
    private String resourcesRequired;

    @Column(name = "recommended_action")
    private String recommendedAction;

    @Column(nullable = false, length = 20)
    private String status = "open";

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by")
    private User reportedBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getReportCode() { return reportCode; }
    public void setReportCode(String reportCode) { this.reportCode = reportCode; }
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BorderPost getBorderPost() { return borderPost; }
    public void setBorderPost(BorderPost borderPost) { this.borderPost = borderPost; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getCampName() { return campName; }
    public void setCampName(String campName) { this.campName = campName; }
    public Instant getReportedAt() { return reportedAt; }
    public void setReportedAt(Instant reportedAt) { this.reportedAt = reportedAt; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getThreatLevel() { return threatLevel; }
    public void setThreatLevel(String threatLevel) { this.threatLevel = threatLevel; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public BigDecimal getMetricValue() { return metricValue; }
    public void setMetricValue(BigDecimal metricValue) { this.metricValue = metricValue; }
    public String getMetricUnit() { return metricUnit; }
    public void setMetricUnit(String metricUnit) { this.metricUnit = metricUnit; }
    public String getMetricLabel() { return metricLabel; }
    public void setMetricLabel(String metricLabel) { this.metricLabel = metricLabel; }
    public String getResponsibleAgency() { return responsibleAgency; }
    public void setResponsibleAgency(String responsibleAgency) { this.responsibleAgency = responsibleAgency; }
    public String getActionsTaken() { return actionsTaken; }
    public void setActionsTaken(String actionsTaken) { this.actionsTaken = actionsTaken; }
    public String getResourcesRequired() { return resourcesRequired; }
    public void setResourcesRequired(String resourcesRequired) { this.resourcesRequired = resourcesRequired; }
    public String getRecommendedAction() { return recommendedAction; }
    public void setRecommendedAction(String recommendedAction) { this.recommendedAction = recommendedAction; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public User getReportedBy() { return reportedBy; }
    public void setReportedBy(User reportedBy) { this.reportedBy = reportedBy; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}

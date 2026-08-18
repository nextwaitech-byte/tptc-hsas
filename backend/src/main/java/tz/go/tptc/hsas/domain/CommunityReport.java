package tz.go.tptc.hsas.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "community_reports")
public class CommunityReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "report_code", nullable = false, unique = true, length = 30)
    private String reportCode;

    @Column(name = "source_type", nullable = false, length = 40)
    private String sourceType = "community_member";

    @Column(name = "reporter_name")
    private String reporterName;

    @Column(name = "is_anonymous", nullable = false)
    private boolean anonymous;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false)
    private String description;

    private String category;
    private String region;
    private String district;
    private String village;
    private BigDecimal latitude;
    private BigDecimal longitude;

    @Column(name = "media_notes")
    private String mediaNotes;

    @Column(name = "verification_status", nullable = false, length = 20)
    private String verificationStatus = "unverified";

    @Column(name = "response_status", nullable = false, length = 20)
    private String responseStatus = "received";

    @Column(name = "response_notes")
    private String responseNotes;

    @Column(name = "reported_at", nullable = false)
    private Instant reportedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getReportCode() { return reportCode; }
    public void setReportCode(String reportCode) { this.reportCode = reportCode; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }
    public boolean isAnonymous() { return anonymous; }
    public void setAnonymous(boolean anonymous) { this.anonymous = anonymous; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public String getMediaNotes() { return mediaNotes; }
    public void setMediaNotes(String mediaNotes) { this.mediaNotes = mediaNotes; }
    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }
    public String getResponseStatus() { return responseStatus; }
    public void setResponseStatus(String responseStatus) { this.responseStatus = responseStatus; }
    public String getResponseNotes() { return responseNotes; }
    public void setResponseNotes(String responseNotes) { this.responseNotes = responseNotes; }
    public Instant getReportedAt() { return reportedAt; }
    public void setReportedAt(Instant reportedAt) { this.reportedAt = reportedAt; }
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}

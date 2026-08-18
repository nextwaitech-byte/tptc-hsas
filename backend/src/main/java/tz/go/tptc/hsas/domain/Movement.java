package tz.go.tptc.hsas.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "movements")
public class Movement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "record_code", nullable = false, unique = true, length = 30)
    private String recordCode;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "border_post_id")
    private BorderPost borderPost;

    @Column(nullable = false, length = 20)
    private String direction = "entry";

    @Column(name = "crossed_at", nullable = false)
    private Instant crossedAt;

    @Column(nullable = false, length = 80)
    private String nationality;

    @Column(nullable = false, length = 20)
    private String sex;

    private Integer age;

    @Column(name = "age_group", length = 20)
    private String ageGroup;

    @Column(nullable = false, length = 40)
    private String purpose;

    @Column(name = "document_type", nullable = false, length = 40)
    private String documentType = "none";

    @Column(name = "document_number", length = 80)
    private String documentNumber;

    @Column(name = "origin_country", nullable = false, length = 80)
    private String originCountry;

    @Column(name = "destination_region", length = 80)
    private String destinationRegion;

    @Column(name = "destination_district", length = 80)
    private String destinationDistrict;

    @Column(name = "accompanied_minors", nullable = false)
    private int accompaniedMinors = 0;

    @Column(name = "family_group_id", length = 40)
    private String familyGroupId;

    @Column(name = "has_biometric", nullable = false)
    private boolean hasBiometric = false;

    @Column(nullable = false, length = 20)
    private String status = "pending";

    @Column(name = "risk_score", nullable = false)
    private int riskScore = 0;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "officer_id")
    private User officer;

    private String remarks;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRecordCode() { return recordCode; }
    public void setRecordCode(String recordCode) { this.recordCode = recordCode; }
    public BorderPost getBorderPost() { return borderPost; }
    public void setBorderPost(BorderPost borderPost) { this.borderPost = borderPost; }
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
    public Instant getCrossedAt() { return crossedAt; }
    public void setCrossedAt(Instant crossedAt) { this.crossedAt = crossedAt; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getAgeGroup() { return ageGroup; }
    public void setAgeGroup(String ageGroup) { this.ageGroup = ageGroup; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
    public String getOriginCountry() { return originCountry; }
    public void setOriginCountry(String originCountry) { this.originCountry = originCountry; }
    public String getDestinationRegion() { return destinationRegion; }
    public void setDestinationRegion(String destinationRegion) { this.destinationRegion = destinationRegion; }
    public String getDestinationDistrict() { return destinationDistrict; }
    public void setDestinationDistrict(String destinationDistrict) { this.destinationDistrict = destinationDistrict; }
    public int getAccompaniedMinors() { return accompaniedMinors; }
    public void setAccompaniedMinors(int accompaniedMinors) { this.accompaniedMinors = accompaniedMinors; }
    public String getFamilyGroupId() { return familyGroupId; }
    public void setFamilyGroupId(String familyGroupId) { this.familyGroupId = familyGroupId; }
    public boolean isHasBiometric() { return hasBiometric; }
    public void setHasBiometric(boolean hasBiometric) { this.hasBiometric = hasBiometric; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getRiskScore() { return riskScore; }
    public void setRiskScore(int riskScore) { this.riskScore = riskScore; }
    public User getOfficer() { return officer; }
    public void setOfficer(User officer) { this.officer = officer; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public Instant getDeletedAt() { return deletedAt; }
    public void setDeletedAt(Instant deletedAt) { this.deletedAt = deletedAt; }
}

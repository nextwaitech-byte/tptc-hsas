package tz.go.tptc.hsas.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "institutional_capacity")
public class InstitutionalCapacity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "record_code", nullable = false, unique = true, length = 30)
    private String recordCode;

    @Column(name = "institution_name", nullable = false, length = 150)
    private String institutionName;

    @Column(name = "institution_type", nullable = false, length = 40)
    private String institutionType = "other";

    private String region;
    private String district;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "border_post_id")
    private BorderPost borderPost;

    @Column(name = "personnel_count")
    private int personnelCount;

    @Column(name = "emergency_teams")
    private int emergencyTeams;

    private int vehicles;
    private int boats;

    @Column(name = "communication_equipment")
    private int communicationEquipment;

    @Column(name = "emergency_supplies")
    private String emergencySupplies;

    @Column(name = "available_budget")
    private BigDecimal availableBudget;

    @Column(name = "focal_person")
    private String focalPerson;

    @Column(name = "focal_contact")
    private String focalContact;

    @Column(name = "readiness_level", nullable = false, length = 20)
    private String readinessLevel = "medium";

    private String notes;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getRecordCode() { return recordCode; }
    public void setRecordCode(String recordCode) { this.recordCode = recordCode; }
    public String getInstitutionName() { return institutionName; }
    public void setInstitutionName(String institutionName) { this.institutionName = institutionName; }
    public String getInstitutionType() { return institutionType; }
    public void setInstitutionType(String institutionType) { this.institutionType = institutionType; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public BorderPost getBorderPost() { return borderPost; }
    public void setBorderPost(BorderPost borderPost) { this.borderPost = borderPost; }
    public int getPersonnelCount() { return personnelCount; }
    public void setPersonnelCount(int personnelCount) { this.personnelCount = personnelCount; }
    public int getEmergencyTeams() { return emergencyTeams; }
    public void setEmergencyTeams(int emergencyTeams) { this.emergencyTeams = emergencyTeams; }
    public int getVehicles() { return vehicles; }
    public void setVehicles(int vehicles) { this.vehicles = vehicles; }
    public int getBoats() { return boats; }
    public void setBoats(int boats) { this.boats = boats; }
    public int getCommunicationEquipment() { return communicationEquipment; }
    public void setCommunicationEquipment(int communicationEquipment) { this.communicationEquipment = communicationEquipment; }
    public String getEmergencySupplies() { return emergencySupplies; }
    public void setEmergencySupplies(String emergencySupplies) { this.emergencySupplies = emergencySupplies; }
    public BigDecimal getAvailableBudget() { return availableBudget; }
    public void setAvailableBudget(BigDecimal availableBudget) { this.availableBudget = availableBudget; }
    public String getFocalPerson() { return focalPerson; }
    public void setFocalPerson(String focalPerson) { this.focalPerson = focalPerson; }
    public String getFocalContact() { return focalContact; }
    public void setFocalContact(String focalContact) { this.focalContact = focalContact; }
    public String getReadinessLevel() { return readinessLevel; }
    public void setReadinessLevel(String readinessLevel) { this.readinessLevel = readinessLevel; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Instant getRecordedAt() { return recordedAt; }
    public void setRecordedAt(Instant recordedAt) { this.recordedAt = recordedAt; }
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

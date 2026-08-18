package tz.go.tptc.hsas.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicle_crossings")
public class VehicleCrossing {
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

    @Column(name = "vehicle_type", nullable = false, length = 40)
    private String vehicleType;

    @Column(name = "registration_number", nullable = false, length = 40)
    private String registrationNumber;

    @Column(name = "registration_country", nullable = false, length = 80)
    private String registrationCountry;

    @Column(name = "driver_name", nullable = false, length = 120)
    private String driverName;

    @Column(name = "driver_nationality", nullable = false, length = 80)
    private String driverNationality;

    @Column(name = "driver_document", length = 80)
    private String driverDocument;

    @Column(name = "passengers_count", nullable = false)
    private int passengersCount;

    @Column(name = "has_cargo", nullable = false)
    private boolean hasCargo;

    @Column(name = "cargo_declared", nullable = false)
    private boolean cargoDeclared = true;

    @Column(name = "estimated_cargo_value", nullable = false)
    private BigDecimal estimatedCargoValue = BigDecimal.ZERO;

    @Column(nullable = false, length = 20)
    private String status = "pending";

    @Column(name = "risk_score", nullable = false)
    private int riskScore;

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

    @OneToMany(mappedBy = "vehicleCrossing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VehicleCargo> cargoItems = new ArrayList<>();

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
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public String getRegistrationCountry() { return registrationCountry; }
    public void setRegistrationCountry(String registrationCountry) { this.registrationCountry = registrationCountry; }
    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    public String getDriverNationality() { return driverNationality; }
    public void setDriverNationality(String driverNationality) { this.driverNationality = driverNationality; }
    public String getDriverDocument() { return driverDocument; }
    public void setDriverDocument(String driverDocument) { this.driverDocument = driverDocument; }
    public int getPassengersCount() { return passengersCount; }
    public void setPassengersCount(int passengersCount) { this.passengersCount = passengersCount; }
    public boolean isHasCargo() { return hasCargo; }
    public void setHasCargo(boolean hasCargo) { this.hasCargo = hasCargo; }
    public boolean isCargoDeclared() { return cargoDeclared; }
    public void setCargoDeclared(boolean cargoDeclared) { this.cargoDeclared = cargoDeclared; }
    public BigDecimal getEstimatedCargoValue() { return estimatedCargoValue; }
    public void setEstimatedCargoValue(BigDecimal estimatedCargoValue) { this.estimatedCargoValue = estimatedCargoValue; }
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
    public List<VehicleCargo> getCargoItems() { return cargoItems; }
    public void setCargoItems(List<VehicleCargo> cargoItems) { this.cargoItems = cargoItems; }
}

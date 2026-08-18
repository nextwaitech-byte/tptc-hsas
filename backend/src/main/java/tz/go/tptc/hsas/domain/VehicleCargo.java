package tz.go.tptc.hsas.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "vehicle_cargo")
public class VehicleCargo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_crossing_id")
    private VehicleCrossing vehicleCrossing;

    @Column(name = "cargo_type", nullable = false, length = 40)
    private String cargoType;

    private String description;
    private BigDecimal quantity = BigDecimal.ZERO;
    private String unit = "kg";

    @Column(name = "estimated_value_tzs", nullable = false)
    private BigDecimal estimatedValueTzs = BigDecimal.ZERO;

    @Column(name = "is_hazardous", nullable = false)
    private boolean hazardous;

    @Column(name = "hazard_class", nullable = false, length = 40)
    private String hazardClass = "none";

    @Column(name = "requires_permit", nullable = false)
    private boolean requiresPermit;

    @Column(name = "permit_number")
    private String permitNumber;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public VehicleCrossing getVehicleCrossing() { return vehicleCrossing; }
    public void setVehicleCrossing(VehicleCrossing vehicleCrossing) { this.vehicleCrossing = vehicleCrossing; }
    public String getCargoType() { return cargoType; }
    public void setCargoType(String cargoType) { this.cargoType = cargoType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public BigDecimal getEstimatedValueTzs() { return estimatedValueTzs; }
    public void setEstimatedValueTzs(BigDecimal estimatedValueTzs) { this.estimatedValueTzs = estimatedValueTzs; }
    public boolean isHazardous() { return hazardous; }
    public void setHazardous(boolean hazardous) { this.hazardous = hazardous; }
    public String getHazardClass() { return hazardClass; }
    public void setHazardClass(String hazardClass) { this.hazardClass = hazardClass; }
    public boolean isRequiresPermit() { return requiresPermit; }
    public void setRequiresPermit(boolean requiresPermit) { this.requiresPermit = requiresPermit; }
    public String getPermitNumber() { return permitNumber; }
    public void setPermitNumber(String permitNumber) { this.permitNumber = permitNumber; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

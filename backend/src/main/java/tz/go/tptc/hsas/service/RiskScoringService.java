package tz.go.tptc.hsas.service;

import org.springframework.stereotype.Service;
import tz.go.tptc.hsas.domain.VehicleCargo;
import tz.go.tptc.hsas.repo.MovementRepository;
import tz.go.tptc.hsas.repo.SystemSettingRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
public class RiskScoringService {
    private final MovementRepository movements;
    private final SystemSettingRepository settings;

    public RiskScoringService(MovementRepository movements, SystemSettingRepository settings) {
        this.movements = movements;
        this.settings = settings;
    }

    public int scoreMovement(Map<String, Object> data, Long excludeId) {
        int score = 0;
        int windowDays = Integer.parseInt(settings.findBySettingKey("duplicate_window_days")
                .map(s -> s.getSettingValue()).orElse("30"));
        String documentType = str(data.get("documentType"), "none");
        if ("none".equals(documentType)) score += 30;
        String documentNumber = str(data.get("documentNumber"), "");
        if (documentNumber.isBlank()) score += 15;
        String nationality = str(data.get("nationality"), "");
        if (List.of("DRC", "Somalia", "Burundi").contains(nationality)) score += 10;
        String purpose = str(data.get("purpose"), "");
        if ("asylum_seeker".equals(purpose)) score += 10;
        int age = data.get("age") instanceof Number n ? n.intValue() : 0;
        int minors = data.get("accompaniedMinors") instanceof Number n ? n.intValue() : 0;
        if ("refugee".equals(purpose) && minors == 0 && age > 0 && age < 18) score += 25;
        if (!documentNumber.isBlank()) {
            long dups = movements.countByDocumentNumberAndDeletedAtIsNullAndIdNot(documentNumber, excludeId == null ? -1L : excludeId);
            if (dups > 0) score += 35;
        }
        String origin = str(data.get("originCountry"), "");
        if (!nationality.isBlank() && !origin.isBlank()) {
            Instant since = Instant.now().minus(windowDays, ChronoUnit.DAYS);
            long recent = movements.countByNationalityAndOriginCountryAndCrossedAtGreaterThanEqualAndDeletedAtIsNull(nationality, origin, since);
            if (recent >= 3) score += 15;
        }
        return Math.min(score, 100);
    }

    public static String ageGroup(Integer age) {
        if (age == null) return null;
        if (age < 18) return "child";
        if (age < 35) return "youth";
        if (age < 60) return "adult";
        return "elderly";
    }

    public int scoreVehicle(Map<String, Object> vehicle, List<Map<String, Object>> cargoItems) {
        int score = 0;
        String type = str(vehicle.get("vehicleType"), "");
        if (List.of("tanker", "trailer", "heavy_truck").contains(type)) score += 15;
        if (str(vehicle.get("registrationNumber"), "").isBlank()) score += 20;
        boolean hasCargo = bool(vehicle.get("hasCargo"));
        boolean declared = vehicle.get("cargoDeclared") == null || bool(vehicle.get("cargoDeclared"));
        if (hasCargo && !declared) score += 35;
        for (Map<String, Object> cargo : cargoItems) {
            String cargoType = str(cargo.get("cargoType"), "");
            boolean hazardous = bool(cargo.get("hazardous"));
            String hazard = str(cargo.get("hazardClass"), "none");
            boolean requiresPermit = bool(cargo.get("requiresPermit"));
            boolean hasPermit = !str(cargo.get("permitNumber"), "").isBlank();
            if ("weapons_ammunition".equals(cargoType)) score += 50;
            if ("fuel_petroleum".equals(cargoType)) score += 15;
            if ("chemicals".equals(cargoType)) score += 25;
            if ("minerals_ores".equals(cargoType)) score += 10;
            if (hazardous || !"none".equals(hazard)) {
                score += 20;
                if (List.of("explosive", "radioactive", "toxic").contains(hazard)) score += 15;
            }
            if (requiresPermit && !hasPermit) score += 30;
        }
        return Math.min(score, 100);
    }

    public String deriveVehicleStatus(int riskScore, List<Map<String, Object>> cargoItems) {
        for (Map<String, Object> cargo : cargoItems) {
            if ("weapons_ammunition".equals(str(cargo.get("cargoType"), ""))) return "held";
            if (bool(cargo.get("requiresPermit")) && str(cargo.get("permitNumber"), "").isBlank()) return "held";
        }
        return riskScore >= 70 ? "flagged" : "cleared";
    }

    public int scoreCargoEntities(List<VehicleCargo> items, String vehicleType, boolean hasCargo, boolean declared, String registration) {
        int score = 0;
        if (List.of("tanker", "trailer", "heavy_truck").contains(vehicleType)) score += 15;
        if (registration == null || registration.isBlank()) score += 20;
        if (hasCargo && !declared) score += 35;
        for (VehicleCargo cargo : items) {
            if ("weapons_ammunition".equals(cargo.getCargoType())) score += 50;
            if ("fuel_petroleum".equals(cargo.getCargoType())) score += 15;
            if ("chemicals".equals(cargo.getCargoType())) score += 25;
            if ("minerals_ores".equals(cargo.getCargoType())) score += 10;
            if (cargo.isHazardous() || !"none".equals(cargo.getHazardClass())) {
                score += 20;
                if (List.of("explosive", "radioactive", "toxic").contains(cargo.getHazardClass())) score += 15;
            }
            if (cargo.isRequiresPermit() && (cargo.getPermitNumber() == null || cargo.getPermitNumber().isBlank())) score += 30;
        }
        return Math.min(score, 100);
    }

    private static String str(Object v, String d) { return v == null ? d : String.valueOf(v); }
    private static boolean bool(Object v) {
        if (v instanceof Boolean b) return b;
        return "1".equals(String.valueOf(v)) || "true".equalsIgnoreCase(String.valueOf(v));
    }
}

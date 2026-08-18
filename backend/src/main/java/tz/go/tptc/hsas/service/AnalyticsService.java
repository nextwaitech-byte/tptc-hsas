package tz.go.tptc.hsas.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.go.tptc.hsas.domain.BorderPost;
import tz.go.tptc.hsas.domain.EconomicResource;
import tz.go.tptc.hsas.domain.IntelligenceReport;
import tz.go.tptc.hsas.domain.RefugeeCamp;
import tz.go.tptc.hsas.repo.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {
    private static final ZoneId ZONE = ZoneId.of("Africa/Dar_es_Salaam");
    private final MovementRepository movements;
    private final AlertRepository alerts;
    private final IncidentRepository incidents;
    private final VehicleCrossingRepository vehicles;
    private final VehicleCargoRepository cargo;
    private final EconomicResourceRepository economic;
    private final IntelligenceReportRepository intel;
    private final RefugeeCampRepository camps;
    private final BorderPostRepository posts;
    private final EarlyWarningIndicatorRepository indicators;

    public AnalyticsService(MovementRepository movements, AlertRepository alerts, IncidentRepository incidents,
                            VehicleCrossingRepository vehicles, VehicleCargoRepository cargo,
                            EconomicResourceRepository economic, IntelligenceReportRepository intel,
                            RefugeeCampRepository camps, BorderPostRepository posts,
                            EarlyWarningIndicatorRepository indicators) {
        this.movements = movements;
        this.alerts = alerts;
        this.incidents = incidents;
        this.vehicles = vehicles;
        this.cargo = cargo;
        this.economic = economic;
        this.intel = intel;
        this.camps = camps;
        this.posts = posts;
        this.indicators = indicators;
    }

    public Map<String, Object> kpis(Integer borderPostId) {
        Instant start = LocalDate.now(ZONE).atStartOfDay(ZONE).toInstant();
        Instant end = Instant.now();
        long entries = borderPostId == null
                ? movements.countByDirectionAndCrossedAtBetweenAndDeletedAtIsNull("entry", start, end)
                : movements.countByDirectionAndCrossedAtBetweenAndBorderPostIdAndDeletedAtIsNull("entry", start, end, borderPostId);
        long exits = borderPostId == null
                ? movements.countByDirectionAndCrossedAtBetweenAndDeletedAtIsNull("exit", start, end)
                : movements.countByDirectionAndCrossedAtBetweenAndBorderPostIdAndDeletedAtIsNull("exit", start, end, borderPostId);
        long openAlerts = borderPostId == null
                ? alerts.countByStatusIn(List.of("open", "acknowledged"))
                : alerts.countByStatusInAndBorderPostId(List.of("open", "acknowledged"), borderPostId);
        long openIncidents = borderPostId == null
                ? incidents.countByStatusIn(List.of("open", "investigating"))
                : incidents.countByStatusInAndBorderPostId(List.of("open", "investigating"), borderPostId);
        long flagged = borderPostId == null
                ? movements.countByStatusAndDeletedAtIsNull("flagged")
                : movements.countByStatusAndBorderPostIdAndDeletedAtIsNull("flagged", borderPostId);
        long vehiclesToday = borderPostId == null
                ? vehicles.countByCrossedAtBetweenAndDeletedAtIsNull(start, end)
                : vehicles.countByCrossedAtBetweenAndBorderPostIdAndDeletedAtIsNull(start, end, borderPostId);
        long undeclared = vehicles.countByHasCargoTrueAndCargoDeclaredFalseAndCrossedAtBetweenAndDeletedAtIsNull(start, end);
        long flaggedVehicles = vehicles.countByStatusInAndDeletedAtIsNull(List.of("flagged", "held"));
        long hazardousToday = cargo.findAll().stream()
                .filter(VehicleCargo -> VehicleCargo.isHazardous())
                .map(c -> c.getVehicleCrossing().getId())
                .distinct()
                .count();
        BigDecimal burden = economic.findAll().stream()
                .map(r -> r.getDailyCostPerCapita().multiply(BigDecimal.valueOf(r.getRefugeePopulation())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("entries_today", entries);
        map.put("exits_today", exits);
        map.put("net_inflow", entries - exits);
        map.put("open_alerts", openAlerts);
        map.put("open_incidents", openIncidents);
        map.put("flagged_count", flagged);
        map.put("economic_burden", burden);
        map.put("vehicles_today", vehiclesToday);
        map.put("hazardous_cargo_today", hazardousToday);
        map.put("undeclared_cargo_today", undeclared);
        map.put("flagged_vehicles", flaggedVehicles);
        map.put("updated_at", Instant.now().toString());
        return map;
    }

    public Map<String, Object> cop(Integer borderPostId) {
        List<String> open = List.of("open", "monitoring");
        long openIntel = intel.countByStatusIn(open);
        long criticalIntel = intel.countByStatusInAndSeverity(open, "critical");
        Map<String, Long> byDomain = new LinkedHashMap<>();
        for (String d : List.of("security", "health", "humanitarian", "economic", "environmental", "infrastructure", "logistics", "population")) {
            byDomain.put(d, intel.countByDomainAndStatusIn(d, open));
        }
        String threat = nationalThreat(criticalIntel, openIntel, byDomain);
        List<RefugeeCamp> campList = camps.findByActiveTrue();
        int totalRefugees = campList.stream().mapToInt(RefugeeCamp::getCurrentPopulation).sum();
        Map<String, Integer> byRegion = new LinkedHashMap<>();
        campList.forEach(c -> byRegion.merge(c.getRegion(), c.getCurrentPopulation(), Integer::sum));
        Instant since30 = Instant.now().minus(30, ChronoUnit.DAYS);
        long refugeeEntries = movements.countByPurposeInAndDirectionAndCrossedAtGreaterThanEqualAndDeletedAtIsNull(
                List.of("refugee", "asylum_seeker"), "entry", since30);
        long disease = intel.countByDomainAndCategoryInAndStatusIn("health", List.of("disease_outbreak", "infected_patients"), open);
        long borderIncidents = incidents.countByStatusIn(List.of("open", "investigating"));
        long infra = intel.countByDomainAndStatusIn("infrastructure", open);
        long env = intel.countByDomainAndStatusInAndSeverityIn("environmental", open, List.of("high", "critical"));
        long shortages = intel.countByDomainInAndCategoryInAndStatusIn(
                List.of("logistics", "humanitarian", "economic"),
                List.of("food_stock", "food_availability", "food_shortage", "medical_supplies", "fuel_stock", "fuel_availability"),
                open);
        List<Map<String, Object>> actions = intel.findAllDetailed().stream()
                .filter(r -> open.contains(r.getStatus()) && r.getRecommendedAction() != null && !r.getRecommendedAction().isBlank())
                .sorted(Comparator.comparingInt(r -> severityRank(r.getSeverity())))
                .limit(6)
                .map(r -> Map.<String, Object>of(
                        "title", r.getTitle(),
                        "recommended_action", r.getRecommendedAction(),
                        "severity", r.getSeverity(),
                        "domain", r.getDomain()))
                .toList();
        Map<String, Object> domainThreats = new LinkedHashMap<>();
        for (String d : List.of("security", "health", "humanitarian", "economic", "environmental", "infrastructure", "logistics")) {
            long count = byDomain.getOrDefault(d, 0L);
            long crit = intel.countByDomainAndSeverityAndStatusIn(d, "critical", open);
            String level = crit > 0 ? "red" : (count > 2 ? "orange" : (count > 0 ? "yellow" : "green"));
            domainThreats.put(d, Map.of("open_count", count, "critical_count", crit, "level", level));
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("threat_level", threat);
        map.put("threat_label", threat.toUpperCase());
        map.put("open_intelligence", openIntel);
        map.put("critical_intelligence", criticalIntel);
        map.put("refugee_population", totalRefugees);
        map.put("refugee_by_region", byRegion.entrySet().stream()
                .map(e -> Map.of("region", e.getKey(), "population", e.getValue())).toList());
        map.put("refugee_entries_30d", refugeeEntries);
        map.put("disease_outbreak_alerts", disease);
        map.put("border_incidents", borderIncidents);
        map.put("humanitarian_situation", byDomain.getOrDefault("humanitarian", 0L));
        map.put("infrastructure_disruptions", infra);
        map.put("weather_hazards", env);
        map.put("resource_shortages", shortages);
        map.put("by_domain", byDomain);
        map.put("domain_threats", domainThreats);
        map.put("recent_intelligence", intel.findAllDetailed().stream().limit(8).map(this::intelSummary).toList());
        map.put("recommended_actions", actions);
        map.put("updated_at", Instant.now().toString());
        map.put("kpis", kpis(borderPostId));
        return map;
    }

    public Map<String, Object> live(Integer borderPostId) {
        Map<String, Object> live = new LinkedHashMap<>();
        live.put("kpis", kpis(borderPostId));
        live.put("cop", cop(borderPostId));
        live.put("trends", trends(30, borderPostId));
        live.put("heatmap", heatmap());
        return live;
    }

    public Map<String, Object> trends(int days, Integer borderPostId) {
        List<Map<String, Object>> daily = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate day = LocalDate.now(ZONE).minusDays(i);
            Instant from = day.atStartOfDay(ZONE).toInstant();
            Instant to = day.plusDays(1).atStartOfDay(ZONE).toInstant();
            long entries = borderPostId == null
                    ? movements.countByDirectionAndCrossedAtBetweenAndDeletedAtIsNull("entry", from, to)
                    : movements.countByDirectionAndCrossedAtBetweenAndBorderPostIdAndDeletedAtIsNull("entry", from, to, borderPostId);
            long exits = borderPostId == null
                    ? movements.countByDirectionAndCrossedAtBetweenAndDeletedAtIsNull("exit", from, to)
                    : movements.countByDirectionAndCrossedAtBetweenAndBorderPostIdAndDeletedAtIsNull("exit", from, to, borderPostId);
            daily.add(Map.of("date", day.toString(), "entries", entries, "exits", exits));
        }
        return Map.of("daily", daily);
    }

    public List<Map<String, Object>> heatmap() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (BorderPost post : posts.findByActiveTrueAndDeletedAtIsNullOrderByNameAsc()) {
            long inc = incidents.countByBorderPostIdAndStatusIn(post.getId(), List.of("open", "investigating"));
            long intelCount = intel.countByBorderPostIdAndStatusIn(post.getId(), List.of("open", "monitoring"));
            long highRisk = movements.countByRiskScoreGreaterThanEqualAndDeletedAtIsNullAndBorderPostId(70, post.getId());
            int score = (int) Math.min(100, inc * 15 + intelCount * 10 + highRisk * 5);
            String level = score >= 50 ? "high" : (score >= 20 ? "medium" : "low");
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("border_post_id", post.getId());
            row.put("name", post.getName());
            row.put("code", post.getCode());
            row.put("latitude", post.getLatitude());
            row.put("longitude", post.getLongitude());
            row.put("region", post.getRegion());
            row.put("risk_score", score);
            row.put("level", level);
            row.put("open_incidents", inc);
            row.put("open_intel", intelCount);
            list.add(row);
        }
        return list;
    }

    public Map<String, Object> gis() {
        return Map.of(
                "posts", heatmap(),
                "camps", camps.findByActiveTrue().stream().map(c -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", c.getId());
                    m.put("name", c.getName());
                    m.put("region", c.getRegion());
                    m.put("district", c.getDistrict());
                    m.put("capacity", c.getCapacity());
                    m.put("current_population", c.getCurrentPopulation());
                    m.put("latitude", c.getLatitude());
                    m.put("longitude", c.getLongitude());
                    return m;
                }).toList(),
                "intel", intel.findAllDetailed().stream()
                        .filter(r -> r.getLatitude() != null && r.getLongitude() != null)
                        .map(this::intelSummary).toList()
        );
    }

    public List<Map<String, Object>> earlyWarning() {
        return indicators.findByActiveTrueOrderByIdAsc().stream().map(i -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", i.getId());
            m.put("indicator_code", i.getIndicatorCode());
            m.put("name", i.getName());
            m.put("domain", i.getDomain());
            m.put("description", i.getDescription());
            m.put("threshold_value", i.getThresholdValue());
            m.put("threshold_unit", i.getThresholdUnit());
            m.put("current_value", i.getCurrentValue());
            m.put("escalation_level", i.getEscalationLevel());
            m.put("geographical_scope", i.getGeographicalScope());
            m.put("ai_risk_score", i.getAiRiskScore());
            m.put("probability_pct", i.getProbabilityPct());
            return m;
        }).toList();
    }

    public Map<String, Object> dailyReport() {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("generated_at", Instant.now().toString());
        report.put("kpis", kpis(null));
        report.put("cop", cop(null));
        report.put("movements", movements.findAllActive().stream().limit(25).map(this::movementSummary).toList());
        report.put("incidents", incidents.findAllDetailed().stream().limit(15).map(this::incidentSummary).toList());
        report.put("alerts", alerts.findAllDetailed().stream().limit(15).map(this::alertSummary).toList());
        return report;
    }

    private String nationalThreat(long critical, long open, Map<String, Long> byDomain) {
        long securityOpen = byDomain.getOrDefault("security", 0L);
        if (critical >= 3 || securityOpen >= 5) return "red";
        if (critical >= 1 || open >= 8) return "orange";
        if (open >= 3) return "yellow";
        return "green";
    }

    private int severityRank(String s) {
        return switch (s) {
            case "critical" -> 0;
            case "high" -> 1;
            case "medium" -> 2;
            default -> 3;
        };
    }

    public Map<String, Object> intelSummary(IntelligenceReport r) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", r.getId());
        m.put("report_code", r.getReportCode());
        m.put("domain", r.getDomain());
        m.put("category", r.getCategory());
        m.put("title", r.getTitle());
        m.put("description", r.getDescription());
        m.put("severity", r.getSeverity());
        m.put("threat_level", r.getThreatLevel());
        m.put("status", r.getStatus());
        m.put("region", r.getRegion());
        m.put("district", r.getDistrict());
        m.put("camp_name", r.getCampName());
        m.put("reported_at", r.getReportedAt());
        m.put("metric_value", r.getMetricValue());
        m.put("metric_unit", r.getMetricUnit());
        m.put("metric_label", r.getMetricLabel());
        m.put("responsible_agency", r.getResponsibleAgency());
        m.put("recommended_action", r.getRecommendedAction());
        m.put("actions_taken", r.getActionsTaken());
        m.put("resources_required", r.getResourcesRequired());
        m.put("latitude", r.getLatitude());
        m.put("longitude", r.getLongitude());
        m.put("border_post_id", r.getBorderPost() != null ? r.getBorderPost().getId() : null);
        m.put("border_post_name", r.getBorderPost() != null ? r.getBorderPost().getName() : null);
        m.put("reported_by_name", r.getReportedBy() != null ? r.getReportedBy().getName() : null);
        return m;
    }

    public Map<String, Object> movementSummary(tz.go.tptc.hsas.domain.Movement mvt) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", mvt.getId());
        m.put("record_code", mvt.getRecordCode());
        m.put("direction", mvt.getDirection());
        m.put("crossed_at", mvt.getCrossedAt());
        m.put("nationality", mvt.getNationality());
        m.put("sex", mvt.getSex());
        m.put("age", mvt.getAge());
        m.put("age_group", mvt.getAgeGroup());
        m.put("purpose", mvt.getPurpose());
        m.put("document_type", mvt.getDocumentType());
        m.put("document_number", mvt.getDocumentNumber());
        m.put("origin_country", mvt.getOriginCountry());
        m.put("destination_region", mvt.getDestinationRegion());
        m.put("destination_district", mvt.getDestinationDistrict());
        m.put("accompanied_minors", mvt.getAccompaniedMinors());
        m.put("status", mvt.getStatus());
        m.put("risk_score", mvt.getRiskScore());
        m.put("remarks", mvt.getRemarks());
        m.put("border_post_id", mvt.getBorderPost().getId());
        m.put("border_post_name", mvt.getBorderPost().getName());
        m.put("officer_name", mvt.getOfficer().getName());
        return m;
    }

    public Map<String, Object> incidentSummary(tz.go.tptc.hsas.domain.Incident i) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", i.getId());
        m.put("incident_code", i.getIncidentCode());
        m.put("incident_type", i.getIncidentType());
        m.put("severity", i.getSeverity());
        m.put("description", i.getDescription());
        m.put("status", i.getStatus());
        m.put("occurred_at", i.getOccurredAt());
        m.put("responsible_agency", i.getResponsibleAgency());
        m.put("actions_taken", i.getActionsTaken());
        m.put("resources_required", i.getResourcesRequired());
        m.put("latitude", i.getLatitude());
        m.put("longitude", i.getLongitude());
        m.put("border_post_id", i.getBorderPost().getId());
        m.put("border_post_name", i.getBorderPost().getName());
        m.put("reported_by_name", i.getReportedBy().getName());
        return m;
    }

    public Map<String, Object> alertSummary(tz.go.tptc.hsas.domain.Alert a) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", a.getId());
        m.put("alert_code", a.getAlertCode());
        m.put("alert_type", a.getAlertType());
        m.put("severity", a.getSeverity());
        m.put("title", a.getTitle());
        m.put("message", a.getMessage());
        m.put("status", a.getStatus());
        m.put("created_at", a.getCreatedAt());
        m.put("border_post_id", a.getBorderPost() != null ? a.getBorderPost().getId() : null);
        m.put("border_post_name", a.getBorderPost() != null ? a.getBorderPost().getName() : null);
        return m;
    }
}

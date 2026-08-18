package tz.go.tptc.hsas.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tz.go.tptc.hsas.catalog.Catalog;
import tz.go.tptc.hsas.domain.*;
import tz.go.tptc.hsas.repo.*;
import tz.go.tptc.hsas.security.AuthPrincipal;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
public class RecordService {
    private final MovementRepository movements;
    private final VehicleCrossingRepository vehicles;
    private final IncidentRepository incidents;
    private final AlertRepository alerts;
    private final IntelligenceReportRepository intel;
    private final CommunityReportRepository community;
    private final InstitutionalCapacityRepository capacity;
    private final PublicCommunicationRepository comms;
    private final UserRepository users;
    private final RoleRepository roles;
    private final BorderPostRepository posts;
    private final RefugeeCampRepository camps;
    private final RiskScoringService risk;
    private final AnalyticsService analytics;
    private final PasswordEncoder encoder;

    public RecordService(MovementRepository movements, VehicleCrossingRepository vehicles, IncidentRepository incidents,
                         AlertRepository alerts, IntelligenceReportRepository intel, CommunityReportRepository community,
                         InstitutionalCapacityRepository capacity, PublicCommunicationRepository comms,
                         UserRepository users, RoleRepository roles, BorderPostRepository posts,
                         RefugeeCampRepository camps, RiskScoringService risk, AnalyticsService analytics,
                         PasswordEncoder encoder) {
        this.movements = movements;
        this.vehicles = vehicles;
        this.incidents = incidents;
        this.alerts = alerts;
        this.intel = intel;
        this.community = community;
        this.capacity = capacity;
        this.comms = comms;
        this.users = users;
        this.roles = roles;
        this.posts = posts;
        this.camps = camps;
        this.risk = risk;
        this.analytics = analytics;
        this.encoder = encoder;
    }

    public Map<String, Object> lookups() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("border_posts", posts.findByActiveTrueAndDeletedAtIsNullOrderByNameAsc().stream().map(p -> Map.of(
                "id", p.getId(), "name", p.getName(), "code", p.getCode(), "region", p.getRegion(),
                "district", p.getDistrict(), "latitude", p.getLatitude(), "longitude", p.getLongitude(),
                "capacity", p.getCapacity()
        )).toList());
        m.put("camps", camps.findByActiveTrue().stream().map(c -> Map.of(
                "id", c.getId(), "name", c.getName(), "region", c.getRegion(), "district", c.getDistrict(),
                "capacity", c.getCapacity(), "current_population", c.getCurrentPopulation()
        )).toList());
        m.put("roles", roles.findAll().stream().map(r -> Map.of("id", r.getId(), "name", r.getName(), "label", r.getLabel())).toList());
        m.put("domains", Catalog.domains());
        m.put("categories", Catalog.categories());
        m.put("modules", Catalog.modules());
        return m;
    }

    public List<Map<String, Object>> movements() {
        return movements.findAllActive().stream().map(analytics::movementSummary).toList();
    }

    public Map<String, Object> movement(Long id) {
        return analytics.movementSummary(movements.findById(id).orElseThrow(() -> notFound("Movement")));
    }

    public Map<String, Object> createMovement(Map<String, Object> body, AuthPrincipal actor) {
        Movement m = new Movement();
        applyMovement(m, body, actor, null);
        m.setRecordCode(code("MOV"));
        m.setCreatedAt(Instant.now());
        movements.save(m);
        maybeAlertForMovement(m);
        return analytics.movementSummary(m);
    }

    public Map<String, Object> updateMovement(Long id, Map<String, Object> body, AuthPrincipal actor) {
        Movement m = movements.findById(id).orElseThrow(() -> notFound("Movement"));
        applyMovement(m, body, actor, id);
        m.setUpdatedAt(Instant.now());
        movements.save(m);
        return analytics.movementSummary(m);
    }

    private void applyMovement(Movement m, Map<String, Object> body, AuthPrincipal actor, Long excludeId) {
        m.setBorderPost(post(num(body.get("borderPostId"), actor.getBorderPostId())));
        m.setDirection(str(body.get("direction"), "entry"));
        m.setCrossedAt(instant(body.get("crossedAt"), Instant.now()));
        m.setNationality(str(body.get("nationality"), ""));
        m.setSex(str(body.get("sex"), "other"));
        Integer age = body.get("age") instanceof Number n ? n.intValue() : null;
        m.setAge(age);
        m.setAgeGroup(RiskScoringService.ageGroup(age));
        m.setPurpose(str(body.get("purpose"), "other"));
        m.setDocumentType(str(body.get("documentType"), "none"));
        m.setDocumentNumber(str(body.get("documentNumber"), null));
        m.setOriginCountry(str(body.get("originCountry"), ""));
        m.setDestinationRegion(str(body.get("destinationRegion"), null));
        m.setDestinationDistrict(str(body.get("destinationDistrict"), null));
        m.setAccompaniedMinors(body.get("accompaniedMinors") instanceof Number n ? n.intValue() : 0);
        m.setHasBiometric(bool(body.get("hasBiometric")));
        m.setRemarks(str(body.get("remarks"), null));
        m.setOfficer(users.findById(actor.getId()).orElseThrow());
        int score = risk.scoreMovement(body, excludeId);
        m.setRiskScore(score);
        m.setStatus(score >= 70 ? "flagged" : "verified");
    }

    private void maybeAlertForMovement(Movement m) {
        if (m.getRiskScore() >= 70) {
            Alert a = new Alert();
            a.setAlertCode(code("ALT"));
            a.setAlertType("high_risk_score");
            a.setSeverity(m.getRiskScore() >= 80 ? "critical" : "high");
            a.setTitle("High Risk Individual");
            a.setMessage("Movement " + m.getRecordCode() + " has risk score " + m.getRiskScore());
            a.setMovement(m);
            a.setBorderPost(m.getBorderPost());
            a.setStatus("open");
            alerts.save(a);
        }
    }

    public List<Map<String, Object>> vehicles() {
        return vehicles.findAllActive().stream().map(this::vehicleSummary).toList();
    }

    public Map<String, Object> vehicle(Long id) {
        return vehicleSummary(vehicles.findById(id).orElseThrow(() -> notFound("Vehicle")));
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> createVehicle(Map<String, Object> body, AuthPrincipal actor) {
        VehicleCrossing v = new VehicleCrossing();
        v.setRecordCode(code("VEH"));
        v.setBorderPost(post(num(body.get("borderPostId"), actor.getBorderPostId())));
        v.setDirection(str(body.get("direction"), "entry"));
        v.setCrossedAt(instant(body.get("crossedAt"), Instant.now()));
        v.setVehicleType(str(body.get("vehicleType"), "other"));
        v.setRegistrationNumber(str(body.get("registrationNumber"), ""));
        v.setRegistrationCountry(str(body.get("registrationCountry"), ""));
        v.setDriverName(str(body.get("driverName"), ""));
        v.setDriverNationality(str(body.get("driverNationality"), ""));
        v.setDriverDocument(str(body.get("driverDocument"), null));
        v.setPassengersCount(body.get("passengersCount") instanceof Number n ? n.intValue() : 0);
        v.setHasCargo(bool(body.get("hasCargo")));
        v.setCargoDeclared(body.get("cargoDeclared") == null || bool(body.get("cargoDeclared")));
        v.setEstimatedCargoValue(dec(body.get("estimatedCargoValue")));
        v.setRemarks(str(body.get("remarks"), null));
        v.setOfficer(users.findById(actor.getId()).orElseThrow());
        List<Map<String, Object>> cargoBody = body.get("cargo") instanceof List<?> list
                ? (List<Map<String, Object>>) list : List.of();
        int score = risk.scoreVehicle(body, cargoBody);
        v.setRiskScore(score);
        v.setStatus(risk.deriveVehicleStatus(score, cargoBody));
        for (Map<String, Object> c : cargoBody) {
            VehicleCargo cargo = new VehicleCargo();
            cargo.setVehicleCrossing(v);
            cargo.setCargoType(str(c.get("cargoType"), "other"));
            cargo.setDescription(str(c.get("description"), null));
            cargo.setQuantity(dec(c.get("quantity")));
            cargo.setUnit(str(c.get("unit"), "kg"));
            cargo.setEstimatedValueTzs(dec(c.get("estimatedValueTzs")));
            cargo.setHazardous(bool(c.get("hazardous")));
            cargo.setHazardClass(str(c.get("hazardClass"), "none"));
            cargo.setRequiresPermit(bool(c.get("requiresPermit")));
            cargo.setPermitNumber(str(c.get("permitNumber"), null));
            v.getCargoItems().add(cargo);
        }
        vehicles.save(v);
        return vehicleSummary(v);
    }

    public List<Map<String, Object>> incidents() {
        return incidents.findAllDetailed().stream().map(analytics::incidentSummary).toList();
    }

    public Map<String, Object> createIncident(Map<String, Object> body, AuthPrincipal actor) {
        Incident i = new Incident();
        i.setIncidentCode(code("INC"));
        i.setBorderPost(post(num(body.get("borderPostId"), actor.getBorderPostId())));
        i.setIncidentType(str(body.get("incidentType"), "other"));
        i.setSeverity(str(body.get("severity"), "medium"));
        i.setDescription(str(body.get("description"), ""));
        i.setResponsibleAgency(str(body.get("responsibleAgency"), null));
        i.setActionsTaken(str(body.get("actionsTaken"), null));
        i.setResourcesRequired(str(body.get("resourcesRequired"), null));
        i.setLatitude(decOrNull(body.get("latitude")));
        i.setLongitude(decOrNull(body.get("longitude")));
        i.setOccurredAt(instant(body.get("occurredAt"), Instant.now()));
        i.setReportedBy(users.findById(actor.getId()).orElseThrow());
        i.setStatus("open");
        incidents.save(i);
        return analytics.incidentSummary(i);
    }

    public Map<String, Object> updateIncidentStatus(Integer id, String status) {
        Incident i = incidents.findById(id).orElseThrow(() -> notFound("Incident"));
        i.setStatus(status);
        if ("resolved".equals(status) || "closed".equals(status)) i.setResolvedAt(Instant.now());
        return analytics.incidentSummary(incidents.save(i));
    }

    public List<Map<String, Object>> alerts() {
        return alerts.findAllDetailed().stream().map(analytics::alertSummary).toList();
    }

    public Map<String, Object> acknowledgeAlert(Integer id, AuthPrincipal actor) {
        Alert a = alerts.findById(id).orElseThrow(() -> notFound("Alert"));
        a.setStatus("acknowledged");
        a.setAcknowledgedAt(Instant.now());
        a.setAcknowledgedBy(users.findById(actor.getId()).orElseThrow());
        return analytics.alertSummary(alerts.save(a));
    }

    public Map<String, Object> resolveAlert(Integer id, AuthPrincipal actor) {
        Alert a = alerts.findById(id).orElseThrow(() -> notFound("Alert"));
        a.setStatus("resolved");
        a.setResolvedAt(Instant.now());
        a.setResolvedBy(users.findById(actor.getId()).orElseThrow());
        return analytics.alertSummary(alerts.save(a));
    }

    public List<Map<String, Object>> intelligence(String domain) {
        List<IntelligenceReport> list = domain == null || domain.isBlank()
                ? intel.findAllDetailed()
                : intel.findByDomainOrderByReportedAtDesc(domain);
        return list.stream().map(analytics::intelSummary).toList();
    }

    public Map<String, Object> intelligenceOne(Integer id) {
        return analytics.intelSummary(intel.findById(id).orElseThrow(() -> notFound("Report")));
    }

    public Map<String, Object> createIntelligence(Map<String, Object> body, AuthPrincipal actor) {
        if (actor.isSuperAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "HQ admin is view/analysis only.");
        }
        IntelligenceReport r = new IntelligenceReport();
        r.setReportCode(code("INT"));
        r.setDomain(str(body.get("domain"), "security"));
        r.setCategory(str(body.get("category"), "other"));
        if (body.get("borderPostId") != null) r.setBorderPost(post(num(body.get("borderPostId"), null)));
        r.setRegion(str(body.get("region"), null));
        r.setDistrict(str(body.get("district"), null));
        r.setCampName(str(body.get("campName"), null));
        r.setReportedAt(instant(body.get("reportedAt"), Instant.now()));
        r.setSeverity(str(body.get("severity"), "medium"));
        r.setThreatLevel(str(body.get("threatLevel"), threatFromSeverity(r.getSeverity())));
        r.setTitle(str(body.get("title"), ""));
        r.setDescription(str(body.get("description"), ""));
        r.setMetricValue(decOrNull(body.get("metricValue")));
        r.setMetricUnit(str(body.get("metricUnit"), null));
        r.setMetricLabel(str(body.get("metricLabel"), null));
        r.setResponsibleAgency(str(body.get("responsibleAgency"), null));
        r.setRecommendedAction(str(body.get("recommendedAction"), null));
        r.setActionsTaken(str(body.get("actionsTaken"), null));
        r.setResourcesRequired(str(body.get("resourcesRequired"), null));
        r.setLatitude(decOrNull(body.get("latitude")));
        r.setLongitude(decOrNull(body.get("longitude")));
        r.setReportedBy(users.findById(actor.getId()).orElseThrow());
        r.setStatus("open");
        intel.save(r);
        return analytics.intelSummary(r);
    }

    public Map<String, Object> updateIntelStatus(Integer id, String status) {
        IntelligenceReport r = intel.findById(id).orElseThrow(() -> notFound("Report"));
        r.setStatus(status);
        r.setUpdatedAt(Instant.now());
        return analytics.intelSummary(intel.save(r));
    }

    public List<Map<String, Object>> community() {
        return community.findAllDetailed().stream().map(this::communitySummary).toList();
    }

    public Map<String, Object> createCommunity(Map<String, Object> body, AuthPrincipal actor) {
        CommunityReport r = new CommunityReport();
        r.setReportCode(code("COM"));
        r.setSourceType(str(body.get("sourceType"), "community_member"));
        r.setReporterName(str(body.get("reporterName"), null));
        r.setAnonymous(bool(body.get("anonymous")));
        r.setTitle(str(body.get("title"), ""));
        r.setDescription(str(body.get("description"), ""));
        r.setCategory(str(body.get("category"), null));
        r.setRegion(str(body.get("region"), null));
        r.setDistrict(str(body.get("district"), null));
        r.setVillage(str(body.get("village"), null));
        r.setReportedAt(instant(body.get("reportedAt"), Instant.now()));
        r.setCreatedBy(users.findById(actor.getId()).orElseThrow());
        community.save(r);
        return communitySummary(r);
    }

    public Map<String, Object> updateCommunityStatus(Integer id, String verification, String response) {
        CommunityReport r = community.findById(id).orElseThrow(() -> notFound("Community report"));
        if (verification != null) r.setVerificationStatus(verification);
        if (response != null) r.setResponseStatus(response);
        return communitySummary(community.save(r));
    }

    public List<Map<String, Object>> capacity() {
        return capacity.findAllDetailed().stream().map(this::capacitySummary).toList();
    }

    public Map<String, Object> createCapacity(Map<String, Object> body, AuthPrincipal actor) {
        InstitutionalCapacity c = new InstitutionalCapacity();
        c.setRecordCode(code("CAP"));
        c.setInstitutionName(str(body.get("institutionName"), ""));
        c.setInstitutionType(str(body.get("institutionType"), "other"));
        c.setRegion(str(body.get("region"), null));
        c.setDistrict(str(body.get("district"), null));
        if (body.get("borderPostId") != null) c.setBorderPost(post(num(body.get("borderPostId"), null)));
        c.setPersonnelCount(intVal(body.get("personnelCount")));
        c.setEmergencyTeams(intVal(body.get("emergencyTeams")));
        c.setVehicles(intVal(body.get("vehicles")));
        c.setBoats(intVal(body.get("boats")));
        c.setCommunicationEquipment(intVal(body.get("communicationEquipment")));
        c.setEmergencySupplies(str(body.get("emergencySupplies"), null));
        c.setAvailableBudget(decOrNull(body.get("availableBudget")));
        c.setFocalPerson(str(body.get("focalPerson"), null));
        c.setFocalContact(str(body.get("focalContact"), null));
        c.setReadinessLevel(str(body.get("readinessLevel"), "medium"));
        c.setNotes(str(body.get("notes"), null));
        c.setRecordedAt(instant(body.get("recordedAt"), Instant.now()));
        c.setCreatedBy(users.findById(actor.getId()).orElseThrow());
        capacity.save(c);
        return capacitySummary(c);
    }

    public List<Map<String, Object>> communications() {
        return comms.findAllByOrderByCreatedAtDesc().stream().map(this::commSummary).toList();
    }

    public Map<String, Object> createCommunication(Map<String, Object> body, AuthPrincipal actor) {
        PublicCommunication c = new PublicCommunication();
        c.setMessageCode(code("MSG"));
        c.setMessageType(str(body.get("messageType"), "public_advisory"));
        c.setTitle(str(body.get("title"), ""));
        c.setBody(str(body.get("body"), ""));
        c.setChannel(str(body.get("channel"), "sms"));
        c.setTargetRegion(str(body.get("targetRegion"), null));
        c.setTargetAudience(str(body.get("targetAudience"), null));
        c.setRecipientsCount(intVal(body.get("recipientsCount")));
        c.setStatus(str(body.get("status"), "draft"));
        if ("sent".equals(c.getStatus())) c.setSentAt(Instant.now());
        c.setCreatedBy(users.findById(actor.getId()).orElseThrow());
        comms.save(c);
        return commSummary(c);
    }

    public List<Map<String, Object>> users() {
        return users.findByActiveTrueAndDeletedAtIsNullOrderByIdAsc().stream().map(this::userSummary).toList();
    }

    public Map<String, Object> createUser(Map<String, Object> body, AuthPrincipal actor) {
        User u = new User();
        u.setName(str(body.get("name"), ""));
        u.setEmail(str(body.get("email"), ""));
        u.setPhone(str(body.get("phone"), null));
        u.setPassword(encoder.encode(str(body.get("password"), "Admin@123")));
        u.setRole(roles.findById(Short.valueOf(String.valueOf(body.get("roleId")))).orElseThrow(() -> notFound("Role")));
        if (body.get("borderPostId") != null) u.setBorderPost(post(num(body.get("borderPostId"), null)));
        u.setActive(true);
        users.save(u);
        return userSummary(u);
    }

    public Map<String, Object> updateUser(Integer id, Map<String, Object> body) {
        User u = users.findById(id).orElseThrow(() -> notFound("User"));
        if (body.get("name") != null) u.setName(str(body.get("name"), u.getName()));
        if (body.get("phone") != null) u.setPhone(str(body.get("phone"), null));
        if (body.get("roleId") != null) u.setRole(roles.findById(Short.valueOf(String.valueOf(body.get("roleId")))).orElseThrow());
        if (body.containsKey("borderPostId")) {
            u.setBorderPost(body.get("borderPostId") == null ? null : post(num(body.get("borderPostId"), null)));
        }
        if (body.get("password") != null && !String.valueOf(body.get("password")).isBlank()) {
            u.setPassword(encoder.encode(String.valueOf(body.get("password"))));
        }
        if (body.get("active") != null) u.setActive(bool(body.get("active")));
        u.setUpdatedAt(Instant.now());
        return userSummary(users.save(u));
    }

    private Map<String, Object> vehicleSummary(VehicleCrossing v) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", v.getId());
        m.put("record_code", v.getRecordCode());
        m.put("direction", v.getDirection());
        m.put("crossed_at", v.getCrossedAt());
        m.put("vehicle_type", v.getVehicleType());
        m.put("registration_number", v.getRegistrationNumber());
        m.put("registration_country", v.getRegistrationCountry());
        m.put("driver_name", v.getDriverName());
        m.put("driver_nationality", v.getDriverNationality());
        m.put("passengers_count", v.getPassengersCount());
        m.put("has_cargo", v.isHasCargo());
        m.put("cargo_declared", v.isCargoDeclared());
        m.put("estimated_cargo_value", v.getEstimatedCargoValue());
        m.put("status", v.getStatus());
        m.put("risk_score", v.getRiskScore());
        m.put("remarks", v.getRemarks());
        m.put("border_post_id", v.getBorderPost().getId());
        m.put("border_post_name", v.getBorderPost().getName());
        m.put("officer_name", v.getOfficer().getName());
        m.put("cargo", v.getCargoItems().stream().map(c -> {
            Map<String, Object> cm = new LinkedHashMap<>();
            cm.put("cargo_type", c.getCargoType());
            cm.put("description", c.getDescription());
            cm.put("quantity", c.getQuantity());
            cm.put("unit", c.getUnit());
            cm.put("is_hazardous", c.isHazardous());
            cm.put("hazard_class", c.getHazardClass());
            cm.put("requires_permit", c.isRequiresPermit());
            cm.put("permit_number", c.getPermitNumber());
            return cm;
        }).toList());
        return m;
    }

    private Map<String, Object> communitySummary(CommunityReport r) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", r.getId());
        m.put("report_code", r.getReportCode());
        m.put("source_type", r.getSourceType());
        m.put("reporter_name", r.isAnonymous() ? "Anonymous" : r.getReporterName());
        m.put("is_anonymous", r.isAnonymous());
        m.put("title", r.getTitle());
        m.put("description", r.getDescription());
        m.put("category", r.getCategory());
        m.put("region", r.getRegion());
        m.put("district", r.getDistrict());
        m.put("village", r.getVillage());
        m.put("verification_status", r.getVerificationStatus());
        m.put("response_status", r.getResponseStatus());
        m.put("reported_at", r.getReportedAt());
        return m;
    }

    private Map<String, Object> capacitySummary(InstitutionalCapacity c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", c.getId());
        m.put("record_code", c.getRecordCode());
        m.put("institution_name", c.getInstitutionName());
        m.put("institution_type", c.getInstitutionType());
        m.put("region", c.getRegion());
        m.put("district", c.getDistrict());
        m.put("personnel_count", c.getPersonnelCount());
        m.put("emergency_teams", c.getEmergencyTeams());
        m.put("vehicles", c.getVehicles());
        m.put("boats", c.getBoats());
        m.put("readiness_level", c.getReadinessLevel());
        m.put("focal_person", c.getFocalPerson());
        m.put("recorded_at", c.getRecordedAt());
        return m;
    }

    private Map<String, Object> commSummary(PublicCommunication c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", c.getId());
        m.put("message_code", c.getMessageCode());
        m.put("message_type", c.getMessageType());
        m.put("title", c.getTitle());
        m.put("body", c.getBody());
        m.put("channel", c.getChannel());
        m.put("target_region", c.getTargetRegion());
        m.put("status", c.getStatus());
        m.put("sent_at", c.getSentAt());
        m.put("recipients_count", c.getRecipientsCount());
        return m;
    }

    private Map<String, Object> userSummary(User u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", u.getId());
        m.put("name", u.getName());
        m.put("email", u.getEmail());
        m.put("phone", u.getPhone());
        m.put("role_id", u.getRole().getId());
        m.put("role_name", u.getRole().getName());
        m.put("role_label", u.getRole().getLabel());
        m.put("border_post_id", u.getBorderPost() != null ? u.getBorderPost().getId() : null);
        m.put("border_post_name", u.getBorderPost() != null ? u.getBorderPost().getName() : null);
        m.put("is_active", u.isActive());
        m.put("last_login", u.getLastLogin());
        return m;
    }

    private BorderPost post(Integer id) {
        if (id == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "borderPostId is required");
        return posts.findById(id).orElseThrow(() -> notFound("Border post"));
    }

    private static String code(String prefix) {
        return prefix + "-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "-"
                + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private static String threatFromSeverity(String severity) {
        return switch (severity) {
            case "critical" -> "red";
            case "high" -> "orange";
            case "low" -> "green";
            default -> "yellow";
        };
    }

    private static ResponseStatusException notFound(String what) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, what + " not found");
    }

    private static String str(Object v, String d) { return v == null ? d : String.valueOf(v); }
    private static boolean bool(Object v) {
        if (v instanceof Boolean b) return b;
        return "1".equals(String.valueOf(v)) || "true".equalsIgnoreCase(String.valueOf(v));
    }
    private static Integer num(Object v, Integer d) {
        if (v == null) return d;
        return Integer.valueOf(String.valueOf(v));
    }
    private static int intVal(Object v) { return v instanceof Number n ? n.intValue() : 0; }
    private static BigDecimal dec(Object v) { return v == null ? BigDecimal.ZERO : new BigDecimal(String.valueOf(v)); }
    private static BigDecimal decOrNull(Object v) { return v == null || String.valueOf(v).isBlank() ? null : new BigDecimal(String.valueOf(v)); }
    private static Instant instant(Object v, Instant d) {
        if (v == null || String.valueOf(v).isBlank()) return d;
        String s = String.valueOf(v);
        if (s.length() == 16) s = s + ":00Z";
        try { return Instant.parse(s); } catch (Exception e) { return Instant.parse(s.replace(" ", "T") + (s.endsWith("Z") ? "" : "Z")); }
    }
}

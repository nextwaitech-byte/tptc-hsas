package tz.go.tptc.hsas.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tz.go.tptc.hsas.config.AppProperties;
import tz.go.tptc.hsas.domain.*;
import tz.go.tptc.hsas.repo.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Component
public class DataSeeder implements CommandLineRunner {
    private final RoleRepository roles;
    private final PermissionRepository permissions;
    private final BorderPostRepository posts;
    private final UserRepository users;
    private final MovementRepository movements;
    private final IncidentRepository incidents;
    private final AlertRepository alerts;
    private final EconomicResourceRepository economic;
    private final SystemSettingRepository settings;
    private final VehicleCrossingRepository vehicles;
    private final IntelligenceReportRepository intel;
    private final RefugeeCampRepository camps;
    private final CommunityReportRepository community;
    private final InstitutionalCapacityRepository capacity;
    private final PublicCommunicationRepository comms;
    private final EarlyWarningIndicatorRepository indicators;
    private final PasswordEncoder encoder;
    private final AppProperties properties;

    public DataSeeder(RoleRepository roles, PermissionRepository permissions, BorderPostRepository posts,
                      UserRepository users, MovementRepository movements, IncidentRepository incidents,
                      AlertRepository alerts, EconomicResourceRepository economic, SystemSettingRepository settings,
                      VehicleCrossingRepository vehicles, IntelligenceReportRepository intel, RefugeeCampRepository camps,
                      CommunityReportRepository community, InstitutionalCapacityRepository capacity,
                      PublicCommunicationRepository comms, EarlyWarningIndicatorRepository indicators,
                      PasswordEncoder encoder, AppProperties properties) {
        this.roles = roles;
        this.permissions = permissions;
        this.posts = posts;
        this.users = users;
        this.movements = movements;
        this.incidents = incidents;
        this.alerts = alerts;
        this.economic = economic;
        this.settings = settings;
        this.vehicles = vehicles;
        this.intel = intel;
        this.camps = camps;
        this.community = community;
        this.capacity = capacity;
        this.comms = comms;
        this.indicators = indicators;
        this.encoder = encoder;
        this.properties = properties;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (roles.count() > 0) return;

        Role admin = role("super_admin", "Super Admin", "Full system access");
        Role officer = role("post_officer", "Post Officer", "Data entry at border post");
        Role supervisor = role("border_supervisor", "Border Post Supervisor", "Supervise post operations");
        Role analyst = role("analyst", "Analyst", "Analytics and reports");
        Role security = role("security_officer", "Security Officer", "Alerts and incidents");
        Role executive = role("executive", "Executive View", "Aggregated KPIs only");

        List<Permission> allPerms = List.of(
                perm("dashboard.view", "View Dashboard", "dashboard"),
                perm("movements.view", "View Movements", "movements"),
                perm("movements.create", "Create Movements", "movements"),
                perm("movements.edit", "Edit Movements", "movements"),
                perm("incidents.view", "View Incidents", "incidents"),
                perm("incidents.manage", "Manage Incidents", "incidents"),
                perm("alerts.view", "View Alerts", "alerts"),
                perm("alerts.manage", "Manage Alerts", "alerts"),
                perm("reports.view", "View Reports", "reports"),
                perm("users.manage", "Manage Users", "settings"),
                perm("settings.manage", "Manage System Settings", "settings"),
                perm("analytics.api", "Analytics API Access", "analytics"),
                perm("ai.view", "View AI Analysis", "ai"),
                perm("ai.use", "Use AI Assistant", "ai"),
                perm("vehicles.view", "View Vehicles", "vehicles"),
                perm("vehicles.create", "Create Vehicles", "vehicles"),
                perm("vehicles.edit", "Edit Vehicles", "vehicles"),
                perm("intelligence.view", "View Intelligence", "intelligence"),
                perm("intelligence.create", "Create Intelligence", "intelligence"),
                perm("intelligence.manage", "Manage Intelligence", "intelligence"),
                perm("cop.view", "View COP", "cop"),
                perm("modules.view", "View Data Matrix", "modules"),
                perm("modules.population", "Population", "modules"),
                perm("modules.human_safety", "Human Safety", "modules"),
                perm("modules.economic", "Economic", "modules"),
                perm("modules.health", "Health", "modules"),
                perm("modules.disaster", "Disaster", "modules"),
                perm("modules.border", "Border", "modules"),
                perm("modules.infrastructure", "Infrastructure", "modules"),
                perm("modules.community", "Community Module", "modules"),
                perm("modules.capacity", "Capacity Module", "modules"),
                perm("modules.early_warning", "Early Warning Module", "modules"),
                perm("modules.response", "Response Module", "modules"),
                perm("modules.gis", "GIS Module", "modules"),
                perm("modules.communications", "Communications Module", "modules"),
                perm("modules.ai", "AI Module", "modules"),
                perm("modules.administration", "Admin Module", "modules"),
                perm("modules.dashboard", "Dashboard Module", "modules"),
                perm("community.view", "View Community", "community"),
                perm("community.create", "Create Community", "community"),
                perm("community.manage", "Manage Community", "community"),
                perm("capacity.view", "View Capacity", "capacity"),
                perm("capacity.manage", "Manage Capacity", "capacity"),
                perm("communications.view", "View Communications", "communications"),
                perm("communications.manage", "Manage Communications", "communications"),
                perm("early_warning.view", "View Early Warning", "early_warning"),
                perm("gis.view", "View GIS", "gis")
        );
        admin.getPermissions().addAll(allPerms);
        grant(officer, allPerms, "dashboard.view", "movements.view", "movements.create", "movements.edit",
                "intelligence.view", "intelligence.create", "vehicles.view", "vehicles.create", "vehicles.edit",
                "modules.view", "modules.border", "modules.disaster", "modules.population", "modules.community",
                "modules.dashboard", "community.view", "community.create", "gis.view");
        grant(supervisor, allPerms, "dashboard.view", "movements.view", "movements.create", "movements.edit",
                "incidents.view", "incidents.manage", "alerts.view", "alerts.manage", "reports.view", "analytics.api",
                "ai.view", "ai.use", "vehicles.view", "vehicles.create", "intelligence.view", "intelligence.create",
                "intelligence.manage", "cop.view", "modules.view", "community.view", "community.create", "gis.view",
                "early_warning.view");
        grant(analyst, allPerms, "dashboard.view", "movements.view", "incidents.view", "alerts.view", "reports.view",
                "analytics.api", "ai.view", "ai.use", "intelligence.view", "cop.view", "modules.view", "gis.view",
                "early_warning.view", "capacity.view", "community.view");
        grant(security, allPerms, "dashboard.view", "movements.view", "incidents.view", "incidents.manage",
                "alerts.view", "alerts.manage", "reports.view", "analytics.api", "ai.view", "ai.use",
                "intelligence.view", "intelligence.create", "intelligence.manage", "cop.view", "modules.view",
                "gis.view", "early_warning.view", "community.view");
        grant(executive, allPerms, "dashboard.view", "reports.view", "analytics.api", "ai.view", "intelligence.view",
                "cop.view", "modules.view", "gis.view", "early_warning.view");
        roles.saveAll(List.of(admin, officer, supervisor, analyst, security, executive));

        BorderPost kigoma = post("Kigoma Border Post", "KIG", "Kigoma", "Kasulu", -4.3000000, 29.6300000, 1200);
        BorderPost bukoba = post("Bukoba Border Post", "BUK", "Kagera", "Kyerwa", -1.0045000, 31.4412000, 900);
        BorderPost katavi = post("Katavi Border Post", "KAT", "Katavi", "Mpanda", -6.3500000, 31.0800000, 500);
        BorderPost rukwa = post("Rukwa Border Post", "RUK", "Rukwa", "Sumbawanga", -8.1500000, 31.6100000, 700);
        posts.saveAll(List.of(kigoma, bukoba, katavi, rukwa));

        String hash = encoder.encode(properties.getDemoPassword());
        User adminUser = user(admin, null, "TPTC Administrator", "admin@tptc.go.tz", hash, "+255700000001");
        User officerUser = user(officer, kigoma, "Officer Kigoma", "officer.kigoma@tptc.go.tz", hash, "+255700000002");
        User securityUser = user(security, null, "Security Officer", "security@tptc.go.tz", hash, "+255700000003");
        users.saveAll(List.of(adminUser, officerUser, securityUser));

        setting("dashboard_refresh_seconds", "10", "Dashboard auto-refresh interval");
        setting("high_risk_score_threshold", "70", "High risk score alert threshold");
        setting("duplicate_window_days", "30", "Duplicate identity window");
        setting("app_name", properties.getName(), "Application display name");
        setting("app_short", properties.getShortName(), "Short system name");
        setting("organization", "TPTC", "Organization name");
        setting("partners", "UNDP · UNTFHS", "Partners / programme line");
        setting("tagline", "Secure border assessment for western Tanzania — operational picture, intelligence reporting, and HQ decision support.", "Login tagline");
        setting("login_subtitle", "Sign in to HSAS to continue", "Login subtitle");
        setting("support_line", "Kigoma · Bukoba · Katavi · Rukwa", "Regions / footer line");

        camp("Nyarugusu Camp", "Kigoma", "Kasulu", 150000, 125000, -4.3167, 30.2500);
        camp("Nduta Camp", "Kigoma", "Kakonko", 120000, 98000, -4.1833, 30.3833);
        camp("Mtendeli Camp", "Kigoma", "Kakonko", 50000, 42000, -4.2000, 30.3500);
        camp("Mtabila Transit Centre", "Kagera", "Kyerwa", 8000, 6500, -2.3500, 30.6500);

        econ("Kagera", 45000, "2500000000", "1800000000");
        econ("Kigoma", 28000, "1800000000", "1200000000");
        econ("Katavi", 8500, "600000000", "450000000");
        econ("Rukwa", 6200, "400000000", "280000000");

        Movement m1 = movement("MOV-20260801-001", kigoma, "entry", 28, "Burundi", "female", 32, "refugee", "unhcr_card", "UNHCR-BI-88421", "Burundi", "Kigoma", "Kasulu", 2, "verified", 25, officerUser, "Family of 3");
        Movement m2 = movement("MOV-20260801-002", bukoba, "entry", 27, "DRC", "male", 28, "asylum_seeker", "none", null, "DRC", "Kagera", "Kyerwa", 0, "flagged", 75, officerUser, "No valid documents");
        Movement m9 = movement("MOV-20260801-009", kigoma, "entry", 20, "Burundi", "male", 17, "refugee", "none", null, "Burundi", "Kigoma", "Kasulu", 0, "flagged", 80, officerUser, "Unaccompanied minor");
        movement("MOV-20260801-003", katavi, "entry", 26, "Zambia", "male", 45, "migrant_worker", "passport", "ZM-445521", "Zambia", "Katavi", "Mpanda", 0, "verified", 10, officerUser, null);
        movement("MOV-20260801-004", kigoma, "entry", 12, "Rwanda", "female", 22, "visitor", "passport", "RW-778899", "Rwanda", "Kigoma", "Kigoma Ujiji", 0, "verified", 5, officerUser, null);
        movement("MOV-20260801-005", bukoba, "entry", 6, "DRC", "female", 40, "refugee", "unhcr_card", "UNHCR-CD-55231", "DRC", "Kagera", "Kyerwa", 3, "verified", 15, officerUser, null);
        movement("MOV-20260801-006", rukwa, "entry", 3, "Zambia", "male", 42, "migrant_worker", "passport", "ZM-112244", "Zambia", "Rukwa", "Sumbawanga", 0, "verified", 28, adminUser, null);
        movement("MOV-20260801-007", kigoma, "entry", 1, "DRC", "male", 24, "refugee", "none", null, "DRC", "Kigoma", "Kasulu", 0, "flagged", 72, officerUser, "Suspected duplicate");
        movement("MOV-20260801-008", bukoba, "entry", 0, "Burundi", "male", 29, "asylum_seeker", "passport", "BI-554433", "Burundi", "Kagera", "Kyerwa", 0, "verified", 35, officerUser, null);

        incident("INC-20260801-001", bukoba, m2, "unauthorized_crossing", "high", "Entry without valid travel documents", "investigating", officerUser, 27);
        incident("INC-20260801-002", kigoma, m9, "duplicate_identity", "medium", "Unaccompanied minor flagged", "open", officerUser, 20);
        incident("INC-20260801-003", katavi, null, "smuggling_suspicion", "critical", "Suspicious cargo vehicle inspection required", "investigating", adminUser, 3);

        alert("ALT-20260801-001", "high_risk_score", "high", "High Risk Individual", "Movement MOV-20260801-002 has risk score 75", m2, bukoba, "open");
        alert("ALT-20260801-002", "high_risk_score", "critical", "Critical Risk - Unaccompanied Minor", "Movement MOV-20260801-009 risk score 80", m9, kigoma, "acknowledged");
        alert("ALT-20260801-003", "volume_spike", "medium", "Entry Volume Spike", "Bukoba post entries above 30-day average", null, bukoba, "open");

        vehicle("VEH-20260801-001", katavi, "entry", 5, "heavy_truck", "TZ-4521 ABC", "Tanzania", "John Mwangi", "Tanzania", 1, true, true, "45000000", "cleared", 15, officerUser, "Construction materials",
                cargo("construction_materials", "Cement and steel bars", "18.5", "tonnes", false, "none", false, null));
        vehicle("VEH-20260801-002", bukoba, "entry", 4, "tanker", "CD-8899 XY", "DRC", "Pierre Kabila", "DRC", 2, true, true, "120000000", "flagged", 55, officerUser, "Fuel tanker",
                cargo("fuel_petroleum", "Diesel fuel", "32000", "litres", true, "flammable", true, "FUEL-2026-4421"));
        vehicle("VEH-20260801-003", kigoma, "entry", 3, "pickup", "KE-2233 ZA", "Kenya", "James Otieno", "Kenya", 3, true, false, "0", "flagged", 65, officerUser, "Undeclared cargo suspected",
                cargo("general_goods", "Undeclared mixed goods", "0", "other", false, "none", false, null));

        intel("INT-SEC-001", "security", "illegal_crossing", bukoba, "Kagera", "Kyerwa", null, 2, "high", "orange", "Illegal Border Crossing Cluster", "15 individuals crossed outside official checkpoint at night", 15, "persons", "Illegal crossings", "TPDF / Immigration", "Increase night patrols at sector 4", adminUser);
        intel("INT-SEC-002", "security", "human_trafficking", kigoma, "Kigoma", "Kasulu", null, 5, "critical", "red", "Suspected Human Trafficking", "Group of 8 unaccompanied minors with single escort", 8, "persons", "Victims", "Police Gender Desk", "Immediate investigation and child protection", adminUser);
        intel("INT-HLT-001", "health", "disease_outbreak", bukoba, "Kagera", "Kyerwa", "Mtabila Transit Centre", 3, "high", "orange", "Cholera Outbreak Alert", "12 cases reported at transit centre, 3 hospitalized", 12, "cases", "Infected patients", "Ministry of Health", "Deploy medical team and water purification", adminUser);
        intel("INT-HUM-001", "humanitarian", "refugee_population", null, "Kigoma", "Kasulu", "Nyarugusu Camp", 1, "medium", "yellow", "Camp Population Update", "Camp at 83% capacity, new arrivals increasing", 125000, "persons", "Refugee population", "UNHCR", "Prepare expansion zone B", adminUser);
        intel("INT-HUM-002", "humanitarian", "food_availability", null, "Kigoma", "Kakonko", "Nduta Camp", 4, "high", "orange", "Food Stock Low", "Grain stocks sufficient for only 18 days", 18, "days", "Food remaining", "WFP", "Emergency food shipment required", adminUser);
        intel("INT-ENV-001", "environmental", "floods", null, "Kagera", "Bukoba Urban", null, 10, "high", "orange", "Flood Warning - Kagera River", "River level 1.2m above alert threshold", 1.2, "metres", "Above alert level", "Disaster Management", "Evacuate low-lying border villages", adminUser);
        intel("INT-INF-001", "infrastructure", "road_damage", kigoma, "Kigoma", "Kasulu", null, 3, "medium", "yellow", "Road Damage - Kigoma Corridor", "Potholes and erosion affecting cargo movement", 1, "sections", "Damaged sections", "TANROADS", "Emergency repairs within 72 hours", officerUser);
        intel("INT-LOG-001", "logistics", "medical_supplies", null, "Kigoma", "Kasulu", "Nyarugusu Camp", 2, "high", "orange", "Medical Supplies Critical", "Essential medicines stock at 12 days supply", 12, "days", "Medical stock days", "MOH / UNHCR", "Urgent medical supply airlift", adminUser);
        intel("INT-POP-001", "population", "internal_displacement", null, "Kagera", "Bukoba Urban", null, 8, "high", "orange", "Internal Displacement", "450 households displaced due to flooding", 450, "households", "IDP households", "OPDM", "Establish temporary shelter camps", adminUser);

        CommunityReport cr = new CommunityReport();
        cr.setReportCode("COM-20260801-001");
        cr.setSourceType("village_leader");
        cr.setReporterName("Mama Amina");
        cr.setTitle("Night crossings near Kasulu");
        cr.setDescription("Villagers report boats landing after midnight west of the official post.");
        cr.setCategory("border_security");
        cr.setRegion("Kigoma");
        cr.setDistrict("Kasulu");
        cr.setVillage("Nyamuhleza");
        cr.setReportedAt(Instant.now().minus(2, ChronoUnit.DAYS));
        cr.setCreatedBy(officerUser);
        community.save(cr);

        InstitutionalCapacity cap = new InstitutionalCapacity();
        cap.setRecordCode("CAP-20260801-001");
        cap.setInstitutionName("Kigoma Immigration Post");
        cap.setInstitutionType("immigration");
        cap.setRegion("Kigoma");
        cap.setDistrict("Kasulu");
        cap.setBorderPost(kigoma);
        cap.setPersonnelCount(18);
        cap.setEmergencyTeams(2);
        cap.setVehicles(3);
        cap.setBoats(1);
        cap.setCommunicationEquipment(6);
        cap.setReadinessLevel("medium");
        cap.setFocalPerson("OC Immigration");
        cap.setRecordedAt(Instant.now().minus(5, ChronoUnit.DAYS));
        cap.setCreatedBy(adminUser);
        capacity.save(cap);

        PublicCommunication msg = new PublicCommunication();
        msg.setMessageCode("MSG-20260801-001");
        msg.setMessageType("health_advisory");
        msg.setTitle("Cholera hygiene advisory");
        msg.setBody("Boil drinking water and report watery diarrhoea immediately to the nearest health facility.");
        msg.setChannel("sms");
        msg.setTargetRegion("Kagera");
        msg.setStatus("sent");
        msg.setSentAt(Instant.now().minus(1, ChronoUnit.DAYS));
        msg.setRecipientsCount(12000);
        msg.setCreatedBy(adminUser);
        comms.save(msg);

        ewi("EWI-MIG-01", "Daily entry volume spike", "border", "Entries exceed baseline by threshold %", 40, "%", 22, "watch", "Western borders", 45, 35);
        ewi("EWI-HLT-01", "Disease outbreak signal", "health", "Confirmed outbreak reports open", 1, "reports", 1, "alert", "National", 70, 50);
        ewi("EWI-ENV-01", "Flood / river level risk", "disaster", "Critical environmental reports", 3, "reports", 1, "watch", "Kigoma / Kagera", 55, 40);
        ewi("EWI-SEC-01", "Armed violence frequency", "human_safety", "High/critical security incidents (7d)", 5, "incidents", 2, "alert", "Border districts", 70, 50);
        ewi("EWI-ECO-01", "Essential commodity shortage", "economic", "Critical economic shortage reports", 2, "reports", 0, "normal", "Western regions", 25, 20);
    }

    private Role role(String name, String label, String desc) {
        Role r = new Role();
        r.setName(name);
        r.setLabel(label);
        r.setDescription(desc);
        return r;
    }

    private Permission perm(String name, String label, String module) {
        return permissions.findByName(name).orElseGet(() -> {
            Permission p = new Permission();
            p.setName(name);
            p.setLabel(label);
            p.setModule(module);
            return permissions.save(p);
        });
    }

    private void grant(Role role, List<Permission> all, String... names) {
        List<String> wanted = List.of(names);
        all.stream().filter(p -> wanted.contains(p.getName())).forEach(role.getPermissions()::add);
    }

    private BorderPost post(String name, String code, String region, String district, double lat, double lon, int cap) {
        BorderPost p = new BorderPost();
        p.setName(name);
        p.setCode(code);
        p.setRegion(region);
        p.setDistrict(district);
        p.setLatitude(BigDecimal.valueOf(lat));
        p.setLongitude(BigDecimal.valueOf(lon));
        p.setCapacity(cap);
        return p;
    }

    private User user(Role role, BorderPost post, String name, String email, String hash, String phone) {
        User u = new User();
        u.setRole(role);
        u.setBorderPost(post);
        u.setName(name);
        u.setEmail(email);
        u.setPassword(hash);
        u.setPhone(phone);
        u.setActive(true);
        return u;
    }

    private void setting(String key, String value, String label) {
        if (settings.findBySettingKey(key).isPresent()) return;
        SystemSetting s = new SystemSetting();
        s.setSettingKey(key);
        s.setSettingValue(value);
        s.setLabel(label);
        settings.save(s);
    }

    private void camp(String name, String region, String district, int cap, int pop, double lat, double lon) {
        RefugeeCamp c = new RefugeeCamp();
        c.setName(name);
        c.setRegion(region);
        c.setDistrict(district);
        c.setCapacity(cap);
        c.setCurrentPopulation(pop);
        c.setLatitude(BigDecimal.valueOf(lat));
        c.setLongitude(BigDecimal.valueOf(lon));
        camps.save(c);
    }

    private void econ(String region, int pop, String allocated, String disbursed) {
        EconomicResource e = new EconomicResource();
        e.setRegion(region);
        e.setPeriodMonth(LocalDate.of(2026, 8, 1));
        e.setRefugeePopulation(pop);
        e.setAidBudgetAllocated(new BigDecimal(allocated));
        e.setAidBudgetDisbursed(new BigDecimal(disbursed));
        e.setDailyCostPerCapita(new BigDecimal("3500"));
        economic.save(e);
    }

    private Movement movement(String code, BorderPost post, String dir, int daysAgo, String nat, String sex, int age,
                              String purpose, String docType, String docNo, String origin, String destR, String destD,
                              int minors, String status, int score, User officer, String remarks) {
        Movement m = new Movement();
        m.setRecordCode(code);
        m.setBorderPost(post);
        m.setDirection(dir);
        m.setCrossedAt(Instant.now().minus(daysAgo, ChronoUnit.DAYS));
        m.setNationality(nat);
        m.setSex(sex);
        m.setAge(age);
        m.setAgeGroup(RiskScoringService.ageGroup(age));
        m.setPurpose(purpose);
        m.setDocumentType(docType);
        m.setDocumentNumber(docNo);
        m.setOriginCountry(origin);
        m.setDestinationRegion(destR);
        m.setDestinationDistrict(destD);
        m.setAccompaniedMinors(minors);
        m.setStatus(status);
        m.setRiskScore(score);
        m.setOfficer(officer);
        m.setRemarks(remarks);
        return movements.save(m);
    }

    private void incident(String code, BorderPost post, Movement mov, String type, String sev, String desc, String status, User by, int daysAgo) {
        Incident i = new Incident();
        i.setIncidentCode(code);
        i.setBorderPost(post);
        i.setMovement(mov);
        i.setIncidentType(type);
        i.setSeverity(sev);
        i.setDescription(desc);
        i.setStatus(status);
        i.setReportedBy(by);
        i.setOccurredAt(Instant.now().minus(daysAgo, ChronoUnit.DAYS));
        incidents.save(i);
    }

    private void alert(String code, String type, String sev, String title, String message, Movement mov, BorderPost post, String status) {
        Alert a = new Alert();
        a.setAlertCode(code);
        a.setAlertType(type);
        a.setSeverity(sev);
        a.setTitle(title);
        a.setMessage(message);
        a.setMovement(mov);
        a.setBorderPost(post);
        a.setStatus(status);
        alerts.save(a);
    }

    private VehicleCargo cargo(String type, String desc, String qty, String unit, boolean haz, String hclass, boolean permit, String permitNo) {
        VehicleCargo c = new VehicleCargo();
        c.setCargoType(type);
        c.setDescription(desc);
        c.setQuantity(new BigDecimal(qty));
        c.setUnit(unit);
        c.setHazardous(haz);
        c.setHazardClass(hclass);
        c.setRequiresPermit(permit);
        c.setPermitNumber(permitNo);
        return c;
    }

    private void vehicle(String code, BorderPost post, String dir, int daysAgo, String vtype, String reg, String country,
                         String driver, String nat, int pax, boolean hasCargo, boolean declared, String value,
                         String status, int score, User officer, String remarks, VehicleCargo cargo) {
        VehicleCrossing v = new VehicleCrossing();
        v.setRecordCode(code);
        v.setBorderPost(post);
        v.setDirection(dir);
        v.setCrossedAt(Instant.now().minus(daysAgo, ChronoUnit.DAYS));
        v.setVehicleType(vtype);
        v.setRegistrationNumber(reg);
        v.setRegistrationCountry(country);
        v.setDriverName(driver);
        v.setDriverNationality(nat);
        v.setPassengersCount(pax);
        v.setHasCargo(hasCargo);
        v.setCargoDeclared(declared);
        v.setEstimatedCargoValue(new BigDecimal(value));
        v.setStatus(status);
        v.setRiskScore(score);
        v.setOfficer(officer);
        v.setRemarks(remarks);
        cargo.setVehicleCrossing(v);
        v.getCargoItems().add(cargo);
        vehicles.save(v);
    }

    private void intel(String code, String domain, String category, BorderPost post, String region, String district,
                       String camp, int daysAgo, String sev, String threat, String title, String desc,
                       double metric, String unit, String label, String agency, String action, User by) {
        IntelligenceReport r = new IntelligenceReport();
        r.setReportCode(code);
        r.setDomain(domain);
        r.setCategory(category);
        r.setBorderPost(post);
        r.setRegion(region);
        r.setDistrict(district);
        r.setCampName(camp);
        r.setReportedAt(Instant.now().minus(daysAgo, ChronoUnit.DAYS));
        r.setSeverity(sev);
        r.setThreatLevel(threat);
        r.setTitle(title);
        r.setDescription(desc);
        r.setMetricValue(BigDecimal.valueOf(metric));
        r.setMetricUnit(unit);
        r.setMetricLabel(label);
        r.setResponsibleAgency(agency);
        r.setRecommendedAction(action);
        r.setReportedBy(by);
        r.setStatus("open");
        intel.save(r);
    }

    private void ewi(String code, String name, String domain, String desc, double th, String unit, double cur,
                     String level, String scope, double ai, double prob) {
        EarlyWarningIndicator i = new EarlyWarningIndicator();
        i.setIndicatorCode(code);
        i.setName(name);
        i.setDomain(domain);
        i.setDescription(desc);
        i.setThresholdValue(BigDecimal.valueOf(th));
        i.setThresholdUnit(unit);
        i.setCurrentValue(BigDecimal.valueOf(cur));
        i.setEscalationLevel(level);
        i.setGeographicalScope(scope);
        i.setAiRiskScore(BigDecimal.valueOf(ai));
        i.setProbabilityPct(BigDecimal.valueOf(prob));
        indicators.save(i);
    }
}

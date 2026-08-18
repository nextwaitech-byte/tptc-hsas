package tz.go.tptc.hsas.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tz.go.tptc.hsas.catalog.Catalog;
import tz.go.tptc.hsas.security.AuthPrincipal;
import tz.go.tptc.hsas.service.AiService;
import tz.go.tptc.hsas.service.AnalyticsService;
import tz.go.tptc.hsas.service.AuthService;
import tz.go.tptc.hsas.service.RecordService;
import tz.go.tptc.hsas.service.SettingsService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ApiController {
    private final AuthService auth;
    private final AnalyticsService analytics;
    private final AiService ai;
    private final RecordService records;
    private final SettingsService settings;

    public ApiController(AuthService auth, AnalyticsService analytics, AiService ai, RecordService records,
                         SettingsService settings) {
        this.auth = auth;
        this.analytics = analytics;
        this.ai = ai;
        this.records = records;
        this.settings = settings;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok", "service", "hsas-api");
    }

    @PostMapping("/auth/login")
    public Map<String, Object> login(@RequestBody LoginRequest req) {
        return auth.login(req.email(), req.password());
    }

    @GetMapping("/auth/me")
    public Map<String, Object> me(@AuthenticationPrincipal AuthPrincipal principal) {
        return auth.profile(principal);
    }

    @GetMapping("/auth/demo-users")
    public Object demoUsers() {
        return auth.demoUsers();
    }

    @GetMapping("/branding")
    public Map<String, Object> branding() {
        return settings.publicBranding();
    }

    @GetMapping("/branding/logo")
    public ResponseEntity<byte[]> brandingLogo() {
        SettingsService.LogoFile logo = settings.logoFile();
        return ResponseEntity.ok()
                .contentType(logo.contentType() == null ? MediaType.IMAGE_PNG : logo.contentType())
                .cacheControl(CacheControl.noCache().mustRevalidate())
                .body(logo.bytes());
    }

    @GetMapping("/settings")
    public Map<String, Object> settings(@AuthenticationPrincipal AuthPrincipal p) {
        return settings.adminView(p);
    }

    @PutMapping("/settings")
    public Map<String, Object> updateSettings(@RequestBody Map<String, Object> body,
                                              @AuthenticationPrincipal AuthPrincipal p) {
        return settings.update(body, p);
    }

    @PostMapping(value = "/settings/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> uploadLogo(@RequestParam("file") MultipartFile file,
                                          @AuthenticationPrincipal AuthPrincipal p) {
        return settings.saveLogo(file, p);
    }

    @DeleteMapping("/settings/logo")
    public Map<String, Object> resetLogo(@AuthenticationPrincipal AuthPrincipal p) {
        return settings.resetLogo(p);
    }

    @GetMapping("/lookups")
    public Map<String, Object> lookups() {
        return records.lookups();
    }

    @GetMapping("/modules")
    public Object modules() {
        return Catalog.modules();
    }

    @GetMapping("/analytics/kpis")
    public Map<String, Object> kpis(@RequestParam(required = false) Integer borderPostId) {
        return analytics.kpis(borderPostId);
    }

    @GetMapping("/analytics/live")
    public Map<String, Object> live(@RequestParam(required = false) Integer borderPostId) {
        return analytics.live(borderPostId);
    }

    @GetMapping("/analytics/trends")
    public Map<String, Object> trends(@RequestParam(defaultValue = "30") int days,
                                      @RequestParam(required = false) Integer borderPostId) {
        return analytics.trends(days, borderPostId);
    }

    @GetMapping("/cop")
    public Map<String, Object> cop(@RequestParam(required = false) Integer borderPostId) {
        return analytics.cop(borderPostId);
    }

    @GetMapping("/gis")
    public Map<String, Object> gis() {
        return analytics.gis();
    }

    @GetMapping("/early-warning")
    public Object earlyWarning() {
        return analytics.earlyWarning();
    }

    @GetMapping("/reports/daily")
    public Map<String, Object> dailyReport() {
        return analytics.dailyReport();
    }

    @GetMapping("/ai/analysis")
    public Map<String, Object> ai(@RequestParam(required = false) Integer borderPostId) {
        return ai.analysis(borderPostId);
    }

    @PostMapping("/ai/ask")
    public Map<String, Object> ask(@RequestBody Map<String, String> body,
                                   @RequestParam(required = false) Integer borderPostId) {
        return ai.ask(body.getOrDefault("question", ""), borderPostId);
    }

    @GetMapping("/movements")
    public Object movements() { return records.movements(); }

    @GetMapping("/movements/{id}")
    public Object movement(@PathVariable Long id) { return records.movement(id); }

    @PostMapping("/movements")
    public Object createMovement(@RequestBody Map<String, Object> body, @AuthenticationPrincipal AuthPrincipal p) {
        return records.createMovement(body, p);
    }

    @PutMapping("/movements/{id}")
    public Object updateMovement(@PathVariable Long id, @RequestBody Map<String, Object> body, @AuthenticationPrincipal AuthPrincipal p) {
        return records.updateMovement(id, body, p);
    }

    @GetMapping("/vehicles")
    public Object vehicles() { return records.vehicles(); }

    @GetMapping("/vehicles/{id}")
    public Object vehicle(@PathVariable Long id) { return records.vehicle(id); }

    @PostMapping("/vehicles")
    public Object createVehicle(@RequestBody Map<String, Object> body, @AuthenticationPrincipal AuthPrincipal p) {
        return records.createVehicle(body, p);
    }

    @GetMapping("/incidents")
    public Object incidents() { return records.incidents(); }

    @PostMapping("/incidents")
    public Object createIncident(@RequestBody Map<String, Object> body, @AuthenticationPrincipal AuthPrincipal p) {
        return records.createIncident(body, p);
    }

    @PatchMapping("/incidents/{id}/status")
    public Object incidentStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        return records.updateIncidentStatus(id, body.get("status"));
    }

    @GetMapping("/alerts")
    public Object alerts() { return records.alerts(); }

    @PatchMapping("/alerts/{id}/acknowledge")
    public Object ack(@PathVariable Integer id, @AuthenticationPrincipal AuthPrincipal p) {
        return records.acknowledgeAlert(id, p);
    }

    @PatchMapping("/alerts/{id}/resolve")
    public Object resolve(@PathVariable Integer id, @AuthenticationPrincipal AuthPrincipal p) {
        return records.resolveAlert(id, p);
    }

    @GetMapping("/intelligence")
    public Object intelligence(@RequestParam(required = false) String domain) {
        return records.intelligence(domain);
    }

    @GetMapping("/intelligence/{id}")
    public Object intelligenceOne(@PathVariable Integer id) { return records.intelligenceOne(id); }

    @PostMapping("/intelligence")
    public Object createIntel(@RequestBody Map<String, Object> body, @AuthenticationPrincipal AuthPrincipal p) {
        return records.createIntelligence(body, p);
    }

    @PatchMapping("/intelligence/{id}/status")
    public Object intelStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        return records.updateIntelStatus(id, body.get("status"));
    }

    @GetMapping("/community")
    public Object community() { return records.community(); }

    @PostMapping("/community")
    public Object createCommunity(@RequestBody Map<String, Object> body, @AuthenticationPrincipal AuthPrincipal p) {
        return records.createCommunity(body, p);
    }

    @PatchMapping("/community/{id}/status")
    public Object communityStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        return records.updateCommunityStatus(id, body.get("verificationStatus"), body.get("responseStatus"));
    }

    @GetMapping("/capacity")
    public Object capacity() { return records.capacity(); }

    @PostMapping("/capacity")
    public Object createCapacity(@RequestBody Map<String, Object> body, @AuthenticationPrincipal AuthPrincipal p) {
        return records.createCapacity(body, p);
    }

    @GetMapping("/communications")
    public Object communications() { return records.communications(); }

    @PostMapping("/communications")
    public Object createComm(@RequestBody Map<String, Object> body, @AuthenticationPrincipal AuthPrincipal p) {
        return records.createCommunication(body, p);
    }

    @GetMapping("/users")
    public Object users() { return records.users(); }

    @PostMapping("/users")
    public Object createUser(@RequestBody Map<String, Object> body, @AuthenticationPrincipal AuthPrincipal p) {
        return records.createUser(body, p);
    }

    @PutMapping("/users/{id}")
    public Object updateUser(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        return records.updateUser(id, body);
    }

    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}
}

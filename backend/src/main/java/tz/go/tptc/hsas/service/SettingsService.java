package tz.go.tptc.hsas.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import tz.go.tptc.hsas.config.AppProperties;
import tz.go.tptc.hsas.domain.BrandingAsset;
import tz.go.tptc.hsas.domain.SystemSetting;
import tz.go.tptc.hsas.repo.BrandingAssetRepository;
import tz.go.tptc.hsas.repo.SystemSettingRepository;
import tz.go.tptc.hsas.security.AuthPrincipal;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SettingsService {
    public static final String LOGO_KEY = "app_logo";
    private static final long MAX_LOGO_BYTES = 2 * 1024 * 1024;
    private static final List<String> ALLOWED_KEYS = List.of(
            "app_name", "app_short", "organization", "partners", "tagline",
            "login_subtitle", "support_line", "dashboard_refresh_seconds",
            "high_risk_score_threshold", "duplicate_window_days"
    );
    private static final Map<String, String> LABELS = Map.ofEntries(
            Map.entry("app_name", "System name"),
            Map.entry("app_short", "Short name"),
            Map.entry("organization", "Organization"),
            Map.entry("partners", "Partners / programme"),
            Map.entry("tagline", "Login tagline"),
            Map.entry("login_subtitle", "Login subtitle"),
            Map.entry("support_line", "Regions / footer line"),
            Map.entry("dashboard_refresh_seconds", "Dashboard refresh (seconds)"),
            Map.entry("high_risk_score_threshold", "High-risk score threshold"),
            Map.entry("duplicate_window_days", "Duplicate identity window (days)")
    );

    private final SystemSettingRepository settings;
    private final BrandingAssetRepository assets;
    private final AppProperties properties;

    public SettingsService(SystemSettingRepository settings, BrandingAssetRepository assets, AppProperties properties) {
        this.settings = settings;
        this.assets = assets;
        this.properties = properties;
    }

    public String get(String key, String fallback) {
        return settings.findBySettingKey(key)
                .map(SystemSetting::getSettingValue)
                .filter(v -> v != null && !v.isBlank())
                .orElse(fallback);
    }

    public String appName() {
        return get("app_name", properties.getName());
    }

    public String appShort() {
        return get("app_short", properties.getShortName());
    }

    public Map<String, Object> publicBranding() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("app_name", appName());
        m.put("app_short", appShort());
        m.put("organization", get("organization", "TPTC"));
        m.put("partners", get("partners", "UNDP · UNTFHS"));
        m.put("tagline", get("tagline",
                "Secure border assessment for western Tanzania — operational picture, intelligence reporting, and HQ decision support."));
        m.put("login_subtitle", get("login_subtitle", "Sign in to " + appShort() + " to continue"));
        m.put("support_line", get("support_line", "Kigoma · Bukoba · Katavi · Rukwa"));
        m.put("has_custom_logo", assets.findByAssetKey(LOGO_KEY).isPresent());
        m.put("logo_url", "/api/v1/branding/logo");
        m.put("logo_updated_at", logoUpdatedAt());
        return m;
    }

    public Map<String, Object> adminView(AuthPrincipal actor) {
        requireAdmin(actor);
        Map<String, Object> m = new LinkedHashMap<>(publicBranding());
        List<Map<String, Object>> fields = ALLOWED_KEYS.stream().map(key -> {
            Map<String, Object> f = new LinkedHashMap<>();
            f.put("key", key);
            f.put("label", LABELS.getOrDefault(key, key));
            f.put("value", get(key, defaultFor(key)));
            f.put("multiline", "tagline".equals(key));
            return f;
        }).toList();
        m.put("fields", fields);
        return m;
    }

    public Map<String, Object> update(Map<String, Object> body, AuthPrincipal actor) {
        requireAdmin(actor);
        if (body == null || body.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No settings provided.");
        }
        body.forEach((key, raw) -> {
            if (!ALLOWED_KEYS.contains(key) || raw == null) return;
            String value = String.valueOf(raw).trim();
            if ("app_name".equals(key) || "app_short".equals(key) || "organization".equals(key)) {
                if (value.isBlank()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, LABELS.get(key) + " cannot be empty.");
                }
            }
            if ("dashboard_refresh_seconds".equals(key) || "high_risk_score_threshold".equals(key)
                    || "duplicate_window_days".equals(key)) {
                try {
                    int n = Integer.parseInt(value);
                    if (n < 1 || n > 10000) throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, LABELS.get(key) + " must be a positive number.");
                }
            }
            upsert(key, value, LABELS.getOrDefault(key, key));
        });
        return adminView(actor);
    }

    public Map<String, Object> saveLogo(MultipartFile file, AuthPrincipal actor) {
        requireAdmin(actor);
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose a logo image to upload.");
        }
        if (file.getSize() > MAX_LOGO_BYTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Logo must be 2 MB or smaller.");
        }
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase();
        String name = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        if (!isAllowedImage(contentType, name)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Use PNG, JPG, SVG or WEBP for the logo.");
        }
        try {
            byte[] data = file.getBytes();
            if (contentType.contains("svg") || name.endsWith(".svg")) {
                String svg = new String(data);
                if (svg.toLowerCase().contains("<script") || svg.toLowerCase().contains("javascript:")) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This SVG is not allowed.");
                }
            }
            BrandingAsset asset = assets.findByAssetKey(LOGO_KEY).orElseGet(BrandingAsset::new);
            asset.setAssetKey(LOGO_KEY);
            asset.setContentType(normalizeType(contentType, name));
            asset.setFileName(file.getOriginalFilename());
            asset.setData(data);
            asset.setUpdatedAt(Instant.now());
            assets.save(asset);
            return adminView(actor);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not read the uploaded logo.");
        }
    }

    public Map<String, Object> resetLogo(AuthPrincipal actor) {
        requireAdmin(actor);
        assets.findByAssetKey(LOGO_KEY).ifPresent(assets::delete);
        return adminView(actor);
    }

    public LogoFile logoFile() {
        return assets.findByAssetKey(LOGO_KEY)
                .map(a -> new LogoFile(a.getData(), MediaType.parseMediaType(a.getContentType()), a.getUpdatedAt()))
                .orElseGet(this::defaultLogo);
    }

    private LogoFile defaultLogo() {
        try {
            ClassPathResource resource = new ClassPathResource("branding/default-logo.svg");
            return new LogoFile(resource.getInputStream().readAllBytes(), MediaType.parseMediaType("image/svg+xml"), Instant.EPOCH);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Default logo missing.");
        }
    }

    private long logoUpdatedAt() {
        return assets.findByAssetKey(LOGO_KEY)
                .map(a -> a.getUpdatedAt() == null ? Instant.now().toEpochMilli() : a.getUpdatedAt().toEpochMilli())
                .orElse(0L);
    }

    private void upsert(String key, String value, String label) {
        SystemSetting s = settings.findBySettingKey(key).orElseGet(SystemSetting::new);
        s.setSettingKey(key);
        s.setSettingValue(value);
        s.setLabel(label);
        s.setUpdatedAt(Instant.now());
        settings.save(s);
    }

    private String defaultFor(String key) {
        return switch (key) {
            case "app_name" -> properties.getName();
            case "app_short" -> properties.getShortName();
            case "organization" -> "TPTC";
            case "partners" -> "UNDP · UNTFHS";
            case "tagline" -> "Secure border assessment for western Tanzania — operational picture, intelligence reporting, and HQ decision support.";
            case "login_subtitle" -> "Sign in to HSAS to continue";
            case "support_line" -> "Kigoma · Bukoba · Katavi · Rukwa";
            case "dashboard_refresh_seconds" -> "10";
            case "high_risk_score_threshold" -> "70";
            case "duplicate_window_days" -> "30";
            default -> "";
        };
    }

    private boolean isAllowedImage(String contentType, String name) {
        return contentType.equals("image/png") || contentType.equals("image/jpeg") || contentType.equals("image/jpg")
                || contentType.equals("image/webp") || contentType.equals("image/svg+xml") || contentType.equals("image/gif")
                || name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")
                || name.endsWith(".webp") || name.endsWith(".svg") || name.endsWith(".gif");
    }

    private String normalizeType(String contentType, String name) {
        if (contentType.startsWith("image/")) return contentType.equals("image/jpg") ? "image/jpeg" : contentType;
        if (name.endsWith(".svg")) return "image/svg+xml";
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".webp")) return "image/webp";
        if (name.endsWith(".gif")) return "image/gif";
        return "image/jpeg";
    }

    private void requireAdmin(AuthPrincipal actor) {
        if (actor == null || !(actor.isSuperAdmin() || actor.has("settings.manage"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only administrators can change system settings.");
        }
    }

    public record LogoFile(byte[] bytes, MediaType contentType, Instant updatedAt) {}
}

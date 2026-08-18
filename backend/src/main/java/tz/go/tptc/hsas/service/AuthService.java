package tz.go.tptc.hsas.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tz.go.tptc.hsas.domain.AuditLog;
import tz.go.tptc.hsas.domain.User;
import tz.go.tptc.hsas.repo.AuditLogRepository;
import tz.go.tptc.hsas.repo.UserRepository;
import tz.go.tptc.hsas.security.AuthPrincipal;
import tz.go.tptc.hsas.security.JwtService;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final AuditLogRepository audits;
    private final SettingsService settings;

    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt,
                       AuditLogRepository audits, SettingsService settings) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
        this.audits = audits;
        this.settings = settings;
    }

    public Map<String, Object> login(String email, String password) {
        User user = users.findWithRoleByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password."));
        if (!user.isActive() || !encoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
        }
        user.setLastLogin(Instant.now());
        users.save(user);
        AuditLog log = new AuditLog();
        log.setUser(user);
        log.setAction("login");
        log.setModule("auth");
        log.setRecordId(String.valueOf(user.getId()));
        log.setDescription("User logged in");
        audits.save(log);
        AuthPrincipal principal = AuthPrincipal.from(user);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("token", jwt.generate(user.getId(), user.getEmail()));
        body.put("user", profile(principal));
        return body;
    }

    public Map<String, Object> profile(AuthPrincipal p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", p.getId());
        m.put("name", p.getName());
        m.put("email", p.getEmail());
        m.put("role_name", p.getRoleName());
        m.put("role_label", p.getRoleLabel());
        m.put("border_post_id", p.getBorderPostId());
        m.put("border_post_name", p.getBorderPostName());
        m.put("permissions", p.getPermissions());
        m.put("is_post_officer", p.isPostOfficer());
        m.put("is_super_admin", p.isSuperAdmin());
        m.put("app_name", settings.appName());
        m.put("app_short", settings.appShort());
        m.put("organization", settings.get("organization", "TPTC"));
        m.put("logo_url", "/api/v1/branding/logo");
        return m;
    }

    public List<Map<String, Object>> demoUsers() {
        return users.findByActiveTrueAndDeletedAtIsNullOrderByIdAsc().stream().map(u -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", u.getId());
            m.put("name", u.getName());
            m.put("email", u.getEmail());
            m.put("role_name", u.getRole().getName());
            m.put("role_label", u.getRole().getLabel());
            m.put("border_post_name", u.getBorderPost() != null ? u.getBorderPost().getName() : null);
            return m;
        }).toList();
    }
}

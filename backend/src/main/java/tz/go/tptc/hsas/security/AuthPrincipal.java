package tz.go.tptc.hsas.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import tz.go.tptc.hsas.domain.Permission;
import tz.go.tptc.hsas.domain.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AuthPrincipal {
    private final Integer id;
    private final String name;
    private final String email;
    private final String roleName;
    private final String roleLabel;
    private final Integer borderPostId;
    private final String borderPostName;
    private final Set<String> permissions;
    private final Collection<? extends GrantedAuthority> authorities;

    public AuthPrincipal(Integer id, String name, String email, String roleName, String roleLabel,
                         Integer borderPostId, String borderPostName, Set<String> permissions) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.roleName = roleName;
        this.roleLabel = roleLabel;
        this.borderPostId = borderPostId;
        this.borderPostName = borderPostName;
        this.permissions = permissions;
        this.authorities = permissions.stream()
                .map(p -> new SimpleGrantedAuthority("PERM_" + p))
                .collect(Collectors.toList());
    }

    public static AuthPrincipal from(User user) {
        Set<String> perms = user.getRole().getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());
        Integer postId = user.getBorderPost() != null ? user.getBorderPost().getId() : null;
        String postName = user.getBorderPost() != null ? user.getBorderPost().getName() : null;
        return new AuthPrincipal(user.getId(), user.getName(), user.getEmail(),
                user.getRole().getName(), user.getRole().getLabel(), postId, postName, perms);
    }

    public boolean has(String permission) {
        return permissions.contains(permission);
    }

    public boolean isPostOfficer() {
        return "post_officer".equals(roleName) || "immigration_officer".equals(roleName);
    }

    public boolean isSuperAdmin() {
        return "super_admin".equals(roleName);
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRoleName() { return roleName; }
    public String getRoleLabel() { return roleLabel; }
    public Integer getBorderPostId() { return borderPostId; }
    public String getBorderPostName() { return borderPostName; }
    public Set<String> getPermissions() { return permissions; }
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
}

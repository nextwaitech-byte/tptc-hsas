-- Super Admin system settings: extra branding keys, settings.manage permission, logo storage.

CREATE TABLE IF NOT EXISTS branding_assets (
    id           SERIAL PRIMARY KEY,
    asset_key    VARCHAR(40)  NOT NULL UNIQUE,
    content_type VARCHAR(80)  NOT NULL,
    file_name    VARCHAR(160),
    data         BYTEA        NOT NULL,
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

INSERT INTO permissions (name, label, module)
SELECT 'settings.manage', 'Manage System Settings', 'settings'
WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE name = 'settings.manage');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'super_admin'
  AND p.name = 'settings.manage'
  AND NOT EXISTS (
      SELECT 1 FROM role_permissions rp
      WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

INSERT INTO system_settings (setting_key, setting_value, label, updated_at)
VALUES
    ('app_name', 'Human Security Assessment System', 'Application display name', NOW()),
    ('app_short', 'HSAS', 'Short system name', NOW()),
    ('organization', 'TPTC', 'Organization name', NOW()),
    ('partners', 'UNDP · UNTFHS', 'Partners / programme line', NOW()),
    ('tagline', 'Secure border assessment for western Tanzania — operational picture, intelligence reporting, and HQ decision support.', 'Login tagline', NOW()),
    ('login_subtitle', 'Sign in to HSAS to continue', 'Login subtitle', NOW()),
    ('support_line', 'Kigoma · Bukoba · Katavi · Rukwa', 'Regions / footer line', NOW()),
    ('dashboard_refresh_seconds', '10', 'Dashboard auto-refresh interval', NOW()),
    ('high_risk_score_threshold', '70', 'High risk score alert threshold', NOW()),
    ('duplicate_window_days', '30', 'Duplicate identity window', NOW())
ON CONFLICT (setting_key) DO NOTHING;

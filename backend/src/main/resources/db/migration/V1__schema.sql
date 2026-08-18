-- Isolated HSAS schema for the Java stack (PostgreSQL).
-- Does not touch MySQL tptc_analytics or any host PostgreSQL databases.

CREATE TABLE roles (
    id          SMALLSERIAL PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL UNIQUE,
    label       VARCHAR(100) NOT NULL,
    description TEXT,
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE TABLE permissions (
    id     SMALLSERIAL PRIMARY KEY,
    name   VARCHAR(80)  NOT NULL UNIQUE,
    label  VARCHAR(120) NOT NULL,
    module VARCHAR(50)  NOT NULL
);

CREATE TABLE role_permissions (
    role_id       SMALLINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id SMALLINT NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE border_posts (
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(150) NOT NULL,
    code       VARCHAR(20)  NOT NULL UNIQUE,
    region     VARCHAR(80)  NOT NULL,
    district   VARCHAR(80)  NOT NULL,
    post_type  VARCHAR(20)  NOT NULL DEFAULT 'land',
    latitude   NUMERIC(10,7),
    longitude  NUMERIC(10,7),
    capacity   INTEGER      NOT NULL DEFAULT 500,
    is_active  BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ
);

CREATE TABLE users (
    id             SERIAL PRIMARY KEY,
    role_id        SMALLINT     NOT NULL REFERENCES roles(id),
    border_post_id INTEGER      REFERENCES border_posts(id) ON DELETE SET NULL,
    name           VARCHAR(120) NOT NULL,
    email          VARCHAR(150) NOT NULL UNIQUE,
    password       VARCHAR(255) NOT NULL,
    phone          VARCHAR(20),
    is_active      BOOLEAN      NOT NULL DEFAULT TRUE,
    last_login     TIMESTAMPTZ,
    created_at     TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ,
    deleted_at     TIMESTAMPTZ
);

CREATE TABLE movements (
    id                   BIGSERIAL PRIMARY KEY,
    record_code          VARCHAR(30)  NOT NULL UNIQUE,
    border_post_id       INTEGER      NOT NULL REFERENCES border_posts(id),
    direction            VARCHAR(20)  NOT NULL DEFAULT 'entry',
    crossed_at           TIMESTAMPTZ    NOT NULL,
    nationality          VARCHAR(80)  NOT NULL,
    sex                  VARCHAR(20)  NOT NULL,
    age                  INTEGER,
    age_group            VARCHAR(20),
    purpose              VARCHAR(40)  NOT NULL,
    document_type        VARCHAR(40)  NOT NULL DEFAULT 'none',
    document_number      VARCHAR(80),
    origin_country       VARCHAR(80)  NOT NULL,
    destination_region   VARCHAR(80),
    destination_district VARCHAR(80),
    accompanied_minors   INTEGER      NOT NULL DEFAULT 0,
    family_group_id      VARCHAR(40),
    has_biometric        BOOLEAN      NOT NULL DEFAULT FALSE,
    status               VARCHAR(20)  NOT NULL DEFAULT 'pending',
    risk_score           SMALLINT     NOT NULL DEFAULT 0,
    officer_id           INTEGER      NOT NULL REFERENCES users(id),
    remarks              TEXT,
    created_at           TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ,
    deleted_at           TIMESTAMPTZ
);
CREATE INDEX idx_movements_crossed ON movements (crossed_at);
CREATE INDEX idx_movements_post ON movements (border_post_id);
CREATE INDEX idx_movements_status ON movements (status);

CREATE TABLE incidents (
    id                   SERIAL PRIMARY KEY,
    incident_code        VARCHAR(30)  NOT NULL UNIQUE,
    border_post_id       INTEGER      NOT NULL REFERENCES border_posts(id),
    movement_id          BIGINT       REFERENCES movements(id) ON DELETE SET NULL,
    incident_type        VARCHAR(40)  NOT NULL,
    severity             VARCHAR(20)  NOT NULL DEFAULT 'medium',
    description          TEXT         NOT NULL,
    latitude             NUMERIC(10,7),
    longitude            NUMERIC(10,7),
    photo_path           VARCHAR(255),
    responsible_agency   VARCHAR(120),
    actions_taken        TEXT,
    resources_required   TEXT,
    status               VARCHAR(20)  NOT NULL DEFAULT 'open',
    assigned_officer_id  INTEGER      REFERENCES users(id) ON DELETE SET NULL,
    reported_by          INTEGER      NOT NULL REFERENCES users(id),
    occurred_at          TIMESTAMPTZ    NOT NULL,
    resolved_at          TIMESTAMPTZ,
    created_at           TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ
);

CREATE TABLE alerts (
    id               SERIAL PRIMARY KEY,
    alert_code       VARCHAR(30)  NOT NULL UNIQUE,
    alert_type       VARCHAR(40)  NOT NULL,
    severity         VARCHAR(20)  NOT NULL DEFAULT 'medium',
    title            VARCHAR(200) NOT NULL,
    message          TEXT         NOT NULL,
    movement_id      BIGINT       REFERENCES movements(id) ON DELETE SET NULL,
    border_post_id   INTEGER      REFERENCES border_posts(id) ON DELETE SET NULL,
    status           VARCHAR(20)  NOT NULL DEFAULT 'open',
    created_by       INTEGER      REFERENCES users(id) ON DELETE SET NULL,
    acknowledged_by  INTEGER      REFERENCES users(id) ON DELETE SET NULL,
    resolved_by      INTEGER      REFERENCES users(id) ON DELETE SET NULL,
    created_at       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    acknowledged_at  TIMESTAMPTZ,
    resolved_at      TIMESTAMPTZ
);

CREATE TABLE economic_resources (
    id                      SERIAL PRIMARY KEY,
    region                  VARCHAR(80)    NOT NULL,
    period_month            DATE           NOT NULL,
    refugee_population      INTEGER        NOT NULL DEFAULT 0,
    aid_budget_allocated    NUMERIC(15,2)  NOT NULL DEFAULT 0,
    aid_budget_disbursed    NUMERIC(15,2)  NOT NULL DEFAULT 0,
    food_units              INTEGER        NOT NULL DEFAULT 0,
    water_units             INTEGER        NOT NULL DEFAULT 0,
    shelter_units           INTEGER        NOT NULL DEFAULT 0,
    medical_units           INTEGER        NOT NULL DEFAULT 0,
    job_seekers_registered  INTEGER        NOT NULL DEFAULT 0,
    jobs_available          INTEGER        NOT NULL DEFAULT 0,
    daily_cost_per_capita   NUMERIC(10,2)  NOT NULL DEFAULT 3500.00,
    notes                   TEXT,
    created_at              TIMESTAMPTZ      NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ,
    UNIQUE (region, period_month)
);

CREATE TABLE analytics_snapshots (
    id              BIGSERIAL PRIMARY KEY,
    snapshot_type   VARCHAR(20)   NOT NULL,
    snapshot_at     TIMESTAMPTZ     NOT NULL,
    border_post_id  INTEGER       REFERENCES border_posts(id) ON DELETE SET NULL,
    entries_count   INTEGER       NOT NULL DEFAULT 0,
    exits_count     INTEGER       NOT NULL DEFAULT 0,
    net_inflow      INTEGER       NOT NULL DEFAULT 0,
    open_alerts     INTEGER       NOT NULL DEFAULT 0,
    open_incidents  INTEGER       NOT NULL DEFAULT 0,
    avg_risk_score  NUMERIC(5,2)  NOT NULL DEFAULT 0,
    economic_burden NUMERIC(15,2) NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE TABLE audit_logs (
    id          BIGSERIAL PRIMARY KEY,
    user_id     INTEGER      REFERENCES users(id) ON DELETE SET NULL,
    action      VARCHAR(50)  NOT NULL,
    module      VARCHAR(50)  NOT NULL,
    record_id   VARCHAR(50),
    description TEXT,
    ip_address  VARCHAR(45),
    user_agent  VARCHAR(255),
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE TABLE system_settings (
    id            SERIAL PRIMARY KEY,
    setting_key   VARCHAR(80)  NOT NULL UNIQUE,
    setting_value TEXT         NOT NULL,
    label         VARCHAR(150),
    updated_at    TIMESTAMPTZ
);

CREATE TABLE vehicle_crossings (
    id                    BIGSERIAL PRIMARY KEY,
    record_code           VARCHAR(30)    NOT NULL UNIQUE,
    border_post_id        INTEGER        NOT NULL REFERENCES border_posts(id),
    direction             VARCHAR(20)    NOT NULL DEFAULT 'entry',
    crossed_at            TIMESTAMPTZ      NOT NULL,
    vehicle_type          VARCHAR(40)    NOT NULL,
    registration_number   VARCHAR(40)    NOT NULL,
    registration_country  VARCHAR(80)    NOT NULL,
    driver_name           VARCHAR(120)   NOT NULL,
    driver_nationality    VARCHAR(80)    NOT NULL,
    driver_document       VARCHAR(80),
    passengers_count      INTEGER        NOT NULL DEFAULT 0,
    has_cargo             BOOLEAN        NOT NULL DEFAULT FALSE,
    cargo_declared        BOOLEAN        NOT NULL DEFAULT TRUE,
    estimated_cargo_value NUMERIC(15,2)  NOT NULL DEFAULT 0,
    status                VARCHAR(20)    NOT NULL DEFAULT 'pending',
    risk_score            SMALLINT       NOT NULL DEFAULT 0,
    officer_id            INTEGER        NOT NULL REFERENCES users(id),
    remarks               TEXT,
    created_at            TIMESTAMPTZ      NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMPTZ,
    deleted_at            TIMESTAMPTZ
);

CREATE TABLE vehicle_cargo (
    id                   BIGSERIAL PRIMARY KEY,
    vehicle_crossing_id  BIGINT         NOT NULL REFERENCES vehicle_crossings(id) ON DELETE CASCADE,
    cargo_type           VARCHAR(40)    NOT NULL,
    description          VARCHAR(255),
    quantity             NUMERIC(12,2)  NOT NULL DEFAULT 0,
    unit                 VARCHAR(20)    NOT NULL DEFAULT 'kg',
    estimated_value_tzs  NUMERIC(15,2)  NOT NULL DEFAULT 0,
    is_hazardous         BOOLEAN        NOT NULL DEFAULT FALSE,
    hazard_class         VARCHAR(40)    NOT NULL DEFAULT 'none',
    requires_permit      BOOLEAN        NOT NULL DEFAULT FALSE,
    permit_number        VARCHAR(80),
    created_at           TIMESTAMPTZ      NOT NULL DEFAULT NOW()
);

CREATE TABLE intelligence_reports (
    id                   SERIAL PRIMARY KEY,
    report_code          VARCHAR(30)    NOT NULL UNIQUE,
    domain               VARCHAR(40)    NOT NULL,
    category             VARCHAR(80)    NOT NULL,
    border_post_id       INTEGER        REFERENCES border_posts(id) ON DELETE SET NULL,
    region               VARCHAR(80),
    district             VARCHAR(80),
    camp_name            VARCHAR(120),
    reported_at          TIMESTAMPTZ      NOT NULL,
    severity             VARCHAR(20)    NOT NULL DEFAULT 'medium',
    threat_level         VARCHAR(20)    NOT NULL DEFAULT 'yellow',
    title                VARCHAR(200)   NOT NULL,
    description          TEXT           NOT NULL,
    latitude             NUMERIC(10,7),
    longitude            NUMERIC(10,7),
    metric_value         NUMERIC(15,2),
    metric_unit          VARCHAR(40),
    metric_label         VARCHAR(120),
    responsible_agency   VARCHAR(120),
    actions_taken        TEXT,
    resources_required   TEXT,
    recommended_action   TEXT,
    status               VARCHAR(20)    NOT NULL DEFAULT 'open',
    reported_by          INTEGER        NOT NULL REFERENCES users(id),
    created_at           TIMESTAMPTZ      NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ
);
CREATE INDEX idx_intel_domain ON intelligence_reports (domain);
CREATE INDEX idx_intel_status ON intelligence_reports (status);

CREATE TABLE refugee_camps (
    id                 SERIAL PRIMARY KEY,
    name               VARCHAR(150) NOT NULL,
    region             VARCHAR(80)  NOT NULL,
    district           VARCHAR(80)  NOT NULL,
    capacity           INTEGER      NOT NULL DEFAULT 0,
    current_population INTEGER      NOT NULL DEFAULT 0,
    latitude           NUMERIC(10,7),
    longitude          NUMERIC(10,7),
    is_active          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at         TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE TABLE community_reports (
    id                   SERIAL PRIMARY KEY,
    report_code          VARCHAR(30)  NOT NULL UNIQUE,
    source_type          VARCHAR(40)  NOT NULL DEFAULT 'community_member',
    reporter_name        VARCHAR(120),
    is_anonymous         BOOLEAN      NOT NULL DEFAULT FALSE,
    title                VARCHAR(200) NOT NULL,
    description          TEXT         NOT NULL,
    category             VARCHAR(80),
    region               VARCHAR(80),
    district             VARCHAR(80),
    village              VARCHAR(120),
    latitude             NUMERIC(10,7),
    longitude            NUMERIC(10,7),
    media_notes          VARCHAR(255),
    verification_status  VARCHAR(20)  NOT NULL DEFAULT 'unverified',
    response_status      VARCHAR(20)  NOT NULL DEFAULT 'received',
    response_notes       TEXT,
    reported_at          TIMESTAMPTZ    NOT NULL,
    created_by           INTEGER      REFERENCES users(id) ON DELETE SET NULL,
    created_at           TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ
);

CREATE TABLE institutional_capacity (
    id                      SERIAL PRIMARY KEY,
    record_code             VARCHAR(30)    NOT NULL UNIQUE,
    institution_name        VARCHAR(150)   NOT NULL,
    institution_type        VARCHAR(40)    NOT NULL DEFAULT 'other',
    region                  VARCHAR(80),
    district                VARCHAR(80),
    border_post_id          INTEGER        REFERENCES border_posts(id) ON DELETE SET NULL,
    personnel_count         INTEGER        NOT NULL DEFAULT 0,
    emergency_teams         INTEGER        NOT NULL DEFAULT 0,
    vehicles                INTEGER        NOT NULL DEFAULT 0,
    boats                   INTEGER        NOT NULL DEFAULT 0,
    communication_equipment INTEGER        NOT NULL DEFAULT 0,
    emergency_supplies      TEXT,
    available_budget        NUMERIC(15,2),
    focal_person            VARCHAR(120),
    focal_contact           VARCHAR(80),
    readiness_level         VARCHAR(20)    NOT NULL DEFAULT 'medium',
    notes                   TEXT,
    recorded_at             TIMESTAMPTZ      NOT NULL,
    created_by              INTEGER        REFERENCES users(id) ON DELETE SET NULL,
    created_at              TIMESTAMPTZ      NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ
);

CREATE TABLE public_communications (
    id                    SERIAL PRIMARY KEY,
    message_code          VARCHAR(30)  NOT NULL UNIQUE,
    message_type          VARCHAR(40)  NOT NULL DEFAULT 'public_advisory',
    title                 VARCHAR(200) NOT NULL,
    body                  TEXT         NOT NULL,
    channel               VARCHAR(40)  NOT NULL DEFAULT 'sms',
    target_region         VARCHAR(80),
    target_audience       VARCHAR(200),
    recipients_count      INTEGER      NOT NULL DEFAULT 0,
    acknowledgement_count INTEGER      NOT NULL DEFAULT 0,
    status                VARCHAR(20)  NOT NULL DEFAULT 'draft',
    sent_at               TIMESTAMPTZ,
    created_by            INTEGER      REFERENCES users(id) ON DELETE SET NULL,
    created_at            TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMPTZ
);

CREATE TABLE early_warning_indicators (
    id                  SERIAL PRIMARY KEY,
    indicator_code      VARCHAR(40)    NOT NULL UNIQUE,
    name                VARCHAR(150)   NOT NULL,
    domain              VARCHAR(80)    NOT NULL,
    description         TEXT,
    threshold_value     NUMERIC(15,2),
    threshold_unit      VARCHAR(40),
    current_value       NUMERIC(15,2),
    escalation_level    VARCHAR(20)    NOT NULL DEFAULT 'normal',
    geographical_scope  VARCHAR(120),
    seasonal_note       VARCHAR(200),
    ai_risk_score       NUMERIC(5,2),
    probability_pct     NUMERIC(5,2),
    is_active           BOOLEAN        NOT NULL DEFAULT TRUE,
    updated_by          INTEGER        REFERENCES users(id) ON DELETE SET NULL,
    created_at          TIMESTAMPTZ      NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ
);

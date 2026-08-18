package tz.go.tptc.hsas.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String name = "Human Security Assessment System";
    private String shortName = "HSAS";
    private String corsOrigins = "http://localhost:4200";
    private String demoPassword = "Admin@123";
    private final Jwt jwt = new Jwt();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getShortName() { return shortName; }
    public void setShortName(String shortName) { this.shortName = shortName; }
    public String getCorsOrigins() { return corsOrigins; }
    public void setCorsOrigins(String corsOrigins) { this.corsOrigins = corsOrigins; }
    public String getDemoPassword() { return demoPassword; }
    public void setDemoPassword(String demoPassword) { this.demoPassword = demoPassword; }
    public Jwt getJwt() { return jwt; }

    public static class Jwt {
        private String secret;
        private long expirationMs = 86400000;

        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public long getExpirationMs() { return expirationMs; }
        public void setExpirationMs(long expirationMs) { this.expirationMs = expirationMs; }
    }
}

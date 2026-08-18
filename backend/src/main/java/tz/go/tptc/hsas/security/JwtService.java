package tz.go.tptc.hsas.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import tz.go.tptc.hsas.config.AppProperties;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private final AppProperties properties;

    public JwtService(AppProperties properties) {
        this.properties = properties;
    }

    public String generate(Integer userId, String email) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + properties.getJwt().getExpirationMs());
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claims(Map.of("email", email))
                .issuedAt(now)
                .expiration(exp)
                .signWith(key())
                .compact();
    }

    public Integer parseUserId(String token) {
        Claims claims = Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();
        return Integer.valueOf(claims.getSubject());
    }

    private SecretKey key() {
        byte[] bytes = properties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(bytes, 0, padded, 0, bytes.length);
            bytes = padded;
        }
        return Keys.hmacShaKeyFor(bytes);
    }
}

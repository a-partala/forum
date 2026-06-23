package net.partala.forum.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import net.partala.forum.auth.SecurityUser;
import net.partala.forum.auth.jwt.dto.JwtResponse;
import net.partala.forum.config.JwtProperties;
import net.partala.forum.user.UserRole;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

@Slf4j
@Service
public class JwtService {

    private static final String ROLE_KEY = "role";
    private static final String PURPOSE_KEY = "purpose";
    private final JwtProperties properties;
    private final Clock clock;

    public JwtService(JwtProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
    }

    public JwtResponse generateAccessToken(SecurityUser securityUser) {
        Instant now = Instant.now(clock);
        Instant expire = now.plus(Duration.ofMinutes(properties.expirationMinutes()));

        var token = Jwts.builder()
                .claim(PURPOSE_KEY, TokenPurpose.ACCESS)
                .claim(ROLE_KEY, securityUser.getRole())
                .subject(securityUser.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expire))
                .signWith(getSigningKey())
                .compact();
        return new JwtResponse(token, expire);
    }

    public String extractUsername(Claims claims) {
        return claims.getSubject();
    }

    public Set<UserRole> extractAuthorities(Claims claims) {
        return Set.of(claims.get(ROLE_KEY, UserRole.class));
    }

    public TokenPurpose extractPurpose(Claims claims) {
        return claims.get(PURPOSE_KEY, TokenPurpose.class);
    }

    public Claims parseAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .clock(() -> Date.from(clock.instant()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(properties.secret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

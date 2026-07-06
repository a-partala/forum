package net.partala.forum.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import net.partala.forum.auth.UserPrincipal;
import net.partala.forum.config.JwtProperties;
import net.partala.forum.user.AccountStatus;
import net.partala.forum.user.UserContext;
import net.partala.forum.user.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
public class JwtService {

    private static final String TOKEN_TYPE = "Bearer";
    private static final String ID_KEY = "id";
    private static final String ROLE_KEY = "role";
    private static final String STATUS_KEY = "accountStatus";
    private static final String EMAIL_KEY = "email";
    private static final String PURPOSE_KEY = "purpose";

    private final JwtProperties properties;
    private final Clock clock;

    public JwtService(JwtProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
    }

    public JwtResponse generateAccessToken(UserPrincipal userPrincipal) {
        Objects.requireNonNull(userPrincipal, "user principal is null");

        Instant now = Instant.now(clock);
        Instant expire = now.plus(Duration.ofMinutes(properties.accessExpirationMinutes()));

        var token = Jwts.builder()
                .claim(PURPOSE_KEY, TokenPurpose.ACCESS)
                .claim(ID_KEY,userPrincipal.getContext().id())
                .claim(ROLE_KEY, userPrincipal.getContext().role())
                .claim(STATUS_KEY, userPrincipal.getContext().status())
                .subject(userPrincipal.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expire))
                .signWith(getSigningKey())
                .compact();
        return new JwtResponse(token, TOKEN_TYPE, expire);
    }

    public JwtResponse generateEmailVerificationToken(UserContext userContext, String email) {
        Objects.requireNonNull(userContext, "user is null");

        Instant now = Instant.now(clock);
        Instant expire = now.plus(Duration.ofMinutes(properties.emailVerificationExpirationMinutes()));

        var token = Jwts.builder()
                .claim(PURPOSE_KEY, TokenPurpose.VERIFY_EMAIL)
                .claim(ID_KEY, userContext.id())
                .claim(EMAIL_KEY, email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expire))
                .signWith(getSigningKey())
                .compact();
        return new JwtResponse(token, TOKEN_TYPE, expire);
    }

    public Set<GrantedAuthority> extractAuthorities(Claims claims) {
        return Set.of(UserRole.valueOf(claims.get(ROLE_KEY, String.class)));
    }

    public Long extractId(Claims claims) {
        return claims.get(ID_KEY, Long.class);
    }

    public String extractEmail(Claims claims) {
        return claims.get(EMAIL_KEY, String.class);
    }

    public UserRole extractRole(Claims claims) {
        return UserRole.valueOf(claims.get(ROLE_KEY, String.class));
    }

    public AccountStatus extractStatus(Claims claims) { return AccountStatus.valueOf(claims.get(STATUS_KEY, String.class)); }

    public TokenPurpose extractPurpose(Claims claims) {
        return TokenPurpose.valueOf(claims.get(PURPOSE_KEY, String.class));
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

    public boolean startsWithTargetType(String authHeader) {
        return authHeader.startsWith(TOKEN_TYPE);
    }

    public String trimPrefix(String authHeader) {
        return authHeader.substring(TOKEN_TYPE.length() + 1);
    }
}

package net.partala.forum.auth.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import net.partala.forum.auth.SecurityUser;
import net.partala.forum.config.JwtProperties;
import net.partala.forum.user.UserEntity;
import net.partala.forum.user.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private final String secret = "Gtl1o/+JE/XbbaOr1tyBF/TOtBBSwWALP6GyhBK76s63dVYIaVk2zUXiDLihUdD0oaSBmANhe+JHRlmafNcM1Q==";
    private final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);

    @Test
    void generateAccessToken_ShouldMatchData() {
        var jwtService = new JwtService(
                new JwtProperties(secret, 10),
                clock);
        var userEntity = new UserEntity("user", "user@email.com", "12345678", UserRole.USER);
        var securityUser = new SecurityUser(userEntity);

        var response = jwtService.generateAccessToken(securityUser);

        var claims = jwtService.parseAllClaims(response.token());
        assertAll(
            () -> assertEquals(userEntity.getUsername(), claims.getSubject()),
            () -> assertEquals(jwtService.extractAuthorities(claims), Set.of((GrantedAuthority)userEntity.getRole())),
            () -> assertEquals(TokenPurpose.ACCESS, jwtService.extractPurpose(claims))
        );
    }

    @Test
    void generateAccessToken_Throw() {
        //will generate expired token
        var jwtService = new JwtService(
                new JwtProperties(secret, -10),
                clock);
        var securityUser = new SecurityUser(mock());

        var response = jwtService.generateAccessToken(securityUser);

        Executable executable = () -> jwtService.parseAllClaims(response.token());
        assertThrows(ExpiredJwtException.class, executable);
    }
}
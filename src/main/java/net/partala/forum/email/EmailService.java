package net.partala.forum.email;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import net.partala.forum.auth.jwt.JwtService;
import net.partala.forum.auth.jwt.TokenPurpose;
import net.partala.forum.user.UserContext;
import net.partala.forum.user.UserService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    private final JwtService jwtService;
    private final UserService userService;

    public EmailService(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    public void sendVerificationToken(UserContext userContext, String email) {
        log.info("sendVerificationToken called for user");

        var response = jwtService.generateEmailVerificationToken(userContext, email);

        //TODO: actually send token
        log.info("Token sent: " + response.token());
    }

    void verifyWithToken(String token) {
        log.info("verifyWithToken called");
        try {
            var claims = jwtService.parseAllClaims(token);
            var purpose = jwtService.extractPurpose(claims);

            if(!purpose.equals(TokenPurpose.VERIFY_EMAIL)) {
                throw new IllegalArgumentException("Invalid token purpose");
            }

            var id = jwtService.extractId(claims);
            var email = jwtService.extractEmail(claims);
            userService.verify(id, email);

            log.info("user with id {} verified email {}", id, email);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("Token expired", e);
        } catch (JwtException e) {
            throw new IllegalArgumentException("Invalid token", e);
        }
    }
}

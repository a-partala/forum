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
        if(!userService.isEmailAvailable(email)) {
            throw new IllegalStateException("Email is not available");
        }

        var response = jwtService.generateEmailVerificationToken(userContext, email);

        //TODO: actually send token
        log.info("Token sent: " + response.token());
    }

    void verifyWithToken(String token) {
        try {
            if(token.isBlank()) {
                throw new IllegalArgumentException("Blank token");
            }

            var claims = jwtService.parseAllClaims(token);
            var purpose = jwtService.extractPurpose(claims);

            if(!purpose.equals(TokenPurpose.VERIFY_EMAIL)) {
                throw new IllegalArgumentException("Invalid token purpose");
            }

            userService.verify(jwtService.extractId(claims), jwtService.extractEmail(claims));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("Token expired", e);
        } catch (JwtException e) {
            throw new IllegalArgumentException("Invalid token", e);
        }
    }
}

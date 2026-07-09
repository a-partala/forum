package net.partala.forum.email;

import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;
import net.partala.forum.auth.annotation.EmailValidation;
import net.partala.forum.user.UserContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verify(@PathParam("token") String token) {
        log.info("verify called");

        emailService.verifyWithToken(token);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/send-verification-link")
    public ResponseEntity<Void> sendVerificationToken(@RequestBody @EmailValidation String email,
                                                   @AuthenticationPrincipal UserContext userContext) {
        log.info("sendVerificationToken called for user with id {}", userContext.id());

        emailService.sendVerificationToken(userContext, email);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

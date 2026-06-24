package net.partala.forum.auth;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.partala.forum.auth.jwt.JwtResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid
                                         RegistrationRequest request) {
        log.info("register called with {}:{}", request.username(), request.email());

        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody @Valid
                                          LoginRequest request) {
        log.info("login called with {}", request.login());

        var response = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

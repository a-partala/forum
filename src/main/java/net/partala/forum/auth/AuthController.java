package net.partala.forum.auth;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.partala.forum.auth.jwt.JwtResponse;
import net.partala.forum.user.UserResponse;
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
    public ResponseEntity<UserResponse> register(@RequestBody @Valid
                                         RegistrationRequest request) {
        log.info("register called for {}:{}", request.username(), request.email());

        var response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody @Valid
                                          LoginRequest request) {
        log.info("login called for \"{}\"", request.login());

        var response = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

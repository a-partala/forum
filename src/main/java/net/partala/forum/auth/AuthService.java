package net.partala.forum.auth;

import lombok.extern.slf4j.Slf4j;
import net.partala.forum.auth.jwt.JwtService;
import net.partala.forum.auth.jwt.JwtResponse;
import net.partala.forum.email.EmailService;
import net.partala.forum.user.UserContext;
import net.partala.forum.user.UserResponse;
import net.partala.forum.user.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final EmailService emailService;
    private final PasswordEncoder encoder;

    AuthService(AuthenticationManager authenticationManager, JwtService jwtService, UserService userService, EmailService emailService, PasswordEncoder encoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.emailService = emailService;
        this.encoder = encoder;
    }

    UserResponse register(RegistrationRequest request) {
        var response = userService.createUser(new RegistrationRequest(
                request.username(),
                request.email(),
                encoder.encode(request.password())
        ));
        emailService.sendVerificationToken(
                new UserContext(response.id(), response.role(), response.status()),
                request.email());
        return response;
    }

    JwtResponse login(LoginRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(
                request.login(),
                request.password());
        var auth = authenticationManager.authenticate(authToken);
        var securityUser = (UserPrincipal) auth.getPrincipal();
        return jwtService.generateAccessToken(securityUser);
    }
}

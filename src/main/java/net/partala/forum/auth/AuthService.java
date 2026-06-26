package net.partala.forum.auth;

import lombok.extern.slf4j.Slf4j;
import net.partala.forum.auth.jwt.JwtService;
import net.partala.forum.auth.jwt.JwtResponse;
import net.partala.forum.user.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final PasswordEncoder encoder;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService, UserService userService, PasswordEncoder encoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.encoder = encoder;
    }

    public void register(RegistrationRequest request) {
        userService.createUser(new RegistrationRequest(
                request.username(),
                request.email(),
                encoder.encode(request.password())
        ));
    }

    public JwtResponse login(LoginRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(
                request.login(),
                request.password());
        var auth = authenticationManager.authenticate(authToken);
        var securityUser = (SecurityUser) auth.getPrincipal();
        return jwtService.generateAccessToken(securityUser);
    }
}

package net.partala.forum.auth;

import net.partala.forum.BaseIntegrationTest;
import net.partala.forum.user.UserRepository;
import net.partala.forum.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class UserPrincipalServiceTest extends BaseIntegrationTest {

    private final RegistrationRequest request = new RegistrationRequest("user", "user@gmail.com", "12345678");
    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserPrincipalService userPrincipalService;

    @Test
    void loadUser_DontThrowException_WhenLoadByUsername() {
        authService.register(request);

        Executable executable = () -> userPrincipalService.loadUserByUsername(request.username());

        assertDoesNotThrow(executable);
    }

    @Test
    void loadUser_DontThrowException_WhenLoadByEmail() {
        var response = authService.register(request);
        var savedUser = userRepository.findById(response.id());
        savedUser.get().setEmail(request.email());
        userRepository.save(savedUser.get());

        Executable executable = () -> userPrincipalService.loadUserByUsername(request.email());

        assertDoesNotThrow(executable);
    }

    @Test
    void loadUser_ThrowUsernameNotFoundException_WhenLoginDoesNotExist() {
        authService.register(request);

        Executable executable = () -> userPrincipalService.loadUserByUsername("some.login");

        assertThrows(UsernameNotFoundException.class, executable);
    }

    @Test
    void loadUser_ThrowUsernameNotFoundException_WhenLoginIsEmpty() {
        authService.register(request);

        Executable executable = () -> userPrincipalService.loadUserByUsername("");

        assertThrows(UsernameNotFoundException.class, executable);
    }
}
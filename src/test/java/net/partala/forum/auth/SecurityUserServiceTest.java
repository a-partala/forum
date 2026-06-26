package net.partala.forum.auth;

import net.partala.forum.BaseIntegrationTest;
import net.partala.forum.user.UserEntity;
import net.partala.forum.user.UserRepository;
import net.partala.forum.user.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class SecurityUserServiceTest extends BaseIntegrationTest {

    private final RegistrationRequest request = new RegistrationRequest("user", "user@gmail.com", "12345678");
    @Autowired
    private AuthService authService;
    @Autowired
    private SecurityUserService securityUserService;

    @Test
    void loadUser_DontThrowException_WhenLoadByUsername() {
        authService.register(request);

        Executable executable = () -> securityUserService.loadUserByUsername(request.username());

        assertDoesNotThrow(executable);
    }

    @Test
    void loadUser_DontThrowException_WhenLoadByEmail() {
        authService.register(request);

        Executable executable = () -> securityUserService.loadUserByUsername(request.email());

        assertDoesNotThrow(executable);
    }

    @Test
    void loadUser_ThrowUsernameNotFoundException_WhenLoginDoesNotExist() {
        authService.register(request);

        Executable executable = () -> securityUserService.loadUserByUsername("some.login");

        assertThrows(UsernameNotFoundException.class, executable);
    }

    @Test
    void loadUser_ThrowUsernameNotFoundException_WhenLoginIsEmpty() {
        authService.register(request);

        Executable executable = () -> securityUserService.loadUserByUsername("");

        assertThrows(UsernameNotFoundException.class, executable);
    }
}
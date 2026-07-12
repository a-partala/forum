package net.partala.forum.auth;

import net.partala.forum.email.EmailService;
import net.partala.forum.user.AccountStatus;
import net.partala.forum.user.UserResponse;
import net.partala.forum.user.UserRole;
import net.partala.forum.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_EncodePasswordBeforeUserCreation() {
        String encodedPassword = "encoded";
        when(encoder.encode(any())).thenReturn(encodedPassword);
        when(userService.createUser(any())).thenReturn(new UserResponse(1L, "", "", UserRole.USER, AccountStatus.UNVERIFIED, LocalDateTime.now()));

        authService.register(RegistrationRequest.empty());

        ArgumentCaptor<RegistrationRequest> captor = ArgumentCaptor.forClass(RegistrationRequest.class);
        verify(userService).createUser(captor.capture());
        var sentRequest = captor.getValue();
        assertEquals(encodedPassword, sentRequest.password());
    }
}
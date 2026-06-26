package net.partala.forum.user;

import net.partala.forum.auth.RegistrationRequest;
import net.partala.forum.exception.AlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_SaveWithAdminRole_WhenUserFirst() {
        var request = new RegistrationRequest("user", "user@email.com", "password");
        when(userRepository.existsBy()).thenReturn(false);

        userService.createUser(request);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());

        var savedUser = captor.getValue();
        assertEquals(UserRole.ADMIN, savedUser.getRole());
    }

    @Test
    void createUser_SaveWithUserRole_WhenUserIsNotFirst() {
        var request = new RegistrationRequest("user", "user@email.com", "password");
        when(userRepository.existsBy()).thenReturn(true);
        when(userRepository.findByUsernameOrEmail(any(), any())).thenReturn(Optional.empty());

        userService.createUser(request);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        var savedUser = captor.getValue();
        assertEquals(UserRole.USER, savedUser.getRole());
    }

    @Test
    void createUser_ThrowAlreadyExists_WhenUsernameOrEmailExist() {
        var request = new RegistrationRequest("user", "user@email.com", "password");
        when(userRepository.existsBy()).thenReturn(true);
        when(userRepository.findByUsernameOrEmail(any(), any())).thenReturn(Optional.of(new UserEntity()));

        Executable executable = () -> userService.createUser(request);

        assertThrows(AlreadyExistsException.class, executable);
    }
}
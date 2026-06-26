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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_SaveWithAdminRole_WhenUserFirst() {
        when(userRepository.existsBy()).thenReturn(false);

        userService.createUser(RegistrationRequest.empty());

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        var savedUser = captor.getValue();
        assertEquals(UserRole.ADMIN, savedUser.getRole());
    }

    @Test
    void createUser_SaveWithUserRole_WhenUserIsNotFirst() {
        when(userRepository.existsBy()).thenReturn(true);
        when(userRepository.findByUsernameOrEmail(any(), any())).thenReturn(Optional.empty());

        userService.createUser(mock());

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        var savedUser = captor.getValue();
        assertEquals(UserRole.USER, savedUser.getRole());
    }

    @Test
    void createUser_ThrowIllegalArgumentException_WhenRequestIsNull() {
        Executable executable = () -> userService.createUser(null);

        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void createUser_ThrowAlreadyExistsException_WhenUsernameOrEmailExist() {
        when(userRepository.existsBy()).thenReturn(true);
        when(userRepository.findByUsernameOrEmail(any(), any())).thenReturn(Optional.of(new UserEntity()));

        Executable executable = () -> userService.createUser(mock());

        assertThrows(AlreadyExistsException.class, executable);
    }
}
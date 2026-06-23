package net.partala.forum.user;

import lombok.extern.slf4j.Slf4j;
import net.partala.forum.auth.dto.RegistrationRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserService {

    private final PasswordEncoder encoder;
    private final UserRepository repository;

    public UserService(PasswordEncoder encoder, UserRepository repository) {
        this.encoder = encoder;
        this.repository = repository;
    }

    @Transactional
    public void createUser(RegistrationRequest request) {

        UserRole role = UserRole.USER;

        if(!repository.existsBy()) {
            role = UserRole.ADMIN;
        }

        var encodedPassword = encoder.encode(request.password());
        var entity = new UserEntity(request.username(), request.email(), encodedPassword, role);
        repository.save(entity);
    }

    public boolean isEmailAvailable(String email) {

        return repository.findByEmail(email).isEmpty();
    }

    public boolean isUsernameAvailable(String username) {

        return repository.findByUsername(username).isEmpty();
    }
}

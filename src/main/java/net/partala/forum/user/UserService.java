package net.partala.forum.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.partala.forum.auth.RegistrationRequest;
import net.partala.forum.exception.AlreadyExistsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UserResponse createUser(RegistrationRequest request) {

        if(request == null) {
            throw new IllegalArgumentException("Empty registration request");
        }

        UserRole role = UserRole.USER;

        if(!repository.existsBy()) {
            role = UserRole.ADMIN;
        } else if(repository.findByUsernameOrEmail(request.username(), request.email()).isPresent()) {
            throw new AlreadyExistsException("User with this username or email already exists");
        }

        var entity = new UserEntity(
                request.username(),
                request.email(),
                request.password(),
                role);
        var savedUser = repository.save(entity);
        return UserResponse.of(savedUser);
    }

    public UserResponse getUserById(Long id) {
        var entity = repository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "No user with id " + id));
        return UserResponse.of(entity);
    }

    public UserEntity getReferenceById(Long id) {
        return repository.getReferenceById(id);
    }

    public boolean isEmailAvailable(String email) {

        return repository.findByEmail(email).isEmpty();
    }

    public boolean isUsernameAvailable(String username) {

        return repository.findByUsername(username).isEmpty();
    }
}

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

        var role = UserRole.USER;
        var status = AccountStatus.UNVERIFIED;

        if(!repository.existsBy()) {
            role = UserRole.ADMIN;
        } else if(repository.findByUsernameOrEmail(request.username(), request.email()).isPresent()) {
            throw new AlreadyExistsException("User with this username or email already exists");
        }

        var entity = new UserEntity(
                request.username(),
                null,
                request.password(),
                role,
                status);
        var savedUser = repository.save(entity);
        return UserResponse.of(savedUser);
    }

    public UserResponse getUserById(Long id) {
        var entity = getEntityById(id);
        return UserResponse.of(entity);
    }

    public UserEntity getReferenceById(Long id) {
        return repository.getReferenceById(id);
    }

    private UserEntity getEntityById(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "No user with id " + id));
    }

    public boolean isEmailAvailable(String email) {
        log.info("Is email available called with: {}", email);
        return repository.findByEmail(email).isEmpty();
    }

    public boolean isUsernameAvailable(String username) {

        log.info("Is username available called with: {}", username);
        return repository.findByUsername(username).isEmpty();
    }

    @Transactional
    public void verify(Long id, String email) {

        var targetEmailUser = repository.findByEmail(email);
        if(targetEmailUser.isPresent()) {
            if(targetEmailUser.get().getId().equals(id)) {
                throw new IllegalStateException("Email is already verified");
            }
            throw new IllegalStateException("Email is not available");
        }

        var user = getEntityById(id);
        user.setEmail(email);
        user.setStatus(AccountStatus.ACTIVE);
        repository.save(user);
    }
}

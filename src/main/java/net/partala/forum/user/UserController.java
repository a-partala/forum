package net.partala.forum.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import net.partala.forum.auth.annotation.EmailValidation;
import net.partala.forum.auth.annotation.UsernameValidation;
import net.partala.forum.common.AvailabilityResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("id") @NotNull @Positive Long id) {

        log.info("getUserById called with id {}", id);
        var response = userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/check-availability/username")
    public ResponseEntity<AvailabilityResponse> isUsernameAvailable(@RequestBody @UsernameValidation String username) {

        log.info("isUsernameAvailable called with {}", username);
        boolean isAvailable = userService.isUsernameAvailable(username);
        return ResponseEntity.status(HttpStatus.OK).body(new AvailabilityResponse(isAvailable));
    }

    @PostMapping("/check-availability/email")
    public ResponseEntity<AvailabilityResponse> isEmailAvailable(@RequestBody @EmailValidation String email) {

        log.info("isEmailAvailable called with {}", email);
        boolean isAvailable = userService.isEmailAvailable(email);
        return ResponseEntity.status(HttpStatus.OK).body(new AvailabilityResponse(isAvailable));
    }
}

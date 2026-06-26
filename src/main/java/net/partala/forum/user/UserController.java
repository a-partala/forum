package net.partala.forum.user;

import lombok.extern.slf4j.Slf4j;
import net.partala.forum.auth.annotation.EmailValidation;
import net.partala.forum.auth.annotation.UsernameValidation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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

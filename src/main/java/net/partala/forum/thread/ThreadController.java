package net.partala.forum.thread;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import net.partala.forum.thread.dto.CreateThreadRequest;
import net.partala.forum.thread.dto.ThreadResponse;
import net.partala.forum.user.UserContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/threads")
public class ThreadController {

    private final ThreadService threadService;

    public ThreadController(ThreadService threadService) {
        this.threadService = threadService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThreadResponse> getThreadById(@PathVariable("id") @NotNull @Positive Long id) {

        log.info("getThreadById called with id {}", id);
        var response = threadService.getThreadById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<ThreadResponse> createThread(@RequestBody @Valid CreateThreadRequest request,
                                                       @AuthenticationPrincipal UserContext userContext) {
        log.info("called create thread with data: {}", request);

        var response = threadService.createThread(request, userContext);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

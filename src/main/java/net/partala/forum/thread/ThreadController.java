package net.partala.forum.thread;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import net.partala.forum.thread.dto.CreateThreadRequest;
import net.partala.forum.thread.dto.ThreadResponse;
import net.partala.forum.thread.dto.UpdateThreadRequest;
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
    public ResponseEntity<ThreadResponse> getThreadById(@PathVariable("id") @Positive Long id) {

        log.info("called getThreadById with id {}", id);
        var response = threadService.getThreadById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<ThreadResponse> createThread(@RequestBody @Valid CreateThreadRequest request,
                                                       @AuthenticationPrincipal UserContext userContext) {
        log.info("called createThread thread with data: {}", request);

        var response = threadService.createThread(request, userContext);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ThreadResponse> updateThread(@PathVariable("id") @Positive Long id,
                                                       @RequestBody @Valid UpdateThreadRequest request,
                                                       @AuthenticationPrincipal UserContext userContext) {

        log.info("called updateThread thread with data: {}", request);

        var response = threadService.updateThread(id, request, userContext);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteThread(@PathVariable("id") @Positive Long id,
                                             @AuthenticationPrincipal UserContext userContext) {

        log.info("called deleteThread thread with id {}", id);

        threadService.deleteThread(id, userContext);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<ThreadResponse> closeThread(@PathVariable("id") @Positive Long id,
                                                      @AuthenticationPrincipal UserContext userContext) {

        log.info("called closeThread thread with id {}", id);

        var response = threadService.closeThread(id, userContext);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

package net.partala.forum.comment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import net.partala.forum.comment.dto.CommentResponse;
import net.partala.forum.comment.dto.CreateCommentRequest;
import net.partala.forum.common.PageMapper;
import net.partala.forum.user.UserContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Slf4j
@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("threads/{id}/comments")
    public ResponseEntity<List<CommentResponse>> getThreadComments(@PathVariable("id") @Positive Long threadId,
                                                             @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                             @RequestParam(value = "pageId", required = false) Integer pageId) {
        log.info("getThreadComments called with thread id {}", threadId);

        var response = commentService.getThreadComments(threadId, PageMapper.pageableOf(pageSize, pageId));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("comments/{id}/replies")
    public ResponseEntity<List<CommentResponse>> getCommentReplies(@PathVariable("id") @Positive Long commentId,
                                                                   @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                                   @RequestParam(value = "pageId", required = false) Integer pageId) {
        log.info("getCommentReplies called with comment id {}", commentId);

        var response = commentService.getCommentReplies(commentId, PageMapper.pageableOf(pageSize, pageId));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("threads/{id}/comments")
    public ResponseEntity<CommentResponse> commentThread(@PathVariable("id") @Positive Long threadId,
                                                         @RequestBody @Valid CreateCommentRequest request,
                                                       @AuthenticationPrincipal UserContext userContext) {
        log.info("commentThread called with data: {}", request);

        var response = commentService.commentThread(request, threadId, userContext);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("comments/{id}/replies")
    public ResponseEntity<CommentResponse> replyComment(@PathVariable("id") @Positive Long commentId,
                                                        @RequestBody @Valid CreateCommentRequest request,
                                                       @AuthenticationPrincipal UserContext userContext) {
        log.info("replyComment called with commentId={} and data: {}", commentId, request);

        var response = commentService.replyComment(request, commentId, userContext);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

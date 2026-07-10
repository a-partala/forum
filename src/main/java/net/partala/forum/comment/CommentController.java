package net.partala.forum.comment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import net.partala.forum.comment.dto.CommentResponse;
import net.partala.forum.comment.dto.CommentContentRequest;
import net.partala.forum.common.PageMapper;
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
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("threads/{id}/comments")
    public ResponseEntity<List<CommentResponse>> getThreadComments(@PathVariable("id") @Positive Long threadId,
                                                             @RequestParam(value = "pageSize", required = false) @Positive Integer pageSize,
                                                             @RequestParam(value = "pageId", required = false) @Positive Integer pageId) {
        log.info("getThreadComments called with thread id {}", threadId);

        var response = commentService.getThreadComments(threadId, PageMapper.pageableOf(pageId, pageSize));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("comments/{id}/replies")
    public ResponseEntity<List<CommentResponse>> getCommentReplies(@PathVariable("id") @Positive Long commentId,
                                                                   @RequestParam(value = "pageSize", required = false) @Positive Integer pageSize,
                                                                   @RequestParam(value = "pageId", required = false) @Positive Integer pageId) {
        log.info("getCommentReplies called with comment id {}", commentId);

        var response = commentService.getCommentReplies(commentId, PageMapper.pageableOf(pageId, pageSize));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("threads/{id}/comments")
    public ResponseEntity<CommentResponse> commentThread(@PathVariable("id") @Positive Long threadId,
                                                         @RequestBody @Valid CommentContentRequest request,
                                                       @AuthenticationPrincipal UserContext userContext) {
        log.info("commentThread called with data: {}", request);

        var response = commentService.commentThread(request, threadId, userContext);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("comments/{id}/replies")
    public ResponseEntity<CommentResponse> replyComment(@PathVariable("id") @Positive Long commentId,
                                                        @RequestBody @Valid CommentContentRequest request,
                                                       @AuthenticationPrincipal UserContext userContext) {
        log.info("replyComment called with commentId={} and data: {}", commentId, request);

        var response = commentService.replyComment(request, commentId, userContext);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("comments/{id}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable("id") @Positive Long id,
                                                       @RequestBody @Valid CommentContentRequest request,
                                                       @AuthenticationPrincipal UserContext userContext) {

        log.info("called updateComment thread with data: {}", request);

        var response = commentService.updateComment(id, request, userContext);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") @Positive Long id,
                                             @AuthenticationPrincipal UserContext userContext) {

        log.info("called deleteComment thread with id {}", id);

        commentService.deleteComment(id, userContext);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

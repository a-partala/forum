package net.partala.forum.vote;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import net.partala.forum.comment.CommentEntity;
import net.partala.forum.thread.ThreadEntity;
import net.partala.forum.user.UserContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class VoteController {

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PutMapping("/threads/{id}/vote")
    public ResponseEntity<Void> voteThread(@PathVariable("id") @Positive Long id,
                                              @RequestBody @NotNull VoteType voteType,
                                              @AuthenticationPrincipal UserContext userContext) {

        voteService.vote(userContext, id, voteType, ThreadEntity.class);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/threads/{id}/vote")
    public ResponseEntity<Void> cancelVoteThread(@PathVariable("id") @Positive Long id,
                                                @AuthenticationPrincipal UserContext userContext) {

        voteService.cancelVote(userContext, id, ThreadEntity.class);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @PutMapping("/comments/{id}/vote")
    public ResponseEntity<Void> voteComment(@PathVariable("id") @Positive Long id,
                                            @RequestBody @NotNull VoteType voteType,
                                            @AuthenticationPrincipal UserContext userContext) {

        voteService.vote(userContext, id, voteType, CommentEntity.class);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/comments/{id}/vote")
    public ResponseEntity<Void> cancelVoteComment(@PathVariable("id") @Positive Long id,
                                                  @AuthenticationPrincipal UserContext userContext) {

        voteService.cancelVote(userContext, id, CommentEntity.class);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

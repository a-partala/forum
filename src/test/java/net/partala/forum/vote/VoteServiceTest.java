package net.partala.forum.vote;

import net.partala.forum.BaseIntegrationTest;
import net.partala.forum.auth.RegistrationRequest;
import net.partala.forum.comment.CommentController;
import net.partala.forum.comment.CommentEntity;
import net.partala.forum.comment.dto.CommentContentRequest;
import net.partala.forum.common.RatableEntity;
import net.partala.forum.realm.RealmController;
import net.partala.forum.realm.dto.CreateRealmRequest;
import net.partala.forum.thread.ThreadController;
import net.partala.forum.thread.ThreadEntity;
import net.partala.forum.thread.dto.CreateThreadRequest;
import net.partala.forum.user.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class VoteServiceTest extends BaseIntegrationTest {

    private final CommentContentRequest commentRequest = new CommentContentRequest("comment");
    @Autowired
    private UserService userService;
    @Autowired
    private RealmController realmController;
    @Autowired
    private ThreadController threadController;
    @Autowired
    private CommentController commentController;
    @Autowired
    private VoteService voteService;

    private int nCounter = 0;
    private Long currentAdminId;

    @Test
    void vote_ThrowIllegalStateException_WhenUserIsNotActive() {
        var threadId = createAdminAndThreadAndGetId();
        var userId = userService.createUser(new RegistrationRequest("user" + getNextN(), getNextN() + "user@email.com", "password")).id();

        List<Executable> assertions = new ArrayList<>(AccountStatus.values().length);
        for(var status : AccountStatus.values()) {
            if(status.equals(AccountStatus.ACTIVE)) {
                continue;
            }

            var userContext = new UserContext(userId, UserRole.USER, AccountStatus.UNVERIFIED);
            Executable executable = () -> voteService.vote(userContext, threadId, VoteType.UP, ThreadEntity.class);
            Executable assertion = () -> assertThrows(IllegalStateException.class, executable);

            assertions.add(assertion);
        }

        assertAll(assertions);
    }

    @Test
    void vote_ThrowIllegalStateException_WhenUserIsCreatorOfEntity() {
        var threadId = createAdminAndThreadAndGetId();
        var userContext = new UserContext(currentAdminId, UserRole.USER, AccountStatus.ACTIVE);

        Executable executable = () -> voteService.vote(userContext, threadId, VoteType.UP, ThreadEntity.class);

        assertThrows(IllegalStateException.class, executable);
    }

    @Test
    void getRating_ReturnCorrect_ForThread() {
        int expectedRating = 0;
        var threadId = createAdminAndThreadAndGetId();

        voteAndGetActorIds(threadId, 3, VoteType.UP, ThreadEntity.class);
        expectedRating += 3;

        voteAndGetActorIds(threadId, 2, VoteType.DOWN, ThreadEntity.class);
        expectedRating -= 2;

        var actors = voteAndGetActorIds(threadId, 2, VoteType.UP, ThreadEntity.class);
        changeVotes(actors, threadId, VoteType.DOWN, ThreadEntity.class);
        expectedRating -= 2;

        actors = voteAndGetActorIds(threadId, 2, VoteType.UP, ThreadEntity.class);
        changeVotes(actors, threadId, VoteType.UP, ThreadEntity.class);
        expectedRating += 2;

        actors = voteAndGetActorIds(threadId, 2, VoteType.DOWN, ThreadEntity.class);
        cancelVotes(actors, threadId, ThreadEntity.class);



        var rating = voteService.getRating(threadId, ThreadEntity.class);

        assertEquals(expectedRating, rating);
    }

    @Test
    void getRating_ReturnCorrect_ForComment() {
        int expectedRating = 0;
        var threadId = createAdminAndThreadAndGetId();
        Long commentId = createCommentsAndGetIds(1, threadId).getFirst();

        voteAndGetActorIds(commentId, 3, VoteType.UP, CommentEntity.class);
        expectedRating += 3;

        voteAndGetActorIds(commentId, 2, VoteType.DOWN, CommentEntity.class);
        expectedRating -= 2;

        var actors = voteAndGetActorIds(commentId, 2, VoteType.UP, CommentEntity.class);
        changeVotes(actors, commentId, VoteType.DOWN, CommentEntity.class);
        expectedRating -= 2;

        actors = voteAndGetActorIds(commentId, 2, VoteType.UP, CommentEntity.class);
        changeVotes(actors, commentId, VoteType.UP, CommentEntity.class);
        expectedRating += 2;

        actors = voteAndGetActorIds(commentId, 2, VoteType.DOWN, CommentEntity.class);
        cancelVotes(actors, commentId, CommentEntity.class);



        var rating = voteService.getRating(commentId, CommentEntity.class);

        assertEquals(expectedRating, rating);
    }



    private List<UserContext> voteAndGetActorIds(Long entityId, int times, VoteType type, Class<? extends RatableEntity> entityClass) {

        var userContexts = new ArrayList<UserContext>(times);
        for (int i = 0; i < times; i++) {

            var userId = userService.createUser(new RegistrationRequest("user" + getNextN(), getNextN() + "user@email.com", "password")).id();
            var userContext = new UserContext(userId, UserRole.USER, AccountStatus.ACTIVE);
            voteService.vote(userContext, entityId, type, entityClass);

            userContexts.add(userContext);
        }

        return userContexts;
    }

    private void changeVotes(List<UserContext> actors, Long entityId, VoteType type, Class<? extends RatableEntity> entityClass) {

        for (int i = 0; i < actors.size(); i++) {

            var userContext = actors.get(i);
            voteService.vote(userContext, entityId, type, entityClass);
        }
    }

    private void cancelVotes(List<UserContext> actors, Long entityId, Class<? extends RatableEntity> entityClass) {

        for (int i = 0; i < actors.size(); i++) {
            var userContext = actors.get(i);
            voteService.cancelVote(userContext, entityId, entityClass);
        }
    }

    private List<Long> createCommentsAndGetIds(int amount, Long threadId) {

        var adminContext = new UserContext(currentAdminId, UserRole.ADMIN, AccountStatus.ACTIVE);
        var commentIds = new ArrayList<Long>(amount);
        for (int i = 0; i < amount; i++) {
            var id = commentController.commentThread(threadId, commentRequest, adminContext).getBody().id();
            commentIds.add(id);
        }

        return commentIds;
    }

    private Long createAdminAndThreadAndGetId() {
        currentAdminId = userService.createUser(new RegistrationRequest("admin", "admin@email.com", "password")).id();
        var adminContext = new UserContext(currentAdminId, UserRole.ADMIN, AccountStatus.ACTIVE);
        var realmId = realmController.createRealm(new CreateRealmRequest("realm", "", currentAdminId, null), adminContext).getBody().id();
        var threadId = threadController.createThread(new CreateThreadRequest("thread", "content", realmId), adminContext).getBody().id();

        return threadId;
    }

    private int getNextN() {
        return nCounter++;
    }
}
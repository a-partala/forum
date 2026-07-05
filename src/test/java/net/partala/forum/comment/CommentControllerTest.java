package net.partala.forum.comment;

import net.partala.forum.BaseIntegrationTest;
import net.partala.forum.auth.AuthController;
import net.partala.forum.auth.RegistrationRequest;
import net.partala.forum.comment.dto.CommentResponse;
import net.partala.forum.comment.dto.CreateCommentRequest;
import net.partala.forum.realm.RealmController;
import net.partala.forum.realm.dto.CreateRealmRequest;
import net.partala.forum.thread.ThreadController;
import net.partala.forum.thread.dto.CreateThreadRequest;
import net.partala.forum.user.UserContext;
import net.partala.forum.user.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentControllerTest extends BaseIntegrationTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private AuthController authController;
    @Autowired
    private RealmController realmController;
    @Autowired
    private ThreadController threadController;
    @Autowired
    private CommentController commentController;

    @Test
    @DisplayName("Should return correct hasReplies values during full user scenario")
    void getThreadComments() throws Exception {
        var user = authController.register(new RegistrationRequest("admin", "admin@gmail.com", "12345678"));
        var userContext = new UserContext(user.getBody().id(), UserRole.ADMIN);
        var realm = realmController.createRealm(new CreateRealmRequest("name", "description", user.getBody().id(), null), userContext);
        var thread = threadController.createThread(new CreateThreadRequest("title", "content", realm.getBody().id()), userContext);
        var commentRequest = new CreateCommentRequest("content");
        var commentWithReply = commentController.commentThread(thread.getBody().id(), commentRequest, userContext);
        var commentWithoutReply = commentController.commentThread(thread.getBody().id(), commentRequest, userContext);
        commentController.replyComment(commentWithReply.getBody().id(), commentRequest, userContext);


        var result = mockMvc.perform(get("/threads/"+thread.getBody().id()+"/comments")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        var comments = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<CommentResponse>>(){});


        Map<Long, Boolean> expectedHasRepliesById = new HashMap<>();
        expectedHasRepliesById.put(commentWithReply.getBody().id(), true);
        expectedHasRepliesById.put(commentWithoutReply.getBody().id(), false);
        assertEquals(2, comments.size());
        for (CommentResponse comment : comments) {
            assertEquals(expectedHasRepliesById.get(comment.id()), comment.hasReplies());
        }
    }
}
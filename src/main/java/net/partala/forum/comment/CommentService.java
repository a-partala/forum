package net.partala.forum.comment;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.partala.forum.comment.dto.CommentResponse;
import net.partala.forum.comment.dto.CreateCommentRequest;
import net.partala.forum.user.UserContext;
import net.partala.forum.user.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.*;

@Slf4j
@Service
public class CommentService {

    private final UserService userService;
    private final CommentRepository repository;

    public CommentService(UserService userService, CommentRepository repository) {
        this.userService = userService;
        this.repository = repository;
    }

    List<CommentResponse> getThreadComments(Long threadId, Pageable pageable) {
        return convertToResponse(repository.getThreadComments(threadId, pageable));
    }

    List<CommentResponse> getCommentReplies(Long commentId, Pageable pageable) {
        return convertToResponse(repository.getCommentReplies(commentId, pageable));
    }

    List<CommentResponse> convertToResponse(List<CommentEntity> entities) {

        if(entities.isEmpty()) {
            return List.of();
        }

        Set<Long> replied = repository.selectReplied(entities.stream().map(CommentEntity::getId).toList());
        return entities.stream()
                .map(entity ->
                        CommentResponse.of(entity, replied.contains(entity.getId())))
                .toList();
    }

    private CommentResponse createComment(CreateCommentRequest request,
                                          Long threadId,
                                          Long parentId,
                                          UserContext userContext) {

        var creator = userService.getReferenceById(userContext.id());
        var commentEntity = new CommentEntity(request.content(), creator, threadId, parentId);

        var savedComment = repository.save(commentEntity);
        return CommentResponse.of(savedComment);
    }

    CommentResponse commentThread(CreateCommentRequest request, Long threadId, UserContext userContext) {
        return createComment(request, threadId, null, userContext);
    }

    CommentResponse replyComment(CreateCommentRequest request, Long parentId, UserContext userContext) {
        var parent = repository.findById(parentId).orElseThrow(
                () -> new EntityNotFoundException("No comment with id " + parentId)
        );
        var threadId = parent.getThreadId();
        return createComment(request, threadId, parentId, userContext);
    }
}

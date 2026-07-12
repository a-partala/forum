package net.partala.forum.comment;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.partala.forum.comment.dto.CommentResponse;
import net.partala.forum.comment.dto.CommentContentRequest;
import net.partala.forum.thread.ThreadService;
import net.partala.forum.user.UserContext;
import net.partala.forum.user.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class CommentService {

    private final UserService userService;
    private final ThreadService threadService;
    private final CommentRepository repository;

    public CommentService(UserService userService, ThreadService threadService, CommentRepository repository) {
        this.userService = userService;
        this.threadService = threadService;
        this.repository = repository;
    }

    public CommentResponse getCommentById(Long id) {
        return CommentResponse.of(getEntityById(id));
    }

    private CommentEntity getEntityById(Long id) {
        return repository.findByIdAndDeletedFalse(id).orElseThrow(() ->
                new EntityNotFoundException("No comment with id " + id));
    }

    List<CommentResponse> getThreadComments(Long threadId, Pageable pageable) {
        return convertToResponse(
                repository.getThreadComments(threadId, pageable)
        );
    }

    List<CommentResponse> getCommentReplies(Long commentId, Pageable pageable) {
        return convertToResponse(
                repository.getCommentReplies(commentId, pageable)
        );
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

    private boolean hasReplies(Long commentId) {
        return !repository.selectReplied(List.of(commentId)).isEmpty();
    }

    @Transactional
    private CommentResponse createComment(CommentContentRequest request,
                                          Long threadId,
                                          Long parentId,
                                          UserContext userContext) {

        var actor = new CommentActor(userContext);
        actor.canCreate()
                .throwIfCannot();

        var creator = userService.getEntityById(userContext.id());
        var commentEntity = new CommentEntity(request.content(), creator, threadId, parentId);

        var savedComment = repository.save(commentEntity);
        return CommentResponse.of(savedComment);
    }

    CommentResponse commentThread(CommentContentRequest request, Long threadId, UserContext userContext) {
        return createComment(request, threadId, null, userContext);
    }

    CommentResponse replyComment(CommentContentRequest request, Long parentId, UserContext userContext) {
        var parent = getEntityById(parentId);
        var threadId = parent.getThreadId();
        return createComment(request, threadId, parentId, userContext);
    }

    @Transactional
    public CommentResponse updateComment(Long id,
                                       CommentContentRequest request,
                                       UserContext userContext) {

        var comment = getEntityById(id);

        if(comment.isDeleted()) {
            throw new IllegalStateException("Comment is deleted");
        }

        if(request.isSameData(comment)) {
            return CommentResponse.of(comment);
        }

        var actor = new CommentActor(userContext);

        actor.canEdit(comment)
                .throwIfCannot();

        comment.setContent(request.content());
        comment.setEdited(true);
        var saved = repository.save(comment);

        log.info("comment with id {} updated", id);
        return CommentResponse.of(saved);
    }

    @Transactional
    public void deleteComment(Long id,
                             UserContext userContext) {

        var comment = getEntityById(id);

        if(comment.isDeleted()) {
            return;
        }

        var actor = new CommentActor(userContext);

        actor.canDelete(comment, threadService.getBranchDetails(comment.getThreadId()))
                .throwIfCannot();

        if(hasReplies(id)) {
            comment.setDeleted(true);
            comment.setContent("");
            repository.save(comment);
            log.info("comment with id {} soft-deleted by user with id {}", id, userContext.id());
            return;
        }

        repository.deleteById(id);
        log.info("comment with id {} hard-deleted by user with id {}", id, userContext.id());
    }
}

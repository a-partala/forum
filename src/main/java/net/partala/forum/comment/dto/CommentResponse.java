package net.partala.forum.comment.dto;

import net.partala.forum.comment.CommentEntity;
import net.partala.forum.user.UserResponse;

public record CommentResponse(
        Long id,
        String content,
        UserResponse creatorId,
        Long threadId,
        Long parentId,
        boolean hasReplies,
        boolean deleted,
        boolean edited
) {

    public static CommentResponse of(CommentEntity entity, boolean hasReplies) {
        return new CommentResponse(
                entity.getId(),
                entity.getContent(),
                UserResponse.of(entity.getCreator()),
                entity.getThreadId(),
                entity.getParentId(),
                hasReplies,
                entity.isDeleted(),
                entity.isEdited());
    }

    public static CommentResponse of(CommentEntity entity) {
        return of(entity, false);
    }
}

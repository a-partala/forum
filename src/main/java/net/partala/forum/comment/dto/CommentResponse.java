package net.partala.forum.comment.dto;

import net.partala.forum.comment.CommentEntity;
import net.partala.forum.user.UserResponse;

public record CommentResponse(
        Long id,
        String content,
        UserResponse creatorId,
        Long threadId,
        Long parentId,
        boolean hasReplies
) {

    public static CommentResponse of(CommentEntity entity, boolean hasReplies) {
        return new CommentResponse(
                entity.getId(),
                entity.getContent(),
                UserResponse.of(entity.getCreator()),
                entity.getThreadId(),
                entity.getParentId(),
                hasReplies);
    }

    public static CommentResponse of(CommentEntity entity) {
        return of(entity, false);
    }
}

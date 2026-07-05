package net.partala.forum.comment.dto;

import net.partala.forum.comment.CommentEntity;

public record CommentResponse(
        Long id,
        String content,
        Long creatorId,
        Long threadId,
        Long parentId,
        boolean hasReplies
) {

    public static CommentResponse of(CommentEntity entity, boolean hasReplies) {
        return new CommentResponse(
                entity.getId(),
                entity.getContent(),
                entity.getCreator().getId(),
                entity.getThreadId(),
                entity.getParentId(),
                hasReplies);
    }

    public static CommentResponse of(CommentEntity entity) {
        return of(entity, false);
    }
}

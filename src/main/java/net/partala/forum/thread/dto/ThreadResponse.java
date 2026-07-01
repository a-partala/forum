package net.partala.forum.thread.dto;

import net.partala.forum.thread.ThreadEntity;

import java.time.LocalDateTime;

public record ThreadResponse(
        Long id,
        String title,
        String content,
        Long realmId,
        LocalDateTime createdAt
) {
    public static ThreadResponse of(ThreadEntity entity) {
        return new ThreadResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getRealm().getId(),
                entity.getCreatedAt()
        );
    }
}

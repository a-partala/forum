package net.partala.forum.realm.dto;

import net.partala.forum.realm.RealmEntity;

import java.time.LocalDateTime;

public record RealmResponse (
        Long id,
        Long parentId,
        String name,
        String description,
        Long ownerId,
        LocalDateTime createdAt
) {
    public static RealmResponse of(RealmEntity entity) {
        return new RealmResponse(
                entity.getId(),
                entity.getParentId(),
                entity.getName(),
                entity.getDescription(),
                entity.getOwner().getId(),
                entity.getCreatedAt()
        );
    }
}

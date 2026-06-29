package net.partala.forum.realms;

public record RealmResponse (
        Long id,
        Long parentId,
        String name,
        Long ownerId
) {
    public static RealmResponse of(RealmEntity entity) {
        return new RealmResponse(
                entity.getId(),
                entity.getParentId(),
                entity.getName(),
                entity.getOwner().getId()
        );
    }
}

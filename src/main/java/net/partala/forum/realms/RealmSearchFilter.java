package net.partala.forum.realms;

public record RealmSearchFilter(
        Long parentRealmId,
        Long ownerId,
        Integer pageSize,
        Integer pageId
) {
}

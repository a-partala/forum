package net.partala.forum.realm.dto;

public record RealmSearchFilter(
        Long parentRealmId,
        Long ownerId,
        Integer pageSize,
        Integer pageId
) {
}

package net.partala.forum.realm;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public record BranchData(
        Long origin,
        Integer size,
        Set<Long> ancestorOwners
) {

    public boolean isRoot() {
        return size() == 0;
    }

    public static BranchData of(Long originId,
                         Function<Long, Optional<RealmEntity>> realmFinderById) {

        var owners = new HashSet<Long>();
        var seenRealms = new HashSet<Long>();

        Long currentId = originId;
        while(currentId != null) {
            var realm = realmFinderById.apply(currentId);
            if(realm.isEmpty()) {
                throw new IllegalStateException("There is no realm with id " + currentId);
            }

            if(!seenRealms.add(realm.get().getId())) {
                throw new IllegalStateException("Cyclic branch detected");
            }
            owners.add(realm.get().getOwner().getId());

            currentId = realm.get().getParentId();
        }

        return new BranchData(originId, seenRealms.size(), owners);
    }
}

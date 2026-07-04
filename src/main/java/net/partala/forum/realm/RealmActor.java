package net.partala.forum.realm;
import net.partala.forum.common.AbilityResponse;
import net.partala.forum.user.UserContext;
import net.partala.forum.user.UserRole;

final class RealmActor {

    private final Long id;
    private final UserRole role;
    private final int maxRealmDepth;

    RealmActor(UserContext userContext,
               int maxRealmDepth) {
        id = userContext.id();
        this.role = userContext.role();
        this.maxRealmDepth = maxRealmDepth;
    }

    AbilityResponse canCreate(BranchData branch) {

        if(isAdmin()) {
            return AbilityResponse.can();
        }

        if(branch.isRoot()) {
            return AbilityResponse.cannot("Cannot create realm in the root scope");
        }

        if(!branch.ancestorOwners().contains(id)) {
            return AbilityResponse.cannot("Don't have access to this scope");
        }

        if(branch.size() >= maxRealmDepth) {
            return AbilityResponse.cannot("Realms' chain is too long");
        }

        return AbilityResponse.can();
    }

    boolean isAdmin() {
        return role.equals(UserRole.ADMIN);
    }
}

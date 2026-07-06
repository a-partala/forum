package net.partala.forum.realm;
import net.partala.forum.common.AbilityResponse;
import net.partala.forum.user.Actor;
import net.partala.forum.user.UserContext;

final class RealmActor extends Actor {

    private final int maxRealmDepth;

    RealmActor(UserContext userContext,
               int maxRealmDepth) {
        super(userContext);
        this.maxRealmDepth = maxRealmDepth;
    }

    AbilityResponse canCreateInBranch(BranchData branch) {

        if(!isActive()) {
            return INACTIVE_ACCOUNT_RESPONSE;
        }

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

    AbilityResponse canEdit(BranchData branch) {

        if(!isActive()) {
            return INACTIVE_ACCOUNT_RESPONSE;
        }

        if(isAdmin()) {
            return AbilityResponse.can();
        }

        if(!branch.ancestorOwners().contains(id)) {
            return AbilityResponse.cannot("Don't have access to this scope");
        }

        return AbilityResponse.can();
    }
}

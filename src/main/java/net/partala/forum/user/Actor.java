package net.partala.forum.user;

import net.partala.forum.common.AbilityResponse;

public abstract class Actor {

    protected final AbilityResponse INACTIVE_ACCOUNT_RESPONSE = AbilityResponse.cannot("Illegal account status for this operation");
    protected final Long id;
    protected final UserRole role;
    protected final AccountStatus status;

    public Actor(UserContext userContext) {
        id = userContext.id();
        role = userContext.role();
        status = userContext.status();
    }

    protected boolean isAdmin() {
        return role.equals(UserRole.ADMIN);
    }

    protected boolean isActive() {
        return status.equals(AccountStatus.ACTIVE);
    }
}

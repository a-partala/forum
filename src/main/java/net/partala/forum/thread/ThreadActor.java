package net.partala.forum.thread;

import net.partala.forum.common.AbilityResponse;
import net.partala.forum.user.Actor;
import net.partala.forum.user.UserContext;

public final class ThreadActor extends Actor {

    public ThreadActor(UserContext userContext) {
        super(userContext);
    }

    AbilityResponse canCreate() {
        if(!isActive()) {
            return INACTIVE_ACCOUNT_RESPONSE;
        }

        return AbilityResponse.can();
    }

    AbilityResponse canEdit(ThreadEntity entity) {
        if(!isActive()) {
            return INACTIVE_ACCOUNT_RESPONSE;
        }

        if(isAdmin()) {
            return AbilityResponse.can();
        }

        if(!entity.getCreator().getId().equals(id)) {
            return AbilityResponse.cannot("You can only edit your threads");
        }

        return AbilityResponse.can();
    }
}

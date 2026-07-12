package net.partala.forum.vote;

import net.partala.forum.common.AbilityResponse;
import net.partala.forum.user.Actor;
import net.partala.forum.user.UserContext;

class VoteActor extends Actor {

    public VoteActor(UserContext userContext) {
        super(userContext);
    }

    AbilityResponse canVote(Long entityCreatorId) {
        if(!isActive()) {
            return INACTIVE_ACCOUNT_RESPONSE;
        }

        if(id.equals(entityCreatorId)) {
            return AbilityResponse.cannot("You cannot do this");
        }

        return AbilityResponse.can();
    }
}

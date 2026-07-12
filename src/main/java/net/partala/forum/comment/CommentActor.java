package net.partala.forum.comment;

import net.partala.forum.common.AbilityResponse;
import net.partala.forum.realm.BranchDetails;
import net.partala.forum.thread.ThreadEntity;
import net.partala.forum.user.Actor;
import net.partala.forum.user.UserContext;

final class CommentActor extends Actor {

    public CommentActor(UserContext userContext) {
        super(userContext);
    }

    AbilityResponse canCreate() {
        if(!isActive()) {
            return INACTIVE_ACCOUNT_RESPONSE;
        }

        return AbilityResponse.can();
    }

    AbilityResponse canEdit(CommentEntity entity) {
        if(!isActive()) {
            return INACTIVE_ACCOUNT_RESPONSE;
        }

        if(isAdmin()) {
            return AbilityResponse.can();
        }

        if(!entity.getCreator().getId().equals(id)) {
            return AbilityResponse.cannot("You can only edit your comments");
        }

        return AbilityResponse.can();
    }

    AbilityResponse canDelete(CommentEntity entity, BranchDetails realmBranch) {
        if(!isActive()) {
            return INACTIVE_ACCOUNT_RESPONSE;
        }

        if(isAdmin()) {
            return AbilityResponse.can();
        }

        if(realmBranch.containsOwner(id)) {
            return AbilityResponse.can();
        }

        if(!entity.getCreator().getId().equals(id)) {
            return AbilityResponse.cannot("You can only edit your comments");
        }

        return AbilityResponse.can();
    }
}

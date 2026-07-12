package net.partala.forum.vote;

import net.partala.forum.common.RatableEntity;
import net.partala.forum.user.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class VoteService {

    private final Map<Class<? extends RatableEntity>, VoteRepository> repositories;

    public VoteService(Set<VoteRepository> voteRepositories) {
        repositories = new HashMap<>(voteRepositories.size());
        for(var repository : voteRepositories) {
            repositories.put(repository.getEntityClass(), repository);
        }
    }

    public VoteRepository getRepository(Class<?> entityClass) {
        return repositories.get(entityClass);
    }

    @Transactional
    public void vote(UserContext userContext, Long entityId, VoteType voteType, Class<? extends RatableEntity> entityClass) {

        var actor = new VoteActor(userContext);

        actor.canVote(getCreatorId(entityId, entityClass))
                .throwIfCannot();

        var repository = getRepository(entityClass);
        var vote = repository.getVote(userContext.id(), entityId);

        if(vote.isPresent()) {

            if(!vote.get().equals(voteType)) {
                repository.updateVote(userContext.id(), entityId, voteType);
            }
            return;
        }

        repository.addVote(userContext.id(), entityId, voteType);
    }

    @Transactional
    public void cancelVote(UserContext userContext, Long entityId, Class<? extends RatableEntity> entityClass) {

        var actor = new VoteActor(userContext);

        actor.canVote(getCreatorId(entityId, entityClass))
                .throwIfCannot();

        var repository = getRepository(entityClass);
        var vote = repository.getVote(userContext.id(), entityId);

        if(vote.isEmpty()) {
            return;
        }

        repository.deleteVote(userContext.id(), entityId);
    }

    public long getRating(Long entityId, Class<? extends RatableEntity> entityClass) {

        return getRepository(entityClass).getRating(entityId);
    }

    private Long getCreatorId(Long entityId, Class<? extends RatableEntity> entityClass) {
        return getRepository(entityClass).getCreatorId(entityId);
    }
}

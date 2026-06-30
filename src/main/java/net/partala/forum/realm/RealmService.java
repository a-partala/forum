package net.partala.forum.realm;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import net.partala.forum.dto.AbilityResponse;
import net.partala.forum.exception.AlreadyExistsException;
import net.partala.forum.realm.dto.CreateRealmRequest;
import net.partala.forum.realm.dto.RealmResponse;
import net.partala.forum.realm.dto.RealmSearchFilter;
import net.partala.forum.user.UserContext;
import net.partala.forum.config.RealmProperties;
import net.partala.forum.user.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RealmService {

    private final RealmProperties properties;
    private final UserService userService;
    private final RealmRepository realmRepository;

    RealmService(RealmProperties properties, UserService userService, RealmRepository realmRepository) {
        this.properties = properties;
        this.userService = userService;
        this.realmRepository = realmRepository;
    }

    List<RealmResponse> searchByFilter(RealmSearchFilter filter) {
        var pageable = Pageable
                .ofSize(filter.pageSize() != null ? filter.pageSize() : 10)
                .withPage(filter.pageId() != null ? filter.pageId() : 0);

        return realmRepository.searchByFilter(
                filter.parentRealmId(),
                filter.ownerId(),
                pageable
        ).stream().map(RealmResponse::of).toList();
    }

    RealmResponse createRealm(CreateRealmRequest request,
                              UserContext userContext) {

        var owner = userService
                .findById(request.ownerId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "No user with id " + request.ownerId())
                );

        if(realmRepository.findByName(request.name()).isPresent()) {
            throw new AlreadyExistsException("Realm with this name already exists");
        }

        AbilityResponse canCreate = canCreateRealmInRealm(request.parentId(), userContext);
        if(!canCreate.result) {
            throw new IllegalStateException(canCreate.reason);
        }

        var realm = new RealmEntity(
                request.name(),
                request.description(),
                owner,
                request.parentId()
        );

        var savedRealm = realmRepository.save(realm);

        return RealmResponse.of(savedRealm);
    }

    AbilityResponse canCreateRealmInRealm(Long parentRealmId,
                                          UserContext userContext) {

        var actor = new RealmActor(
                userContext,
                properties.maxDepth());
        return actor.canCreate(BranchData.of(parentRealmId, realmRepository::findById));
    }
}

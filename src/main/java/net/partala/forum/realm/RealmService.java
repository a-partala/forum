package net.partala.forum.realm;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.partala.forum.common.AbilityResponse;
import net.partala.forum.exception.AlreadyExistsException;
import net.partala.forum.realm.dto.CreateRealmRequest;
import net.partala.forum.realm.dto.UpdateRealmRequest;
import net.partala.forum.realm.dto.RealmResponse;
import net.partala.forum.user.UserContext;
import net.partala.forum.config.RealmProperties;
import net.partala.forum.user.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class RealmService {

    private final RealmProperties properties;
    private final UserService userService;
    private final RealmRepository repository;

    RealmService(RealmProperties properties, UserService userService, RealmRepository repository) {
        this.properties = properties;
        this.userService = userService;
        this.repository = repository;
    }

    public RealmResponse getRealmById(Long id) {
        return RealmResponse.of(getEntityById(id));
    }

    public RealmEntity getReferenceById(Long id) {
        return repository.getReferenceById(id);
    }

    private RealmEntity getEntityById(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "No realm with id " + id)
        );
    }

    List<RealmResponse> searchByFilter(Long parentRealmId, Pageable pageable) {

        return repository.searchByFilter(
                parentRealmId,
                pageable
        ).stream().map(RealmResponse::of).toList();
    }

    @Transactional
    RealmResponse createRealm(CreateRealmRequest request,
                              UserContext userContext) {

        var owner = userService.getReferenceById(request.ownerId());

        if(repository.findByName(request.name()).isPresent()) {
            throw new AlreadyExistsException("Realm with this name already exists");
        }

        canCreateRealmInParent(request.parentId(), userContext)
                .throwIfCannot();

        var realm = new RealmEntity(
                request.name(),
                request.description(),
                owner,
                request.parentId()
        );

        var savedRealm = repository.save(realm);

        var response = RealmResponse.of(savedRealm);
        log.info("realm created: {}", response);
        return response;
    }

    @Transactional
    RealmResponse updateRealm(Long id, UpdateRealmRequest request, UserContext userContext) {

        var realm = getEntityById(id);

        if(realm.getName().equals(request.name()) &&
                realm.getDescription().equals(request.description())) {
            return RealmResponse.of(realm);
        }

        var actor = new RealmActor(userContext);
        var branchData = getBranchDetails(id);

        actor.canEdit(branchData)
                .throwIfCannot();

        realm.setName(request.name());
        realm.setDescription(request.description());
        return RealmResponse.of(repository.save(realm));
    }

    AbilityResponse canCreateRealmInParent(Long parentRealmId,
                                           UserContext userContext) {

        var actor = new RealmActor(userContext);
        var branch = getBranchDetails(parentRealmId);
        return actor.canCreateInBranch(branch, properties.maxDepth());
    }

    public BranchDetails getBranchDetails(Long originId) {
        return BranchDetails.of(originId, repository::findById);
    }
}

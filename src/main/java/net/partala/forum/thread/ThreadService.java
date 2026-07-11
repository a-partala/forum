package net.partala.forum.thread;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.partala.forum.realm.BranchDetails;
import net.partala.forum.realm.RealmService;
import net.partala.forum.thread.dto.CreateThreadRequest;
import net.partala.forum.thread.dto.ThreadResponse;
import net.partala.forum.thread.dto.UpdateThreadRequest;
import net.partala.forum.user.UserContext;
import net.partala.forum.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ThreadService {

    private final ThreadRepository repository;
    private final UserService userService;
    private final RealmService realmService;

    public ThreadService(ThreadRepository repository,
                         UserService userService,
                         RealmService realmService) {
        this.repository = repository;
        this.userService = userService;
        this.realmService = realmService;
    }

    public ThreadEntity getReferenceById(Long id) {
        return repository.getReferenceById(id);
    }

    private ThreadEntity getEntityById(Long id) {
        return repository.findByIdAndStatusNot(id, ThreadStatus.DELETED).orElseThrow(() -> new EntityNotFoundException(
                "No thread with id " + id));
    }

    public ThreadResponse getThreadById(Long id) {
        return ThreadResponse.of(getEntityById(id));
    }

    @Transactional
    public ThreadResponse createThread(CreateThreadRequest request,
                                       UserContext userContext) {

        var actor = new ThreadActor(userContext);
        actor.canCreate()
                .throwIfCannot();

        var creator = userService.getReferenceById(userContext.id());
        var realm = realmService.getReferenceById(request.realmId());
        var thread = new ThreadEntity(
                request.title(),
                request.content(),
                creator,
                realm,
                ThreadStatus.ACTIVE);
        var savedThread = repository.save(thread);
        return ThreadResponse.of(savedThread);
    }

    @Transactional
    public ThreadResponse updateThread(Long id,
                                       UpdateThreadRequest request,
                                       UserContext userContext) {

        var thread = getEntityById(id);

        if(request.isSameData(thread)) {
            return ThreadResponse.of(thread);
        }

        var actor = new ThreadActor(userContext);

        actor.canEdit(thread)
                .throwIfCannot();

        thread.setTitle(request.title());
        thread.setContent(request.content());
        return ThreadResponse.of(repository.save(thread));
    }

    @Transactional
    public void deleteThread(Long id,
                                       UserContext userContext) {

        var thread = getEntityById(id);
        var branch = getBranchDetails(thread);
        var actor = new ThreadActor(userContext);

        actor.canDelete(thread, branch)
                .throwIfCannot();

        thread.setStatus(ThreadStatus.DELETED);
        ThreadResponse.of(repository.save(thread));
    }

    @Transactional
    public ThreadResponse closeThread(Long id,
                                       UserContext userContext) {

        var thread = getEntityById(id);

        var branch = getBranchDetails(thread);
        var actor = new ThreadActor(userContext);

        actor.canClose(thread, branch)
                .throwIfCannot();

        thread.setStatus(ThreadStatus.CLOSED);
        return ThreadResponse.of(repository.save(thread));
    }

    public BranchDetails getBranchDetails(Long threadId) {
        return getBranchDetails(getEntityById(threadId));
    }

    private BranchDetails getBranchDetails(ThreadEntity thread) {
        return realmService.getBranchDetails(thread.getRealm().getId());
    }
}

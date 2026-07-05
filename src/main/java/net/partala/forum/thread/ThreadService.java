package net.partala.forum.thread;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.partala.forum.realm.RealmService;
import net.partala.forum.thread.dto.CreateThreadRequest;
import net.partala.forum.thread.dto.ThreadResponse;
import net.partala.forum.user.UserContext;
import net.partala.forum.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    public ThreadResponse getReferenceById(Long id) {
        var entity = repository.getReferenceById(id);
        return ThreadResponse.of(entity);
    }

    public ThreadResponse getThreadById(Long id) {
        var entity = repository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "No thread with id " + id));
        return ThreadResponse.of(entity);
    }

    @Transactional
    public ThreadResponse createThread(CreateThreadRequest request,
                                       UserContext userContext) {

        var creator = userService.getReferenceById(userContext.id());
        var realm = realmService.getReferenceById(request.realmId());
        var thread = new ThreadEntity(request.title(), request.content(), creator, realm);
        var savedThread = repository.save(thread);
        return ThreadResponse.of(savedThread);
    }
}

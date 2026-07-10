package net.partala.forum.thread;

import net.partala.forum.user.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThreadRepository extends JpaRepository<ThreadEntity, Long> {

    Optional<ThreadEntity> findByIdAndStatusNot(Long id, AccountStatus status);
}

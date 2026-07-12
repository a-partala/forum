package net.partala.forum.thread;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThreadRepository extends JpaRepository<ThreadEntity, Long> {

    Optional<ThreadEntity> findByIdAndStatusNot(Long id, ThreadStatus status);

    @Query("""
            SELECT t.creator.id FROM ThreadEntity t
            WHERE t.id = :threadId
            """)
    Long getCreatorId(Long threadId);
}

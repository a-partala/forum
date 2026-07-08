package net.partala.forum.comment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    @Query("""
            SELECT c FROM CommentEntity c
            JOIN FETCH c.creator
            WHERE c.threadId = :threadId
            AND c.parentId IS NULL
            """)
    List<CommentEntity> getThreadComments(Long threadId, Pageable pageable);

    @Query("""
            SELECT c FROM CommentEntity c
            JOIN FETCH c.creator
            WHERE c.parentId = :commentId
            """)
    List<CommentEntity> getCommentReplies(Long commentId, Pageable pageable);

    @Query("""
            SELECT c.parentId FROM CommentEntity c
            WHERE c.parentId IN :commentIds
            """)
    Set<Long> selectReplied(List<Long> commentIds);
}

package net.partala.forum.vote;

import net.partala.forum.comment.CommentEntity;
import net.partala.forum.comment.CommentRepository;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CommentVoteRepository extends VoteRepository {

    public CommentVoteRepository(NamedParameterJdbcTemplate jdbc, CommentRepository commentRepository) {
        super(jdbc, "comment_votes", "comment_id", CommentEntity.class, commentRepository::getCreatorId);
    }
}

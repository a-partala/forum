package net.partala.forum.vote;

import net.partala.forum.thread.ThreadEntity;
import net.partala.forum.thread.ThreadRepository;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ThreadVoteRepository extends VoteRepository {

    public ThreadVoteRepository(NamedParameterJdbcTemplate jdbc, ThreadRepository threadRepository) {
        super(jdbc, "thread_votes", "thread_id", ThreadEntity.class, threadRepository::getCreatorId);
    }
}

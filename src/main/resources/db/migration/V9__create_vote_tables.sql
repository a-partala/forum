CREATE TABLE thread_votes (
    user_id BIGINT,
    thread_id BIGINT,
    val SMALLINT NOT NULL,

    CONSTRAINT pk_thread_votes PRIMARY KEY (user_id, thread_id),
    CONSTRAINT fk_thread_votes_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_thread_votes_thread_id FOREIGN KEY (thread_id) REFERENCES threads(id),
    CONSTRAINT chk_thread_votes_val CHECK (val IN (-1, 1))
);
CREATE TABLE comment_votes (
    user_id BIGINT,
    comment_id BIGINT,
    val SMALLINT NOT NULL,

    CONSTRAINT pk_comment_votes PRIMARY KEY (user_id, comment_id),
    CONSTRAINT fk_comment_votes_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_comment_votes_comment_id FOREIGN KEY (comment_id) REFERENCES comments(id),
    CONSTRAINT chk_comment_votes_val CHECK (val IN (-1, 1))
);
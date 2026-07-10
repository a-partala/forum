CREATE TABLE comments (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    content VARCHAR(1000) NOT NULL,
    creator_id BIGINT NOT NULL,
    thread_id BIGINT NOT NULL,
    parent_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_comments_creator_id FOREIGN KEY (creator_id) REFERENCES users(id),
    CONSTRAINT fk_comments_thread_id FOREIGN KEY (thread_id) REFERENCES threads(id),
    CONSTRAINT fk_comments_parent_id FOREIGN KEY (parent_id) REFERENCES comments(id)
)
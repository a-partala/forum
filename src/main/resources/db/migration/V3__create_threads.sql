CREATE TABLE threads (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    content VARCHAR(4000) NOT NULL,
    creator_id BIGINT NOT NULL,
    realm_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_threads_creator_id FOREIGN KEY (creator_id) REFERENCES users(id),
    CONSTRAINT fk_threads_realm_id FOREIGN KEY (realm_id) REFERENCES realms(id)
)
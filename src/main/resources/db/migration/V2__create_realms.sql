CREATE TABLE realms (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(1000),
    owner_id BIGINT NOT NULL,
    parent_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_realms_owner_id FOREIGN KEY (owner_id) REFERENCES users(id),
    CONSTRAINT fk_realms_parent_id FOREIGN KEY (parent_id) REFERENCES realms(id)
)
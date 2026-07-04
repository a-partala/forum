CREATE INDEX idx_realms_parent_id ON realms(parent_id);

CREATE INDEX idx_threads_creator_id ON threads(creator_id);
CREATE INDEX idx_threads_realm_id ON threads(realm_id);

CREATE INDEX idx_comments_creator_id ON comments(creator_id);
CREATE INDEX idx_comments_thread_id ON comments(thread_id);
CREATE INDEX idx_comments_parent_id ON comments(parent_id);
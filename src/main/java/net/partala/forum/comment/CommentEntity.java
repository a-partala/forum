package net.partala.forum.comment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.partala.forum.common.RatableEntity;
import net.partala.forum.user.UserEntity;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "comments")
public class CommentEntity extends RatableEntity {

    @Setter
    private String content;

    @Column(name = "thread_id")
    private Long threadId;

    @Column(name = "parent_id")
    private Long parentId;

    @Setter
    private boolean deleted;

    @Setter
    private boolean edited;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public CommentEntity() {}

    public CommentEntity(String content, UserEntity creator, Long threadId, Long parentId) {
        this.creator = creator;
        this.content = content;
        this.threadId = threadId;
        this.parentId = parentId;
    }
}

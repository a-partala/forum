package net.partala.forum.comment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.partala.forum.user.UserEntity;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "comments")
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private UserEntity creator;

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
        this.content = content;
        this.creator = creator;
        this.threadId = threadId;
        this.parentId = parentId;
    }
}

package net.partala.forum.thread;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.partala.forum.common.RatableEntity;
import net.partala.forum.realm.RealmEntity;
import net.partala.forum.user.UserEntity;
import org.hibernate.annotations.Generated;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "threads")
public class ThreadEntity extends RatableEntity {

    @Setter
    private String title;
    @Setter
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "realm_id")
    private RealmEntity realm;

    //TODO: Add status_actor_id or status_changed_by to the table
    @Setter
    @Enumerated(EnumType.STRING)
    private ThreadStatus status;

    @Generated
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public ThreadEntity() {}

    public ThreadEntity(String title, String content, UserEntity creator, RealmEntity realm, ThreadStatus status) {
        this.creator = creator;
        this.title = title;
        this.content = content;
        this.realm = realm;
        this.status = status;
    }
}

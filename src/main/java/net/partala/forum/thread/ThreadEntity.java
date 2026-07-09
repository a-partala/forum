package net.partala.forum.thread;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.partala.forum.realm.RealmEntity;
import net.partala.forum.user.UserEntity;
import org.hibernate.annotations.Generated;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "threads")
public class ThreadEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Setter
    private String title;
    @Setter
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private UserEntity creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "realm_id")
    private RealmEntity realm;

    @Generated
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public ThreadEntity() {}
    public ThreadEntity(String title, String content, UserEntity creator, RealmEntity realm) {
        this.title = title;
        this.content = content;
        this.creator = creator;
        this.realm = realm;
    }
}

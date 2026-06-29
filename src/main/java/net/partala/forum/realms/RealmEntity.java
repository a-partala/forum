package net.partala.forum.realms;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.partala.forum.user.UserEntity;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Entity
@Table(name = "realms")
public class RealmEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public RealmEntity() {}

    public RealmEntity(String name, String description, UserEntity owner, Long parentId) {
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.parentId = parentId;
    }
}

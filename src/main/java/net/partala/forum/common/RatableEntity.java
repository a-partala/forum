package net.partala.forum.common;

import jakarta.persistence.*;
import lombok.Getter;
import net.partala.forum.user.UserEntity;

@MappedSuperclass
public abstract class RatableEntity {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    protected UserEntity creator;
}

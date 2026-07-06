package net.partala.forum.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Setter
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Setter
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public UserEntity() {}
    public UserEntity(String username,
                      String email,
                      String encodedPassword,
                      UserRole role,
                      AccountStatus status) {

        this.username = username;
        this.email = email;
        password = encodedPassword;
        this.role = role;
        this.status = status;
    }
}
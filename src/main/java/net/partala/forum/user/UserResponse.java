package net.partala.forum.user;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String email,
        UserRole role,
        AccountStatus status,
        LocalDateTime createdAt
) {
    public static UserResponse of(UserEntity entity) {
        return new UserResponse(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getRole(),
                entity.getStatus(),
                entity.getCreatedAt());
    }
}

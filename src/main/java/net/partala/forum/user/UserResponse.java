package net.partala.forum.user;

public record UserResponse(
        Long id,
        String username,
        String email,
        UserRole role
) {
    public static UserResponse of(UserEntity entity) {
        return new UserResponse(entity.getId(), entity.getUsername(), entity.getEmail(), entity.getRole());
    }
}

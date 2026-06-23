package net.partala.forum.auth;

import net.partala.forum.user.UserEntity;
import net.partala.forum.user.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Set;

public class SecurityUser implements UserDetails {

    private final UserEntity entity;

    public SecurityUser(UserEntity entity) {
        this.entity = entity;
    }

    public Long getId() {
        return entity.getId();
    }

    @Override
    public String getPassword() {
        return entity.getPassword();
    }

    @Override
    public String getUsername() {
        return entity.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(entity.getRole());
    }

    public UserRole getRole() {
        return entity.getRole();
    }
}

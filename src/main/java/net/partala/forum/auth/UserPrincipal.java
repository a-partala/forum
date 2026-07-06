package net.partala.forum.auth;

import lombok.Getter;
import net.partala.forum.user.AccountStatus;
import net.partala.forum.user.UserContext;
import net.partala.forum.user.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Set;

public class UserPrincipal implements UserDetails {

    @Getter
    private final UserContext context;
    private final String username;
    private final String password;

    public UserPrincipal(UserEntity entity) {
        context = new UserContext(
                entity.getId(),
                entity.getRole(),
                entity.getStatus());
        this.username = entity.getUsername();
        this.password = entity.getPassword();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return Set.of(context.role()); }

    @Override
    public boolean isEnabled() {
        return !context.status().equals(AccountStatus.DELETED);
    }
}

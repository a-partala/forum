package net.partala.forum.auth;

import net.partala.forum.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserPrincipalService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserPrincipalService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        if(login.isBlank()) {
            throw new UsernameNotFoundException("Blank login");
        }
        var user = userRepository.findByUsernameOrEmail(login, login).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new UserPrincipal(user);
    }
}

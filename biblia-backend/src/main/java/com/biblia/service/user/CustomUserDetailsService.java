package com.biblia.service.user;

import com.biblia.entity.Role;
import com.biblia.entity.User;
import com.biblia.repository.user.UserRepository;
import com.biblia.security.UserPrincipal;
import com.biblia.utils.Constants;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByLoginIdAndStatusAndDeleteFlag
                (loginId, Constants.ACCOUNT_STATUS.ACTIVE, Constants.DELETE_FLAG.NOT_DELETED);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return new UserPrincipal(user);
        }else{
            throw new UsernameNotFoundException("Invalid username or password.");
        }
    }

    private Collection < ? extends GrantedAuthority> mapRolesToAuthorities(Collection <Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleCode()))
                .collect(Collectors.toList());
    }
}


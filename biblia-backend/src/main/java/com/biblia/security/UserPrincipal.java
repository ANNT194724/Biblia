package com.biblia.security;

import com.biblia.entity.Role;
import com.biblia.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@NoArgsConstructor
@Data
public class UserPrincipal implements UserDetails {
    private Integer userId;
    private String username;
    private String loginId;
    @JsonIgnore
    private String password;
    private String roleCode;
    private Integer status;
    private String avatarUrl;
    private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.loginId = user.getLoginId();
        this.userId = user.getUserId();
        this.roleCode = user.getRoleCode();
        this.status = user.getStatus();
        this.avatarUrl = user.getAvatarUrl();
        this.status = user.getStatus();
        this.avatarUrl = user.getAvatarUrl();
        this.authorities = translate(user.getRoles());
    }

    private Collection<? extends GrantedAuthority> translate(Collection<Role> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            String code = role.getRoleCode().toUpperCase();
            authorities.add(new SimpleGrantedAuthority(code));
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrincipal that = (UserPrincipal) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

}


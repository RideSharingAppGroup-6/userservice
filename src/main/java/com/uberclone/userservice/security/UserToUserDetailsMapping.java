package com.uberclone.userservice.security;

import com.uberclone.userservice.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserToUserDetailsMapping implements UserDetails {
//    private String name;
//    private String password;
//
//    List<GrantedAuthority> authorities;
    private User user;

    public UserToUserDetailsMapping(User user){
        this.user = user;
//        this.name = user.getEmail();
//        this.password = user.getPassword();
//        this.authorities = Arrays.stream(user.getUserRole().toString().split(","))
//                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(user.getUserRole().toString().split(","))
                        .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
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
}

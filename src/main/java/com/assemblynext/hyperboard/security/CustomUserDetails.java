package com.assemblynext.hyperboard.security;

import java.util.ArrayList;
import java.util.Collection;

import com.assemblynext.hyperboard.entities.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {
    private final User user;
        public CustomUserDetails(User user) {
        this.user = user;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        var authorities = new ArrayList<SimpleGrantedAuthority>();
        if (user.getAdmin()){
            authorities.add(new SimpleGrantedAuthority("ADMIN"));
        }
        if (user.getMod()){
            authorities.add(new SimpleGrantedAuthority("MOD"));
        }
        authorities.add(new SimpleGrantedAuthority("USER"));
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
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
    public final User getUser() {
        return user;
    }
}


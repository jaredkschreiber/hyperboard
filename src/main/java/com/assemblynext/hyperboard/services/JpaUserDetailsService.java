package com.assemblynext.hyperboard.services;

import java.util.function.Supplier;

import com.assemblynext.hyperboard.entities.User;
import com.assemblynext.hyperboard.repositories.UserRepository;
import com.assemblynext.hyperboard.security.CustomUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String username) {
        Supplier<UsernameNotFoundException> s =() -> new UsernameNotFoundException("Problem during authentication!");

        User u = userRepository
        .findUserByUsername(username)
        .orElseThrow(s);
        
        return new CustomUserDetails(u);
    }
}
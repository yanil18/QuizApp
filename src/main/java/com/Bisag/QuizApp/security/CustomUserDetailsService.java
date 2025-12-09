package com.Bisag.QuizApp.security;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.Bisag.QuizApp.dto.Csuser;
import com.Bisag.QuizApp.repository.CsuserRepo;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private CsuserRepo csuserRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Csuser> user = csuserRepo.findFirstByEmail(email);
        if (!user.isPresent()) {
        throw new UsernameNotFoundException("User not found: " + email);
    }
        return new org.springframework.security.core.userdetails.User(
                user.get().getEmail(), 
                user.get().getPassword(), 
                getAuthorities(user.get()) 
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Csuser user) {
        String role = user.getRole();
        if (role == null || role.trim().isEmpty()) {
            role = "ROLE_USER"; // Default role
        }
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }
        return Collections.singleton(new SimpleGrantedAuthority(role));
    }
}






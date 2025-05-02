package com.codigo.code.test.service;

import com.codigo.code.test.repo.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetails implements UserDetailsService {

    private final UserRepository userRepo;

    public MyUserDetails(UserRepository userRepo) {
        this.userRepo = userRepo;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.getReferenceByUsername(username)
                .map(user -> User.withUsername(username)
                        .password(user.getPassword())
                        .authorities(user.getAuthorities())
                        .disabled(!user.isEnabled())
                        .accountExpired(!user.isAccountNonExpired())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

    }
}

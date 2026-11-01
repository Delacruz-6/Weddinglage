package com.weddinglage.security;

import com.weddinglage.repository.AdminUserRepository;
import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminUserDetailsService implements UserDetailsService {

    private final AdminUserRepository adminUserRepository;

    public AdminUserDetailsService(AdminUserRepository adminUserRepository) {
        this.adminUserRepository = adminUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var admin = adminUserRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));
        if (admin.getLockedUntil() != null && admin.getLockedUntil().isAfter(Instant.now())) {
            throw new LockedException("Cuenta temporalmente bloqueada");
        }
        var authorities = Arrays.stream(admin.getRoles().split(","))
                .map(String::trim)
                .filter(r -> !r.isEmpty())
                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return User.withUsername(admin.getEmail())
                .password(admin.getPasswordHash())
                .authorities(authorities)
                .build();
    }
}

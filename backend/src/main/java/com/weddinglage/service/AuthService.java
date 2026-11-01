package com.weddinglage.service;

import com.weddinglage.config.WeddinglageProperties;
import com.weddinglage.domain.AdminUser;
import com.weddinglage.repository.AdminUserRepository;
import com.weddinglage.web.dto.LoginRequest;
import com.weddinglage.web.error.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AdminUserRepository adminUserRepository;
    private final AuditService auditService;
    private final WeddinglageProperties properties;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public AuthService(
            AuthenticationManager authenticationManager,
            AdminUserRepository adminUserRepository,
            AuditService auditService,
            WeddinglageProperties properties) {
        this.authenticationManager = authenticationManager;
        this.adminUserRepository = adminUserRepository;
        this.auditService = auditService;
        this.properties = properties;
    }

    @Transactional
    public Map<String, Object> login(LoginRequest request, HttpServletRequest httpRequest) {
        AdminUser admin = adminUserRepository.findByEmailIgnoreCase(request.email()).orElse(null);
        if (admin != null && admin.getLockedUntil() != null && admin.getLockedUntil().isAfter(Instant.now())) {
            throw new ApiException("ACCOUNT_LOCKED", "Cuenta temporalmente bloqueada", HttpStatus.LOCKED);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, httpRequest, null);

            if (admin != null) {
                admin.setFailedLogins(0);
                admin.setLockedUntil(null);
                admin.setUpdatedAt(Instant.now());
                adminUserRepository.save(admin);
            }
            auditService.record(request.email(), "LOGIN", "AdminUser", null, null);
            return Map.of("email", authentication.getName(), "authenticated", true);
        } catch (LockedException ex) {
            throw new ApiException("ACCOUNT_LOCKED", "Cuenta temporalmente bloqueada", HttpStatus.LOCKED);
        } catch (BadCredentialsException ex) {
            registerFailedLogin(admin);
            throw new ApiException("UNAUTHORIZED", "Credenciales incorrectas", HttpStatus.UNAUTHORIZED);
        }
    }

    public void logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        var session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    private void registerFailedLogin(AdminUser admin) {
        if (admin == null) {
            return;
        }
        int failures = admin.getFailedLogins() + 1;
        admin.setFailedLogins(failures);
        if (failures >= properties.getMaxFailedLogins()) {
            admin.setLockedUntil(Instant.now().plus(properties.getLockoutMinutes(), ChronoUnit.MINUTES));
            admin.setFailedLogins(0);
        }
        admin.setUpdatedAt(Instant.now());
        adminUserRepository.save(admin);
    }
}

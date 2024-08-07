package com.yeogi.scms.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final LoginAccountService loginAccountService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(AuthenticationManager authenticationManager, LoginAccountService loginAccountService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.loginAccountService = loginAccountService;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean verifyPassword(String username, String rawPassword) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) loginAccountService.loadUserByUsername(username);

            boolean matches = passwordEncoder.matches(rawPassword, userDetails.getPassword());

            if (matches) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, rawPassword, userDetails.getAuthorities());
                Authentication authentication = authenticationManager.authenticate(authenticationToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return true;
            } else {
                return false;
            }
        } catch (AuthenticationException e) {
            return false;
        }
    }
}

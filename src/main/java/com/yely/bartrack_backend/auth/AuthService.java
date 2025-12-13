package com.yely.bartrack_backend.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.yely.bartrack_backend.domain.ResourceNotFoundException;
import com.yely.bartrack_backend.domain.ValidationException;
import com.yely.bartrack_backend.security.JpaUserDetailsService;
import com.yely.bartrack_backend.security.JwtUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final JpaUserDetailsService userDetailsService;

    public LoginResult login(String username, String password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        UserDetails ud = (UserDetails) auth.getPrincipal();

        String access = jwtUtils.generateAccessToken(ud);
        String refresh = jwtUtils.generateRefreshToken(ud);

        return new LoginResult(ud.getUsername(), access, refresh, ud.getAuthorities());
    }

    public TokenPair refreshAccessToken(String refreshToken) {
        if (refreshToken == null) {
            throw new ValidationException("Refresh token is missing");
        }
        if (!jwtUtils.validateToken(refreshToken)) {
            throw new ValidationException("Refresh token is invalid");
        }

        String username = jwtUtils.getUsernameFromToken(refreshToken);
        UserDetails ud = userDetailsService.loadUserByUsername(username);
        if (ud == null) {
            throw new ResourceNotFoundException("User not found");
        }

        String newAccess = jwtUtils.generateAccessToken(ud);
        String newRefresh = jwtUtils.generateRefreshToken(ud);

        return new TokenPair(newAccess, newRefresh);
    }
}
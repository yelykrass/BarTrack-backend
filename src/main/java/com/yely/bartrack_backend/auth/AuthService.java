package com.yely.bartrack_backend.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.yely.bartrack_backend.domain.ForbiddenOperationException;
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
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new ValidationException("Username and password must be provided");
        }
        UserDetails ud;
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            ud = (UserDetails) auth.getPrincipal();
        } catch (BadCredentialsException e) {
            throw new ValidationException("Invalid username or password");
        } catch (LockedException e) {
            throw new ForbiddenOperationException("User account is locked");
        }

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
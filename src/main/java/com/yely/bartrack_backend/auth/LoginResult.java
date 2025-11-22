package com.yely.bartrack_backend.auth;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public record LoginResult(String username, String accessToken, String refreshToken,
        Collection<? extends GrantedAuthority> authorities) {
}

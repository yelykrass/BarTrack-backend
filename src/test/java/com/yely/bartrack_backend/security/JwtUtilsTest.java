package com.yely.bartrack_backend.security;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import io.jsonwebtoken.Claims;

class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private static final String TEST_SECRET = "dGhpcy1pcy1hLXZlcnktbG9uZy1zZWNyZXQta2V5LWZvci10ZXN0aW5nLWhzNTEyLXdoaWNoLXJlcXVpcmVzLWF0LWxlYXN0LTY0LWJ5dGVzLW9mLWRhdGEtdG8tYmUtc2VjdXJl";
    private static final long ACCESS_EXP_MS = 3600000; // 1 hour
    private static final long REFRESH_EXP_MS = 86400000; // 1 day

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(TEST_SECRET, ACCESS_EXP_MS, REFRESH_EXP_MS);
    }

    @Test
    void testGenerateAccessToken() {
        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails user = new User("testuser", "password", authorities);

        String token = jwtUtils.generateAccessToken(user);

        assertThat(token, notNullValue());
        assertThat(token, not(emptyString()));
        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void testGenerateAccessTokenIncludesRoles() {
        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        UserDetails user = new User("admin", "password", authorities);

        String token = jwtUtils.generateAccessToken(user);
        Claims claims = jwtUtils.getAllClaimsFromToken(token);

        assertThat(claims.get("roles"), equalTo("ROLE_ADMIN"));
    }

    @Test
    void testGenerateRefreshToken() {
        UserDetails user = new User("testuser", "password", Collections.emptyList());

        String token = jwtUtils.generateRefreshToken(user);

        assertThat(token, notNullValue());
        assertThat(token, not(emptyString()));
        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void testValidateToken_Valid() {
        UserDetails user = new User("testuser", "password", Collections.emptyList());
        String token = jwtUtils.generateAccessToken(user);

        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void testValidateToken_Invalid() {
        assertFalse(jwtUtils.validateToken("invalid.token.here"));
    }

    @Test
    void testGetAllClaimsFromToken() {
        UserDetails user = new User("testuser", "password", Collections.emptyList());
        String token = jwtUtils.generateAccessToken(user);

        Claims claims = jwtUtils.getAllClaimsFromToken(token);

        assertThat(claims, notNullValue());
        assertThat(claims.getSubject(), equalTo("testuser"));
    }

    @Test
    void testGetUsernameFromToken() {
        UserDetails user = new User("testuser", "password", Collections.emptyList());
        String token = jwtUtils.generateAccessToken(user);

        String username = jwtUtils.getUsernameFromToken(token);

        assertThat(username, equalTo("testuser"));
    }

    @Test
    void testGetUsernameFromToken_RefreshToken() {
        UserDetails user = new User("refreshuser", "password", Collections.emptyList());
        String token = jwtUtils.generateRefreshToken(user);

        String username = jwtUtils.getUsernameFromToken(token);

        assertThat(username, equalTo("refreshuser"));
    }

    @Test
    void testValidateToken_Expired() throws InterruptedException {

        final long SHORT_EXP_MS = 1;
        JwtUtils shortLivedJwtUtils = new JwtUtils(TEST_SECRET, SHORT_EXP_MS, REFRESH_EXP_MS);

        UserDetails user = new User("expireduser", "password", Collections.emptyList());
        String token = shortLivedJwtUtils.generateAccessToken(user);

        TimeUnit.MILLISECONDS.sleep(100);

        assertFalse(jwtUtils.validateToken(token), "An expired token should be invalid.");
    }

    @Test
    void testValidateToken_WrongSignature() {

        UserDetails user = new User("wrongsign", "password", Collections.emptyList());
        String validToken = jwtUtils.generateAccessToken(user);

        String OTHER_SECRET = "dGhpcy1pcy1hLXZlcnktbG9uZy1zZWNyZXQta2V5LXdpdGgtYS1kaWZmZXJlbnQtc2lnbmF0dXJlLXRvLWJlX3VzZWQ=";
        JwtUtils otherKeyJwtUtils = new JwtUtils(OTHER_SECRET, ACCESS_EXP_MS, REFRESH_EXP_MS);

        assertFalse(otherKeyJwtUtils.validateToken(validToken), "A token with an invalid signature should be invalid.");
    }

    @Test
    void testValidateToken_Malformed() {
        assertFalse(jwtUtils.validateToken("header.payload"), "An invalid JWT format should be invalid");

        assertFalse(jwtUtils.validateToken("just_a_random_string"), "A random string must be invalid");
    }

    @Test
    void testGenerateAccessToken_NoRoles() {
        UserDetails user = new User("noroles", "password", Collections.emptyList());
        String token = jwtUtils.generateAccessToken(user);

        Claims claims = jwtUtils.getAllClaimsFromToken(token);

        assertThat(claims.get("roles"), equalTo(""));
    }

}
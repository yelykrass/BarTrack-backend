package com.yely.bartrack_backend.security;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.io.IOException;
import java.util.Collections;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

public class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private JpaUserDetailsService userDetailsService;

    private JwtAuthenticationFilter filter;
    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        filter = new JwtAuthenticationFilter(jwtUtils, userDetailsService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
        mocks.close();
    }

    @Test
    void doFilterInternal_noCookie_doesNotAuthenticate_and_continuesChain() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        final boolean[] chainCalled = { false };
        FilterChain chain = (req, res) -> chainCalled[0] = true;

        filter.doFilterInternal(request, response, chain);

        assertThat("FilterChain should be invoked", chainCalled[0], is(true));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat("No authentication should be set when no cookie present", auth, is(nullValue()));
    }

    @Test
    void doFilterInternal_withValidToken_setsAuthentication_and_continuesChain() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String token = "valid-token";
        Cookie cookie = new Cookie("accessToken", token);
        request.setCookies(cookie);

        when(jwtUtils.validateToken(token)).thenReturn(true);
        when(jwtUtils.getUsernameFromToken(token)).thenReturn("alice");

        UserDetails userDetails = new User("alice", "password", Collections.emptyList());
        when(userDetailsService.loadUserByUsername("alice")).thenReturn(userDetails);

        final boolean[] chainCalled = { false };
        FilterChain chain = (req, res) -> chainCalled[0] = true;

        filter.doFilterInternal(request, response, chain);

        assertThat("FilterChain should be invoked", chainCalled[0], is(true));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat("Authentication should be set", auth, is(notNullValue()));
        assertThat("Principal should be the loaded UserDetails", auth.getPrincipal(), is(userDetails));
        // credentials should be null as set in the filter
        assertThat("Credentials should be null", auth.getCredentials(), is(nullValue()));
    }

    @Test
    void doFilterInternal_invalidToken_skipsAuthentication_and_continuesChain() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String token = "invalid-signature-token";
        request.setCookies(new Cookie("accessToken", token));

        when(jwtUtils.validateToken(token)).thenReturn(false);

        final boolean[] chainCalled = { false };
        FilterChain chain = (req, res) -> chainCalled[0] = true;

        filter.doFilterInternal(request, response, chain);

        assertThat("FilterChain should be invoked", chainCalled[0], is(true));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat("Authentication should NOT be set when token is invalid", auth, is(nullValue()));
        verify(jwtUtils, never()).getUsernameFromToken(any());
    }

    @Test
    void doFilterInternal_jwtException_skipsAuthentication_and_continuesChain() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String token = "expired-token";
        request.setCookies(new Cookie("accessToken", token));

        when(jwtUtils.validateToken(token)).thenReturn(true);
        when(jwtUtils.getUsernameFromToken(token)).thenThrow(new RuntimeException("Simulated JWT error"));

        final boolean[] chainCalled = { false };
        FilterChain chain = (req, res) -> chainCalled[0] = true;

        filter.doFilterInternal(request, response, chain);

        assertThat("FilterChain should be invoked", chainCalled[0], is(true));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat("Authentication should NOT be set due to exception", auth, is(nullValue()));
    }
}
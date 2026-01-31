// package com.yely.bartrack_backend.auth;

// import static org.hamcrest.MatcherAssert.assertThat;
// import static org.hamcrest.Matchers.*;
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;
// import static org.mockito.ArgumentMatchers.any;

// import java.util.Collections;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.LockedException;
// import
// org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.userdetails.UserDetails;

// import com.yely.bartrack_backend.domain.ForbiddenOperationException;
// import com.yely.bartrack_backend.domain.ResourceNotFoundException;
// import com.yely.bartrack_backend.domain.ValidationException;
// import com.yely.bartrack_backend.security.JpaUserDetailsService;
// import com.yely.bartrack_backend.security.JwtUtils;

// @ExtendWith(MockitoExtension.class)
// class AuthServiceTest {

// @Mock
// private AuthenticationManager authenticationManager;
// @Mock
// private JwtUtils jwtUtils;
// @Mock
// private JpaUserDetailsService userDetailsService;

// @Mock
// private UserDetails userDetails;
// @Mock
// private Authentication authentication;

// private AuthService authService;

// @BeforeEach
// void setUp() {
// authService = new AuthService(authenticationManager, jwtUtils,
// userDetailsService);
// }

// @Test
// void testLoginSuccess() {
// String username = "testuser";
// String password = "password123";
// String accessToken = "access_token";
// String refreshToken = "refresh_token";

// when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
// .thenReturn(authentication);
// when(authentication.getPrincipal()).thenReturn(userDetails);
// when(userDetails.getUsername()).thenReturn(username);

// when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

// when(jwtUtils.generateAccessToken(userDetails)).thenReturn(accessToken);
// when(jwtUtils.generateRefreshToken(userDetails)).thenReturn(refreshToken);

// LoginResult result = authService.login(username, password);

// assertThat(result.username(), equalTo(username));
// assertThat(result.accessToken(), equalTo(accessToken));
// assertThat(result.refreshToken(), equalTo(refreshToken));

// verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
// }

// @Test
// void testRefreshAccessTokenSuccess() {
// String refreshToken = "valid_refresh_token";
// String username = "testuser";
// String newAccessToken = "new_access_token";
// String newRefreshToken = "new_refresh_token";

// when(jwtUtils.validateToken(refreshToken)).thenReturn(true);
// when(jwtUtils.getUsernameFromToken(refreshToken)).thenReturn(username);
// when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
// when(jwtUtils.generateAccessToken(userDetails)).thenReturn(newAccessToken);
// when(jwtUtils.generateRefreshToken(userDetails)).thenReturn(newRefreshToken);

// TokenPair result = authService.refreshAccessToken(refreshToken);

// assertThat(result.accessToken(), equalTo(newAccessToken));
// assertThat(result.refreshToken(), equalTo(newRefreshToken));
// verify(jwtUtils).validateToken(refreshToken);
// }

// @Test
// void testRefreshAccessTokenWithNullToken() {
// assertThrows(ValidationException.class, () ->
// authService.refreshAccessToken(null));

// verifyNoInteractions(jwtUtils, userDetailsService);
// }

// @Test
// void testRefreshAccessTokenWithInvalidToken() {
// String invalidToken = "invalid_token";
// when(jwtUtils.validateToken(invalidToken)).thenReturn(false);

// assertThrows(ValidationException.class, () ->
// authService.refreshAccessToken(invalidToken));

// verify(userDetailsService, never()).loadUserByUsername(any());
// }

// @Test
// void login_withBlankUsername_throwsValidationException() {
// assertThrows(ValidationException.class,
// () -> authService.login("", "password"));
// }

// @Test
// void login_whenAccountLocked_throwsForbiddenException() {
// when(authenticationManager.authenticate(any()))
// .thenThrow(new LockedException("locked"));

// assertThrows(ForbiddenOperationException.class,
// () -> authService.login("user", "pass"));
// }

// @Test
// void refreshAccessToken_userNotFound_throwsException() {
// String token = "valid";

// when(jwtUtils.validateToken(token)).thenReturn(true);
// when(jwtUtils.getUsernameFromToken(token)).thenReturn("ghost");
// when(userDetailsService.loadUserByUsername("ghost")).thenReturn(null);

// assertThrows(ResourceNotFoundException.class,
// () -> authService.refreshAccessToken(token));
// }
// }
package com.yely.bartrack_backend.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yely.bartrack_backend.domain.ForbiddenOperationException;
import com.yely.bartrack_backend.domain.ValidationException;
import com.yely.bartrack_backend.user.LoginRequestDTO;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.List;

@SpringBootTest
public class AuthControllerIntegrationTest {

        @Autowired
        private WebApplicationContext context;

        @Autowired
        private ObjectMapper objectMapper;

        private MockMvc mockMvc;

        @MockitoBean
        private AuthService authService;

        private static final String API_AUTH = "/api/v1/auth";

        @BeforeEach
        void setUp() {
                mockMvc = MockMvcBuilders.webAppContextSetup(context)
                                .apply(springSecurity())
                                .build();
        }

        @Test
        @DisplayName("POST /login success - returns 200 and sets cookies")
        void testLoginSuccess() throws Exception {
                LoginRequestDTO loginReq = new LoginRequestDTO("user", "pass");
                LoginResult loginRes = new LoginResult("user", "access-token-xyz", "refresh-token-xyz",
                                List.of(new SimpleGrantedAuthority("ROLE_USER")));

                when(authService.login(any(), any())).thenReturn(loginRes);

                mockMvc.perform(post(API_AUTH + "/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginReq)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username").value("user"))
                                .andExpect(cookie().value("accessToken", "access-token-xyz"))
                                .andExpect(cookie().httpOnly("accessToken", true))
                                .andExpect(cookie().maxAge("accessToken", 900))
                                .andExpect(cookie().value("refreshToken", "refresh-token-xyz"))
                                .andExpect(cookie().maxAge("refreshToken", 604800))
                                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
        }

        @Test
        @DisplayName("POST /login → 400 when credentials are invalid")
        public void testLoginBadCredentials() throws Exception {

                doThrow(new ValidationException("Invalid username or password"))
                                .when(authService).login(any(), any());

                mockMvc.perform(post(API_AUTH + "/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                new LoginRequestDTO("user", "wrong"))))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message")
                                                .value("Invalid username or password"));
        }

        @Test
        @DisplayName("POST /login → 403 when account is locked")
        void login_lockedAccount_returns403() throws Exception {

                doThrow(new ForbiddenOperationException("User account is locked"))
                                .when(authService).login(any(), any());

                mockMvc.perform(post(API_AUTH + "/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                new LoginRequestDTO("user", "pass"))))
                                .andExpect(status().isForbidden())
                                .andExpect(jsonPath("$.message")
                                                .value("User account is locked"));
        }

        @Test
        @DisplayName("POST /refresh should return 400 Bad Request without refresh token")
        public void testRefreshEndpointIsPublicAndFailsWithoutCookie() throws Exception {

                doThrow(new ValidationException("Invalid refresh token"))
                                .when(authService).refreshAccessToken(any());

                mockMvc.perform(post(API_AUTH + "/refresh"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("Invalid refresh token"));
        }

        @Test
        @DisplayName("POST /logout should return 200 OK regardless of token presence")
        public void testLogoutEndpointIsPublicAndReturnsOk() throws Exception {
                mockMvc.perform(post(API_AUTH + "/logout"))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("POST /login should return 400 Bad Request on invalid JSON body (Validation Error)")
        void testLoginFailsOnMissingFields() throws Exception {
                final String EMPTY_LOGIN_JSON = "{}";

                mockMvc.perform(post(API_AUTH + "/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(EMPTY_LOGIN_JSON))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("Bad Request"))
                                .andExpect(jsonPath("$.message").exists());

                verifyNoInteractions(authService);
        }

        @Test
        @DisplayName("POST /login → 400 when request body is malformed JSON")
        void login_malformedJson_returns400() throws Exception {

                mockMvc.perform(post(API_AUTH + "/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("Malformed JSON request"))
                                .andExpect(jsonPath("$.error").value("Bad Request"));
        }

        @Test
        @DisplayName("POST /refresh success - returns 200 and sets new cookies")
        void refresh_withValidRefreshToken_setsNewCookies() throws Exception {

                // given
                TokenPair tokens = new TokenPair("new-access-token", "new-refresh-token");

                when(authService.refreshAccessToken("refresh-token-xyz"))
                                .thenReturn(tokens);

                // when + then
                mockMvc.perform(post(API_AUTH + "/refresh")
                                .cookie(new jakarta.servlet.http.Cookie("refreshToken", "refresh-token-xyz")))
                                .andExpect(status().isOk())
                                .andExpect(cookie().value("accessToken", "new-access-token"))
                                .andExpect(cookie().httpOnly("accessToken", true))
                                .andExpect(cookie().maxAge("accessToken", 900))
                                .andExpect(cookie().value("refreshToken", "new-refresh-token"))
                                .andExpect(cookie().httpOnly("refreshToken", true))
                                .andExpect(cookie().maxAge("refreshToken", 604800));
        }
}
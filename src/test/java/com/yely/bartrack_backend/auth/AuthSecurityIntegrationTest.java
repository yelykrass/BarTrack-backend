package com.yely.bartrack_backend.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class AuthSecurityIntegrationTest {

        @Autowired
        private WebApplicationContext context;
        private MockMvc mockMvc;

        @MockitoBean
        private AuthService authService;

        private static final String API_AUTH = "/api/v1/auth";

        private static final String INVALID_LOGIN_JSON = "{\"username\":\"a\",\"password\":\"b\"}";

        @BeforeEach
        void setUp() {
                mockMvc = MockMvcBuilders.webAppContextSetup(context)
                                .apply(springSecurity())
                                .build();
        }

        @Test
        @DisplayName("POST /login should return 401 Unauthorized for invalid credentials")
        public void testLoginEndpointIsPublicAndRequiresValidBody() throws Exception {

                doThrow(new BadCredentialsException("Bad credentials"))
                                .when(authService).login(any(), any());

                mockMvc.perform(post(API_AUTH + "/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(INVALID_LOGIN_JSON))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("POST /refresh should return 400 Bad Request without refresh token")
        public void testRefreshEndpointIsPublicAndFailsWithoutCookie() throws Exception {

                doThrow(new IllegalArgumentException("Invalid refresh token"))
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
                                .andExpect(jsonPath("$.error").value("Validation Error"))
                                .andExpect(jsonPath("$.message").exists());
        }

        @Test
        @DisplayName("POST /login should return 401 for a locked account (Skipping BadCredentials branch)")
        public void testLoginFailsOnLockedAccount() throws Exception {

                doThrow(new LockedException("User account is locked"))
                                .when(authService).login(any(), any());

                mockMvc.perform(post(API_AUTH + "/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(INVALID_LOGIN_JSON))
                                .andExpect(status().isUnauthorized())

                                .andExpect(jsonPath("$.message").value("User account is locked"));
        }

        @Test
        @DisplayName("API should return 500 Internal Server Error for unhandled exceptions (Mocked)")
        void testUnhandledExceptionReturns500() throws Exception {
                doThrow(new NullPointerException("Simulated error")).when(authService).login(any(), any());

                mockMvc.perform(post(API_AUTH + "/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(INVALID_LOGIN_JSON))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.error").value("Internal Server Error"));
        }

}
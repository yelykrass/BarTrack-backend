package com.yely.bartrack_backend.auth;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import com.yely.bartrack_backend.user.LoginRequestDTO;
import com.yely.bartrack_backend.user.LoginResponseDTO;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController controller;

    private final String apiEndpoint = "/api/v1";

    @BeforeEach
    void setUp() throws Exception {
        ReflectionTestUtils.setField(controller, "apiEndpoint", apiEndpoint);
        ReflectionTestUtils.setField(controller, "accessExpMs", 3600000);
        ReflectionTestUtils.setField(controller, "refreshExpMs", 86400000);
    }

    @Test
    void login_returnsLoginResponse_and_setsCookies() {
        MockHttpServletResponse resp = new MockHttpServletResponse();
        LoginRequestDTO reqDto = new LoginRequestDTO("alice", "secret");

        LoginResult loginResult = mock(LoginResult.class);
        when(loginResult.username()).thenReturn("alice");
        when(loginResult.accessToken()).thenReturn("access-token-xyz");
        when(loginResult.refreshToken()).thenReturn("refresh-token-xyz");
        GrantedAuthority role = () -> "ROLE_USER";
        doReturn(List.of(role)).when(loginResult).authorities();

        when(authService.login(eq("alice"), eq("secret"))).thenReturn(loginResult);

        ResponseEntity<LoginResponseDTO> respEntity = controller.login(reqDto, resp);

        assertThat(respEntity, is(notNullValue()));
        assertThat(respEntity.getStatusCode().value(), is(200));
        LoginResponseDTO body = respEntity.getBody();
        assertThat(body, is(notNullValue()));
        assertThat(body.username(), is("alice"));
        assertThat(body.roles(), contains("ROLE_USER"));
        assertThat(body.active(), is(true));

        assertThat(resp.getHeaderNames(), hasItem("Set-Cookie"));
        assertThat(resp.getHeaders("Set-Cookie").size(), greaterThanOrEqualTo(1));
        verify(authService).login("alice", "secret");
    }

    @Test
    void refresh_rotatesTokens_and_setsCookies() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse resp = new MockHttpServletResponse();

        req.setCookies(new Cookie("refreshToken", "refresh-123"));

        TokenPair tokens = mock(TokenPair.class);
        when(tokens.accessToken()).thenReturn("new-access");
        when(tokens.refreshToken()).thenReturn("new-refresh");

        when(authService.refreshAccessToken(eq("refresh-123"))).thenReturn(tokens);

        ResponseEntity<?> result = controller.refresh(req, resp);

        assertThat(result.getStatusCode().value(), is(200));
        assertThat(resp.getHeaderNames(), hasItem("Set-Cookie"));
        assertThat(resp.getHeaders("Set-Cookie").size(), greaterThanOrEqualTo(1));
        verify(authService).refreshAccessToken("refresh-123");
    }

    @Test
    void logout_deletesCookies_and_returnsOk() {
        MockHttpServletResponse resp = new MockHttpServletResponse();

        ResponseEntity<?> result = controller.logout(resp);

        assertThat(result.getStatusCode().value(), is(200));
        assertThat(resp.getHeaderNames(), hasItem("Set-Cookie"));
        assertThat(resp.getHeaders("Set-Cookie").size(), greaterThanOrEqualTo(1));
    }
}
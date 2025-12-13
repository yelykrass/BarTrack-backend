package com.yely.bartrack_backend.auth;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import com.yely.bartrack_backend.security.CookieUtil;
import com.yely.bartrack_backend.user.LoginRequestDTO;
import com.yely.bartrack_backend.user.LoginResponseDTO;

@Slf4j
@RestController
@RequestMapping(path = "${api-endpoint}/auth")
@RequiredArgsConstructor
public class AuthController {

        private final AuthService authService;

        @Value("${api-endpoint}")
        private String apiEndpoint;

        @Value("${jwt.access-exp-ms}")
        private long accessExpMs;

        @Value("${jwt.refresh-exp-ms}")
        private long refreshExpMs;

        private int accessExpSeconds() {
                return Math.toIntExact(accessExpMs / 1000);
        }

        private int refreshExpSeconds() {
                return Math.toIntExact(refreshExpMs / 1000);
        }

        @PostMapping("/login")
        public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request,
                        HttpServletResponse resp) {
                LoginResult result = authService.login(request.username(), request.password());

                CookieUtil.addCookie(resp,
                                CookieUtil.buildCookie("accessToken", result.accessToken(),
                                                accessExpSeconds(), true, true, "Lax", "/"),
                                "Lax");

                CookieUtil.addCookie(resp,
                                CookieUtil.buildCookie("refreshToken", result.refreshToken(),
                                                refreshExpSeconds(), true, true,
                                                "Strict", apiEndpoint
                                                                + "/auth"),
                                "Strict");

                List<String> roles = result.authorities().stream().map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList());
                boolean active = true;

                return ResponseEntity.ok(new LoginResponseDTO(result.username(), roles, active));
        }

        @PostMapping("/refresh")
        public ResponseEntity<?> refresh(HttpServletRequest req, HttpServletResponse resp) {
                String refresh = CookieUtil.getCookieValue(req, "refreshToken").orElse(null);
                TokenPair tokens = authService.refreshAccessToken(refresh);

                CookieUtil.addCookie(resp,
                                CookieUtil.buildCookie("accessToken", tokens.accessToken(),
                                                accessExpSeconds(), true, true, "Lax", "/"),
                                "Lax");

                CookieUtil.addCookie(resp,
                                CookieUtil.buildCookie("refreshToken", tokens.refreshToken(),
                                                refreshExpSeconds(), true, true,
                                                "Strict", apiEndpoint
                                                                + "/auth"),
                                "Strict");

                return ResponseEntity.ok().build();
        }

        @PostMapping("/logout")
        public ResponseEntity<?> logout(HttpServletResponse resp) {
                CookieUtil.deleteCookie(resp, "accessToken", "/", "Lax");
                CookieUtil.deleteCookie(resp, "refreshToken", apiEndpoint + "/auth", "Strict");
                return ResponseEntity.ok().build();
        }

}
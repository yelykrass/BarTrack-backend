package com.yely.bartrack_backend.auth;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import com.yely.bartrack_backend.security.CookieUtil;
import com.yely.bartrack_backend.user.LoginRequestDTO;
import com.yely.bartrack_backend.user.LoginResponseDTO;

@RestController
@RequestMapping(path = "${api-endpoint}/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request,
            HttpServletResponse resp) {
        LoginResult result = authService.login(request.username(), request.password());

        // create cookies
        CookieUtil.addCookie(resp,
                CookieUtil.buildCookie("accessToken", result.accessToken(), (int) (900), true, true, "Lax", "/"),
                "Lax");

        CookieUtil.addCookie(resp,
                CookieUtil.buildCookie("refreshToken", result.refreshToken(), (int) (60 * 60 * 24 * 7), true, true,
                        "Strict", "/api/v1/auth"),
                "Strict");

        List<String> roles = result.authorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        boolean active = true; // якщо хочеш, можна витягнути з SecurityUser

        return ResponseEntity.ok(new LoginResponseDTO(result.username(), roles, active));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest req, HttpServletResponse resp) {
        String refresh = CookieUtil.getCookieValue(req, "refreshToken").orElse(null);
        TokenPair tokens = authService.refreshAccessToken(refresh);

        CookieUtil.addCookie(resp,
                CookieUtil.buildCookie("accessToken", tokens.accessToken(), (int) (900), true, true, "Lax", "/"),
                "Lax");

        // rotate refresh token
        CookieUtil.addCookie(resp,
                CookieUtil.buildCookie("refreshToken", tokens.refreshToken(), (int) (60 * 60 * 24 * 7), true, true,
                        "Strict", "/api/v1/auth"),
                "Strict");

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse resp) {
        CookieUtil.deleteCookie(resp, "accessToken", "/", "Lax");
        CookieUtil.deleteCookie(resp, "refreshToken", "/api/v1/auth", "Strict");
        return ResponseEntity.ok().build();
    }

    // @GetMapping("/login")
    // public ResponseEntity<AuthDTOResponse> login() {
    // SecurityContext contextHolder = SecurityContextHolder.getContext();
    // Authentication auth = contextHolder.getAuthentication();

    // if (auth == null || !auth.isAuthenticated() ||
    // auth.getPrincipal().equals("anonymousUser")) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    // .body(new AuthDTOResponse("Unauthorized", null, null, false));
    // }

    // Object principal = auth.getPrincipal();

    // String username = auth.getName();
    // String role = auth.getAuthorities().iterator().next().getAuthority();
    // boolean isActive = true;

    // // Якщо principal — це SecurityUser, беремо статус активності з нього
    // if (principal instanceof SecurityUser securityUser) {
    // isActive = securityUser.isEnabled();
    // username = securityUser.getUsername();
    // }

    // AuthDTOResponse authResponse = new AuthDTOResponse("Logged", username, role,
    // isActive);

    // return ResponseEntity.accepted().body(authResponse);
    // }

    // @GetMapping("/private")
    // public ResponseEntity<String> foo() {
    // System.out.println("ffffff");
    // return ResponseEntity.ok().body("Foo");
    // }

    // @GetMapping("/check-session")
    // public ResponseEntity<?> checkSession(Authentication authentication) {
    // if (authentication != null && authentication.isAuthenticated()) {
    // Object principal = authentication.getPrincipal();
    // UserDetails userDetails = (UserDetails) principal;

    // // Якщо ти використовуєш свій клас SecurityUser, то можна привести його до
    // // нього:
    // boolean isActive = true;
    // if (userDetails instanceof SecurityUser securityUser) {
    // isActive = securityUser.isEnabled();
    // }

    // Map<String, Object> user = new HashMap<>();
    // user.put("username", userDetails.getUsername());
    // user.put("role", userDetails.getAuthorities().stream()
    // .map(a -> a.getAuthority())
    // .findFirst().orElse("USER"));
    // user.put("active", isActive); // 🟢 додай це поле

    // Map<String, Object> response = new HashMap<>();
    // response.put("auth", true);
    // response.put("user", user);

    // return ResponseEntity.ok(response);
    // } else {
    // Map<String, Object> response = new HashMap<>();
    // response.put("auth", false);
    // response.put("user", null);
    // return ResponseEntity.ok(response);
    // }
    // }
}

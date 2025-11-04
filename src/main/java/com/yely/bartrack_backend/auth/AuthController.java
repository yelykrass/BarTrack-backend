package com.yely.bartrack_backend.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yely.bartrack_backend.security.SecurityUser;

@RestController
@RequestMapping(path = "${api-endpoint}")
public class AuthController {

    @GetMapping("/login")
    public ResponseEntity<AuthDTOResponse> login() {
        SecurityContext contextHolder = SecurityContextHolder.getContext();
        Authentication auth = contextHolder.getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthDTOResponse("Unauthorized", null, null, false));
        }

        Object principal = auth.getPrincipal();

        String username = auth.getName();
        String role = auth.getAuthorities().iterator().next().getAuthority();
        boolean isActive = true;

        // Якщо principal — це SecurityUser, беремо статус активності з нього
        if (principal instanceof SecurityUser securityUser) {
            isActive = securityUser.isEnabled();
            username = securityUser.getUsername();
        }

        AuthDTOResponse authResponse = new AuthDTOResponse("Logged", username, role, isActive);

        return ResponseEntity.accepted().body(authResponse);
    }

    @GetMapping("/private")
    public ResponseEntity<String> foo() {
        System.out.println("ffffff");
        return ResponseEntity.ok().body("Foo");
    }

    @GetMapping("/check-session")
    public ResponseEntity<?> checkSession(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            UserDetails userDetails = (UserDetails) principal;

            // Якщо ти використовуєш свій клас SecurityUser, то можна привести його до
            // нього:
            boolean isActive = true;
            if (userDetails instanceof SecurityUser securityUser) {
                isActive = securityUser.isEnabled();
            }

            Map<String, Object> user = new HashMap<>();
            user.put("username", userDetails.getUsername());
            user.put("role", userDetails.getAuthorities().stream()
                    .map(a -> a.getAuthority())
                    .findFirst().orElse("USER"));
            user.put("active", isActive); // 🟢 додай це поле

            Map<String, Object> response = new HashMap<>();
            response.put("auth", true);
            response.put("user", user);

            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("auth", false);
            response.put("user", null);
            return ResponseEntity.ok(response);
        }
    }
}

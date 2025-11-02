package com.yely.bartrack_backend.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "${api-endpoint}")
public class AuthController {

    @GetMapping("/login")
    public ResponseEntity<AuthDTOResponse> login() {

        SecurityContext contextHolder = SecurityContextHolder.getContext();
        Authentication auth = contextHolder.getAuthentication();

        AuthDTOResponse authResponse = new AuthDTOResponse("Logged", auth.getName(),
                auth.getAuthorities().iterator().next().getAuthority());

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
            // отримуємо користувача з Authentication
            Object principal = authentication.getPrincipal();
            // якщо використовуєш UserDetails
            UserDetails userDetails = (UserDetails) principal;

            // формуємо об’єкт користувача для Frontend
            Map<String, Object> user = new HashMap<>();
            user.put("username", userDetails.getUsername());
            user.put("role", userDetails.getAuthorities().stream()
                    .map(a -> a.getAuthority())
                    .findFirst().orElse("USER"));

            Map<String, Object> response = new HashMap<>();
            response.put("auth", true);
            response.put("user", user);

            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("auth", false);
            response.put("user", null);
            return ResponseEntity.ok(response); // важливо: повертаємо 200, а не 401
        }
    }
}

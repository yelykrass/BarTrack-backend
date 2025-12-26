package com.yely.bartrack_backend.register;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yely.bartrack_backend.user.UserEntity;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/register")
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("")
    public ResponseEntity<RegisterDTOResponse> registerUser(@Valid @RequestBody RegisterDTORequest dto) {
        UserEntity user = registerService.registerUser(dto);

        return ResponseEntity.status(201)
                .body(RegisterDTOResponse.builder()
                        .message("User registered successfully")
                        .username(user.getUsername())
                        .build());
    }

}

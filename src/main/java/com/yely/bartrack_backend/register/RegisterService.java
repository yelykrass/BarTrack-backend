package com.yely.bartrack_backend.register;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.yely.bartrack_backend.domain.ValidationException;
import com.yely.bartrack_backend.user.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public RegisterDTOResponse registerUser(RegisterDTORequest dto) {

        if (dto.username() == null || dto.username().isBlank()
                || dto.password() == null || dto.password().isBlank()) {
            throw new ValidationException("Username and password must be provided");
        }

        if (userService.existsByUsername(dto.username())) {
            throw new ValidationException("User already exists");
        }

        String hashedPassword = passwordEncoder.encode(dto.password());

        userService.save(
                dto.username(),
                hashedPassword);

        return new RegisterDTOResponse(
                "User registered successfully",
                dto.username());
    }
}

package com.yely.bartrack_backend.register;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterDTORequest(
        @NotBlank @Email(message = "Invalid email format") @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Email must contain only Latin characters") String username,

        @NotBlank @Size(min = 8, max = 64) String password) {
}

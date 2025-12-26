package com.yely.bartrack_backend.register;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDTORequest(
        @NotBlank @Email(message = "Invalid email format") String username,

        @NotBlank @Size(min = 8, max = 64) String password) {
}

package com.yely.bartrack_backend.register;

import org.springframework.stereotype.Component;

import com.yely.bartrack_backend.domain.ValidationException;

@Component
public class PasswordPolicy {

    public void validate(String password) {
        if (password.length() < 10) {
            throw new ValidationException("Password must be at least 10 characters long");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new ValidationException("Password must contain an uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new ValidationException("Password must contain a lowercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            throw new ValidationException("Password must contain a digit");
        }
        if (!password.matches(".*[@#$%!^&*].*")) {
            throw new ValidationException("Password must contain a special character");
        }
    }
}

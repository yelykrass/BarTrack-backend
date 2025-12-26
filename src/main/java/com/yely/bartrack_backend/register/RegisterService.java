package com.yely.bartrack_backend.register;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.yely.bartrack_backend.domain.ValidationException;
import com.yely.bartrack_backend.role.RoleService;
import com.yely.bartrack_backend.user.UserEntity;
import com.yely.bartrack_backend.user.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordPolicy passwordPolicy;
    private final PasswordEncoder passwordEncoder;

    public UserEntity registerUser(RegisterDTORequest dto) {
        String username = dto.username().toLowerCase();

        if (userService.existsByUsername(username)) {
            throw new ValidationException("User with this email already exists");
        }

        passwordPolicy.validate(dto.password());

        String hashedPassword = passwordEncoder.encode(dto.password());

        UserEntity newUser = UserEntity.builder()
                .username(username)
                .password(hashedPassword)
                .roles(roleService.assignDefaultRole())
                .active(true)
                .build();

        return userService.save(newUser);
    }
}
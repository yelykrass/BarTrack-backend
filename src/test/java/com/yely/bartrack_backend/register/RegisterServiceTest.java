package com.yely.bartrack_backend.register;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.yely.bartrack_backend.domain.ValidationException;
import com.yely.bartrack_backend.role.RoleEntity;
import com.yely.bartrack_backend.role.RoleService;
import com.yely.bartrack_backend.user.UserEntity;
import com.yely.bartrack_backend.user.UserService;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordPolicy passwordPolicy;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterService registerService;

    @Test
    @DisplayName("Should successfully register user with encoded password and lowercase email")
    void registerUser_successfullyCreatesUser() {
        String rawEmail = "Test@Email.com";
        String lowerEmail = "test@email.com";
        String rawPass = "StrongPass123!";
        String encodedPass = "hashed-password";

        RegisterDTORequest dto = new RegisterDTORequest(rawEmail, rawPass);
        RoleEntity role = new RoleEntity();

        when(userService.existsByUsername(lowerEmail)).thenReturn(false);
        when(passwordEncoder.encode(rawPass)).thenReturn(encodedPass);
        when(roleService.assignDefaultRole()).thenReturn(Set.of(role));

        when(userService.save(any(UserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserEntity result = registerService.registerUser(dto);

        assertThat("Username should be converted to lowercase", result.getUsername(), is(lowerEmail));
        assertThat("Password should be encoded", result.getPassword(), is(encodedPass));
        assertThat("User should be active by default", result.isActive(), is(true));
        assertThat("User should have default roles", result.getRoles(), hasItem(role));

        verify(passwordPolicy).validate(rawPass);
        verify(userService).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should throw ValidationException when user already exists")
    void registerUser_throwsValidationException_whenUserAlreadyExists() {

        RegisterDTORequest dto = new RegisterDTORequest("existing@email.com", "pass");

        when(userService.existsByUsername("existing@email.com")).thenReturn(true);

        assertThrows(ValidationException.class,
                () -> registerService.registerUser(dto));

        verify(userService, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    @DisplayName("Should throw ValidationException when password policy fails")
    void registerUser_throwsValidationException_whenPasswordInvalid() {

        RegisterDTORequest dto = new RegisterDTORequest("user@email.com", "weak");

        when(userService.existsByUsername("user@email.com")).thenReturn(false);

        doThrow(new ValidationException("Weak password"))
                .when(passwordPolicy).validate("weak");

        assertThrows(ValidationException.class,
                () -> registerService.registerUser(dto));

        verify(userService, never()).save(any());
    }
}
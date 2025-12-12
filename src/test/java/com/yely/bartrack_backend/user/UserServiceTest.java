package com.yely.bartrack_backend.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void save_returnsSavedEntity() {
        UserEntity user = org.mockito.Mockito.mock(UserEntity.class);
        when(userRepository.save(user)).thenReturn(user);

        UserEntity result = userService.save(user);

        assertThat(result, sameInstance(user));
    }

    @Test
    void getUserById_returnsWhenFound() {
        UserEntity user = org.mockito.Mockito.mock(UserEntity.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserEntity result = userService.getUserById(1L);

        assertThat(result, sameInstance(user));
    }

    @Test
    void getUserById_throwsWhenNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.getUserById(2L));
        assertThat(ex.getMessage(), is("User not found"));
    }

    @Test
    void getCurrentUser_returnsUserForAuthenticatedPrincipal() {
        String username = "alice";
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, null));

        UserEntity user = org.mockito.Mockito.mock(UserEntity.class);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserEntity result = userService.getCurrentUser();

        assertThat(result, sameInstance(user));
    }

    @Test
    void getCurrentUser_throwsWhenRepositoryDoesNotFindUser() {
        String username = "bob";
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, null));
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.getCurrentUser());
        assertThat(ex.getMessage(), is("Current user not found"));
    }
}
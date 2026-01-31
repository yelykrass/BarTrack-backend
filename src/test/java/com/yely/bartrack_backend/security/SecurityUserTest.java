package com.yely.bartrack_backend.security;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import com.yely.bartrack_backend.role.RoleEntity;
import com.yely.bartrack_backend.user.UserEntity;

@ExtendWith(MockitoExtension.class)
public class SecurityUserTest {

    @Mock
    private UserEntity userEntity;

    @Mock
    private RoleEntity roleEntity;

    private SecurityUser securityUser;

    @BeforeEach
    public void setUp() {
        securityUser = new SecurityUser(userEntity);
    }

    @Test
    public void testGetUsername() {
        when(userEntity.getUsername()).thenReturn("testuser");
        assertThat(securityUser.getUsername(), equalTo("testuser"));
    }

    @Test
    public void testGetPassword() {
        when(userEntity.getPassword()).thenReturn("hashedpassword");
        assertThat(securityUser.getPassword(), equalTo("hashedpassword"));
    }

    @Test
    void testGetAuthorities_WithSingleRole() {
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(roleEntity);

        when(userEntity.getRoles()).thenReturn(roles);
        when(roleEntity.getName()).thenReturn("ROLE_USER");

        Collection<? extends GrantedAuthority> authorities = securityUser.getAuthorities();

        assertThat(authorities, hasSize(1));
        assertThat(authorities, hasItem(hasProperty("authority",
                equalTo("ROLE_USER"))));

        verify(roleEntity).getName();
    }

    @Test
    void testGetAuthorities_NoRoles() {
        when(userEntity.getRoles()).thenReturn(Collections.emptySet());

        Collection<? extends GrantedAuthority> authorities = securityUser.getAuthorities();

        assertThat(authorities, is(empty()));
        verify(roleEntity, never()).getName();
    }

    @Test
    public void testIsAccountNonExpired() {
        assertThat(securityUser.isAccountNonExpired(), is(true));
    }

    @Test
    public void testIsAccountNonLocked() {
        assertThat(securityUser.isAccountNonLocked(), is(true));
    }

    @Test
    public void testIsCredentialsNonExpired() {
        assertThat(securityUser.isCredentialsNonExpired(), is(true));
    }

    @Test
    public void testIsEnabledTrue() {
        when(userEntity.isActive()).thenReturn(true);
        assertThat(securityUser.isEnabled(), is(true));
    }

    @Test
    public void testIsEnabledFalse() {
        when(userEntity.isActive()).thenReturn(false);
        assertThat(securityUser.isEnabled(), is(false));
    }
}
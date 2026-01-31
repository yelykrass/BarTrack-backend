package com.yely.bartrack_backend.role;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.when;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.yely.bartrack_backend.domain.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository repository;

    @InjectMocks
    private RoleService service;

    @Test
    void getById_returnsEntity_whenFound() {
        RoleEntity role = org.mockito.Mockito.mock(RoleEntity.class);
        when(repository.findById(1L)).thenReturn(Optional.of(role));

        RoleEntity result = service.getById(1L);

        assertThat(result, is(sameInstance(role)));
    }

    @Test
    void getById_throwsResourceNotFoundException_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(ResourceNotFoundException.class,
                () -> service.getById(99L));
    }

    @Test
    void assignDefaultRole_returnsSetContainingDefaultRole() {
        RoleEntity defaultRole = org.mockito.Mockito.mock(RoleEntity.class);
        when(repository.findById(2L)).thenReturn(Optional.of(defaultRole));

        Set<RoleEntity> roles = service.assignDefaultRole();

        assertThat(roles, hasSize(1));
        assertThat(roles, hasItem(defaultRole));
    }
}
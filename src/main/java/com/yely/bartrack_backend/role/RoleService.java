package com.yely.bartrack_backend.role;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository repository;

    public RoleEntity getById(Long id) {
        return repository.findById(id).orElseThrow(); // OJO se devería devolver una exception concreta
    }

    public Set<RoleEntity> assignDefaultRole() {
        RoleEntity defaultRole = this.getById(2L);

        Set<RoleEntity> roles = new HashSet<>();
        roles.add(defaultRole);

        return roles;
    }

}

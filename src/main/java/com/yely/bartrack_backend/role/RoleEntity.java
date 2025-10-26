package com.yely.bartrack_backend.role;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yely.bartrack_backend.user.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "roles")
@Data

public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role")
    private Long id;
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private Set<UserEntity> users;

}

// public static RoleEntity adminRole() {
// return RoleEntity.builder()
// .name("ROLE_ADMIN")

// .build();
// }

// public static RoleEntity clientRole() {
// return RoleEntity.builder()
// .name("ROLE_USER")
// .build();
// }

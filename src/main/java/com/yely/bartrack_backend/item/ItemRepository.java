package com.yely.bartrack_backend.item;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    List<ItemEntity> findByActiveTrue();

    boolean existsByName(String name);
}

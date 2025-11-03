package com.yely.bartrack_backend.inventory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    List<ItemEntity> findByUserId(Long userId);
}

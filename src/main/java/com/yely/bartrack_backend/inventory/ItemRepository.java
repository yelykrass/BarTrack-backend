package com.yely.bartrack_backend.inventary;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    List<ItemEntity> findByCategory(String category);

    List<ItemEntity> findByQuantityLessThan(int quantity);
}

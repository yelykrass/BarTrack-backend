package com.yely.bartrack_backend.inventary;

import java.time.LocalDateTime;

import com.yely.bartrack_backend.user.UserEntity;

public class ItemMapper {
    public static ItemEntity toEntity(ItemDTORequest dto, UserEntity user) {
        return ItemEntity.builder()
                .name(dto.name())
                .quantity(dto.quantity())
                .unitPrice(dto.unitPrice())
                .category(dto.category())
                .supplier(dto.supplier())
                .expiryDate(dto.expiryDate())
                .createdBy(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static ItemDTOResponse toDTO(ItemEntity e) {
        return new ItemDTOResponse(
                e.getId(),
                e.getName(),
                e.getQuantity(),
                e.getUnitPrice(),
                e.getCategory(),
                e.getSupplier(),
                e.getExpiryDate(),
                e.getCreatedAt(),
                e.getUpdatedAt());
    }
}

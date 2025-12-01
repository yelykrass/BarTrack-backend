package com.yely.bartrack_backend.item;

import com.yely.bartrack_backend.user.UserEntity;

public class ItemMapper {

    public static ItemEntity toEntity(ItemDTORequest dto, UserEntity user) {
        return ItemEntity.builder()
                .name(dto.name())
                .category(dto.category())
                .quantity(dto.quantity())
                .price(dto.price())
                .expiryDate(dto.expiryDate())
                .user(user)
                .build();
    }

    public static ItemDTOResponse toDTO(ItemEntity e) {
        return new ItemDTOResponse(
                e.getId(),
                e.getName(),
                e.getCategory(),
                e.getQuantity(),
                e.getPrice(),
                e.getExpiryDate(),
                e.getUser().getUsername());
    }
}

package com.yely.bartrack_backend.item;

public class ItemMapper {

    public static ItemEntity toEntity(ItemDTORequest dto) {
        if (dto == null)
            return null;
        return ItemEntity.builder()
                .name(dto.name())
                .category(dto.category())
                .quantity(dto.quantity())
                .price(dto.price())
                .active(dto.active() != null ? dto.active() : true)
                .build();
    }

    public static ItemDTOResponse toDTO(ItemEntity entity) {
        if (entity == null)
            return null;
        return ItemDTOResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .category(entity.getCategory())
                .quantity(entity.getQuantity())
                .price(entity.getPrice())
                .active(entity.isActive())
                .build();
    }
}

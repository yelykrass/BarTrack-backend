package com.yely.bartrack_backend.item;

import lombok.Builder;

@Builder
public record ItemDTOResponse(Long id,
        String name,
        String category,
        Integer quantity,
        Double price,
        boolean active) {

}

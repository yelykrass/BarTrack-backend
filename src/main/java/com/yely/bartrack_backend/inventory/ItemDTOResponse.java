package com.yely.bartrack_backend.inventory;

import java.time.LocalDate;

public record ItemDTOResponse(Long id,
        String name,
        String category,
        int quantity,
        double price,
        LocalDate expiryDate,
        String username) {

}

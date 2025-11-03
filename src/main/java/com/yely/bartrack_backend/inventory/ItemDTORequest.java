package com.yely.bartrack_backend.inventary;

import java.time.LocalDate;

public record ItemDTORequest(String name,
        int quantity,
        double unitPrice,
        String category,
        String supplier,
        LocalDate expiryDate,
        Long userId) {

}

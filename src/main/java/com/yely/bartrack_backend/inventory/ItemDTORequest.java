package com.yely.bartrack_backend.inventory;

import java.time.LocalDate;

public record ItemDTORequest(String name,
                int quantity,
                double unitPrice,
                String category,
                String supplier,
                LocalDate expiryDate,
                Long userId) {

}

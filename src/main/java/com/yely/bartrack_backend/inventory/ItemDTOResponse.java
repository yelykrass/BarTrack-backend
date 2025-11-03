package com.yely.bartrack_backend.inventory;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ItemDTOResponse(Long id,
                String name,
                int quantity,
                double unitPrice,
                String category,
                String supplier,
                LocalDate expiryDate,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {

}

package com.yely.bartrack_backend.inventory;

import java.time.LocalDate;

public record ItemDTORequest(String name,
                String category,
                int quantity,
                double price,
                LocalDate expiryDate) {

}

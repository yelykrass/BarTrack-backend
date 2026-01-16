package com.yely.bartrack_backend.item;

public record ItemDTORequest(String name,
                String category,
                Integer quantity,
                Double price,
                Boolean active) {

}

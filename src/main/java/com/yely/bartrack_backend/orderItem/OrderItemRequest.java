package com.yely.bartrack_backend.orderItem;

public record OrderItemRequest(
        Long itemId,
        Integer quantity) {
}

package com.yely.bartrack_backend.order;

import java.util.List;

import com.yely.bartrack_backend.orderItem.OrderItemRequest;

public record OrderDTORequest(List<OrderItemRequest> items,
        PaymentMethod paymentMethod) {

}

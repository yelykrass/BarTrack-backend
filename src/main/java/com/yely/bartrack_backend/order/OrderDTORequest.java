package com.yely.bartrack_backend.order;

import java.util.List;

import com.yely.bartrack_backend.orderItem.OrderItemRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record OrderDTORequest(
                @NotEmpty(message = "Order must contain at least one item") @Valid List<OrderItemRequest> items,
                @NotNull(message = "Payment method must be specified") PaymentMethod paymentMethod) {

}

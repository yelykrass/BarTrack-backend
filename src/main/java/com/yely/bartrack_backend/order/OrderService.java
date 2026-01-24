package com.yely.bartrack_backend.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yely.bartrack_backend.domain.ValidationException;
import com.yely.bartrack_backend.item.ItemEntity;
import com.yely.bartrack_backend.item.ItemService;
import com.yely.bartrack_backend.orderItem.OrderItemEntity;
import com.yely.bartrack_backend.orderItem.OrderItemRequest;
import com.yely.bartrack_backend.user.UserEntity;
import com.yely.bartrack_backend.user.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemService itemService;
    private final UserService userService;

    private OrderItemEntity createOrderItem(OrderItemRequest req) {
        ItemEntity item = itemService.getEntityById(req.itemId());

        if (item.getQuantity() < req.quantity()) {
            throw new ValidationException("Not enough stock for: " + item.getName());
        }

        item.setQuantity(item.getQuantity() - req.quantity());

        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setItem(item);
        orderItem.setQuantity(req.quantity());
        orderItem.setPricePerUnit(item.getPrice());
        return orderItem;
    }

    @Transactional
    public void create(OrderDTORequest request) {
        UserEntity user = userService.getCurrentUser();
        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setPaymentMethod(request.paymentMethod());

        for (OrderItemRequest itemRequest : request.items()) {
            OrderItemEntity orderItem = createOrderItem(itemRequest);
            order.addOrderItem(orderItem);
        }

        order.calculateAndSetTotal();

        orderRepository.save(order);
    }
}

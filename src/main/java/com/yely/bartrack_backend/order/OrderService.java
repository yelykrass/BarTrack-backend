package com.yely.bartrack_backend.order;

import org.springframework.stereotype.Service;

import com.yely.bartrack_backend.item.ItemService;
import com.yely.bartrack_backend.user.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemService itemService;
    private final UserService userService;

}

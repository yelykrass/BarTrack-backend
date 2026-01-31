package com.yely.bartrack_backend.order;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.yely.bartrack_backend.item.ItemEntity;
import com.yely.bartrack_backend.item.ItemService;
import com.yely.bartrack_backend.orderItem.OrderItemRequest;
import com.yely.bartrack_backend.user.UserEntity;
import com.yely.bartrack_backend.user.UserService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("Should create order, calculate total and save to repository")
    void create_ShouldSaveOrderWithCorrectData() {
        Long itemId = 100L;
        Integer quantity = 2;
        Double pricePerUnit = 50.0;
        PaymentMethod paymentMethod = PaymentMethod.CARD;

        UserEntity mockUser = new UserEntity();
        mockUser.setId(1L);
        mockUser.setUsername("test@bar.com");

        ItemEntity mockItem = new ItemEntity();
        mockItem.setId(itemId);
        mockItem.setPrice(pricePerUnit);
        mockItem.setName("Guinness");
        mockItem.setQuantity(10); // На складі достатньо
        mockItem.setActive(true);

        OrderItemRequest itemRequest = new OrderItemRequest(itemId, quantity);
        OrderDTORequest orderRequest = new OrderDTORequest(List.of(itemRequest),
                paymentMethod);

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(itemService.getEntityById(itemId)).thenReturn(mockItem);

        doNothing().when(itemService).sellItem(itemId, quantity);

        orderService.create(orderRequest);

        verify(itemService).sellItem(itemId, quantity);

        ArgumentCaptor<OrderEntity> orderCaptor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(orderRepository).save(orderCaptor.capture());

        OrderEntity savedOrder = orderCaptor.getValue();

        assertThat(savedOrder, notNullValue());

        assertThat(savedOrder.getUser(), is(mockUser));

        assertThat(savedOrder.getPaymentMethod(), is(paymentMethod));

        assertThat(savedOrder.getItems(), hasSize(1));
        assertThat(savedOrder.getItems().get(0).getItem(), is(mockItem));
        assertThat(savedOrder.getItems().get(0).getQuantity(), is(quantity));

        assertThat(savedOrder.getTotalPrice(), is(closeTo(100.0, 0.01)));
    }
}

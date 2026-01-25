package com.yely.bartrack_backend.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yely.bartrack_backend.item.ItemEntity;
import com.yely.bartrack_backend.item.ItemRepository;
import com.yely.bartrack_backend.orderItem.OrderItemRequest;
import com.yely.bartrack_backend.role.RoleEntity;
import com.yely.bartrack_backend.role.RoleRepository;
import com.yely.bartrack_backend.user.UserEntity;
import com.yely.bartrack_backend.user.UserRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {

        orderRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        RoleEntity adminRole = new RoleEntity();
        adminRole.setName("ROLE_ADMIN");
        adminRole = roleRepository.save(adminRole);

        testUser = UserEntity.builder()
                .username("test@bar.com")
                .password("password")
                .active(true)
                .roles(Set.of(adminRole))
                .build();

        userRepository.save(testUser);
    }

    private ItemEntity createItemInDb(String name, int quantity, double price) {
        ItemEntity item = new ItemEntity();
        item.setName(name);
        item.setCategory("Drinks");
        item.setPrice(price);
        item.setQuantity(quantity);
        item.setActive(true);
        return itemRepository.save(item);
    }

    @Test
    @DisplayName("Successfully create order: Returns 201 and decreases stock")
    @WithMockUser(username = "test@bar.com", roles = "USER")
    void create_Success() throws Exception {

        ItemEntity beer = createItemInDb("Guinness", 10, 150.0);

        OrderItemRequest itemRequest = new OrderItemRequest(beer.getId(), 2);
        OrderDTORequest orderRequest = new OrderDTORequest(
                List.of(itemRequest),
                PaymentMethod.CASH);

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))

                .andExpect(status().isCreated());

        ItemEntity updatedBeer = itemRepository.findById(beer.getId()).orElseThrow();
        assertEquals(8, updatedBeer.getQuantity(), "Stock should decrease by 2");
    }

    @Test
    @DisplayName("Fail to create order: Not enough stock (Returns 400)")
    @WithMockUser(username = "test@bar.com", roles = "USER")
    void create_NotEnoughStock_BadRequest() throws Exception {
        ItemEntity rareWhisky = createItemInDb("Rare Whisky", 1, 500.0);

        OrderItemRequest itemRequest = new OrderItemRequest(rareWhisky.getId(), 5);
        OrderDTORequest orderRequest = new OrderDTORequest(
                List.of(itemRequest),
                PaymentMethod.CARD);

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest());
    }

}

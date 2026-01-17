package com.yely.bartrack_backend.item;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
    }

    private ItemEntity createItemInDb(String name, boolean active) {
        ItemEntity item = new ItemEntity();
        item.setName(name);
        item.setCategory("Test Category");
        item.setPrice(100.0);
        item.setQuantity(10);
        item.setActive(active);
        return itemRepository.save(item);
    }

    @Test
    @DisplayName("ADMIN must see ALL products (active and inactive)")
    @WithMockUser(roles = "ADMIN")
    void getAll_AsAdmin_ReturnsAllItems() throws Exception {

        createItemInDb("Active Beer", true);
        createItemInDb("Hidden Vodka", false);

        mockMvc.perform(get("/api/v1/items"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Active Beer")))
                .andExpect(jsonPath("$[1].name", is("Hidden Vodka")));
    }

    @Test
    @DisplayName("USER should see ONLY active products")
    @WithMockUser(username = "barman", roles = "USER")
    void getAll_AsUser_ReturnsOnlyActiveItems() throws Exception {

        createItemInDb("Active Beer", true);
        createItemInDb("Hidden Vodka", false);

        mockMvc.perform(get("/api/v1/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Active Beer")));
    }

    @Test
    @DisplayName("Receiving goods by ID - success")
    @WithMockUser(roles = "USER")
    void getById_Success() throws Exception {
        ItemEntity savedItem = createItemInDb("Mojito", true);

        mockMvc.perform(get("/api/v1/items/{id}", savedItem.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Mojito")));
    }

    @Test
    @DisplayName("Getting non-existent ID returns 404 Not Found")
    @WithMockUser(roles = "USER")
    void getById_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/items/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("ADMIN can create a product (success)")
    @WithMockUser(roles = "ADMIN")
    void create_AsAdmin_Success() throws Exception {
        ItemDTORequest request = new ItemDTORequest(
                "Whiskey", "Alcohol", 50.0, 10, true);

        mockMvc.perform(post("/api/v1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Whiskey")));
    }

    @Test
    @DisplayName("USER cannot create a product (403 Forbidden)")
    @WithMockUser(roles = "USER")
    void create_AsUser_Forbidden() throws Exception {
        ItemDTORequest request = new ItemDTORequest(
                "Whiskey", "Alcohol", 50.0, 10, true);

        mockMvc.perform(post("/api/v1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Validation: Creating a product with incorrect data (400 Bad Request)")
    @WithMockUser(roles = "ADMIN")
    void create_InvalidData_BadRequest() throws Exception {
        ItemDTORequest invalidRequest = new ItemDTORequest(
                "", "Alcohol", -50.0, 10, true);

        mockMvc.perform(post("/api/v1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("ADMIN can delete products")
    @WithMockUser(roles = "ADMIN")
    void delete_AsAdmin_Success() throws Exception {
        ItemEntity item = createItemInDb("To Delete", true);

        mockMvc.perform(delete("/api/v1/items/{id}", item.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("USER cannot delete goods")
    @WithMockUser(roles = "USER")
    void delete_AsUser_Forbidden() throws Exception {
        ItemEntity item = createItemInDb("To Delete", true);

        mockMvc.perform(delete("/api/v1/items/{id}", item.getId()))
                .andExpect(status().isForbidden());
    }
}

package com.yely.bartrack_backend.register;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yely.bartrack_backend.user.UserEntity;

@SpringBootTest
class RegisterControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @MockitoBean
    private RegisterService registerService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("should register user and return 201 when called by ADMIN")
    @WithMockUser(roles = "ADMIN")
    void registerUser_returns201_forAdmin() throws Exception {

        RegisterDTORequest request = new RegisterDTORequest("admin@test.com", "StrongPass123!");

        UserEntity mockUser = UserEntity.builder().username("admin@test.com").build();
        when(registerService.registerUser(any())).thenReturn(mockUser);

        mockMvc.perform(post("/api/v1/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.username").value("admin@test.com"));
    }

    @Test
    @DisplayName("should return 403 Forbidden when called by regular USER")
    @WithMockUser(roles = "USER")
    void registerUser_returns403_forNonAdmin() throws Exception {
        RegisterDTORequest request = new RegisterDTORequest("user@test.com", "StrongPass123!");

        mockMvc.perform(post("/api/v1/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
package com.quickcart.quickCart.store;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the {@link StoreController}
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private Long storeId; // хранения ID магазина

    @Test
    @Order(1)
    public void login() throws Exception {

        String signupRequest = """
                {
                    "username": "Test",
                    "password": "29=417|KL?_@*",
                    "email": "t@gmail.com",
                    "location": "Moscow"
                }""";

        // Регистрация пользователя
        mockMvc.perform(post("/api/v1/auth/signup")
                        .content(signupRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // Вход в систему
        String loginRequest = """
                {
                    "email": "t@gmail.com",
                    "password": "29=417|KL?_@*"
                }""";

        mockMvc.perform(post("/api/v1/auth/login")
                        .content(loginRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("sessionId"))
                .andDo(print());
    }

    @Test
    @Order(2)
    public void registerStore() throws Exception {
        mockMvc.perform(post("/api/v1/store/register")
                        .param("name", "Хоз-Товары")
                        .param("location", "Сочи")
                        .param("description", "Без обмана \uD83D\uDE01")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("t@gmail.com")))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    public void myStore() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/store/my/store")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("t@gmail.com")))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        System.out.println("RESULT -> " + responseContent);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(responseContent);

        this.storeId = jsonNode.get(0).get("id").asLong();
    }

    @Test
    public void updateStore() throws Exception {
        myStore();

        mockMvc.perform(patch("/api/v1/store/update/" + storeId)
                        .param("storeWorkingHours", "09:00 до 21:00")
                        .param("status", "ACTIVE")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("t@gmail.com")))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void storeDTO() throws Exception {
        myStore();

        mockMvc.perform(get("/api/v1/store/" + storeId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("t@gmail.com")))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void storeList() throws Exception {
        mockMvc.perform(get("/api/v1/store/all/store"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"))
                .andDo(print());
    }

    /// <h1>--------------  NEGATIVE TEST --------------------</h1>

    @Test
    public void updateStoreError() throws Exception {
        mockMvc.perform(patch("/api/v1/store/update/" + storeId)
                        .param("storeName", "1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("t@gmail.com")))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    public void registerStoreError() throws Exception {
        mockMvc.perform(post("/api/v1/store/register")
                        .param("users", """
                                {
                                    "username": "Test",
                                    "email": "t@gmail.com",
                                    "location": "Moscow"
                                }""")
                        .param("name", "X")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("t@gmail.com")))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }
}

package com.quickcart.quickCart.store;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the {@link StoreController}
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {

    }

    String signupRequest = """
            {
                "username": "Test",
                "password": "29=417|KL?_@*",
                "email": "t@gmail.com",
                "location": "Moscow"
            }""";

    @Test
    @Order(1)
    public void login() throws Exception {

        // Регистрация пользователя
        mockMvc.perform(post("/api/v1/auth/signup")
                        .content(signupRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // Попытка входа
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
                        .param("users", """
                                {
                                    "username": "Test",
                                    "email": "t@gmail.com",
                                    "location": "Moscow"
                                }""")
                        .param("name", "Хоз-Товары")
                        .param("location", "Сочи")
                        .param("description", "Без обмана \uD83D\uDE01")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("t@gmail.com")))
                .andExpect(status().isCreated())
                .andDo(print());
    }


    @Test
    @Order(3)
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

    @Test
    public void updateStore() throws Exception {
        mockMvc.perform(patch("/api/v1/store/update/1")
                        .param("storeWorkingHours", "09:00 до 21:00")
                        .param("status", "ACTIVE")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("t@gmail.com")))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void updateStoreError() throws Exception {
        mockMvc.perform(patch("/api/v1/store/update/1")
                        .param("storeName", "1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("t@gmail.com")))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    public void storeDTO() throws Exception {
        mockMvc.perform(get("/api/v1/store/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("t@gmail.com")))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void storeList() throws Exception {
        mockMvc.perform(get("/api/v1/store/all/store")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("t@gmail.com")))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void myStore() throws Exception {
        mockMvc.perform(get("/api/v1/store/my/store")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("t@gmail.com")))
                .andExpect(status().isOk())
                .andDo(print());
    }

}

package com.quickcart.quickCart.auth;

import com.quickcart.quickCart.user.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.MethodOrderer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the {@link AuthController}
 */

@ActiveProfiles("test")
@Rollback
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @AfterEach
    public void cleanRedis() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    public void registerUser() throws Exception {
        String signupRequest = """
                {
                    "username": "Test",
                    "password": "29=417|KL?_@*",
                    "email": "t@gmail.com",
                    "location": "Moscow"
                }""";

        mockMvc.perform(post("/api/v1/auth/signup")
                        .content(signupRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @Order(2)
    public void login() throws Exception {
        // Регистрация пользователя
        String signupRequest = """
                {
                    "username": "Test",
                    "password": "29=417|KL?_@*",
                    "email": "t@gmail.com",
                    "location": "Moscow"
                }""";

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
    @Order(3)
    public void logout() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("t@gmail.com")))
                .andExpect(status().isOk())
                .andDo(print());
    }

    /// <h1>--------------  NEGATIVE TEST --------------------</h1>

    @Test
    public void registerUserError() throws Exception {
        String signupRequest = """
                {
                    "username": "Test",
                    "password": "@*",
                    "email": "gmail.com",
                    "location": "----"
                }""";

        mockMvc.perform(post("/api/v1/auth/signup")
                        .content(signupRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void loginError() throws Exception {
        // Регистрация пользователя
        String signupRequest = """
                {
                    "username": "Test",
                    "password": "29=417|KL?_@*",
                    "email": "t@gmail.com",
                    "location": "Moscow"
                }""";

        mockMvc.perform(post("/api/v1/auth/signup")
                        .content(signupRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // Попытка входа
        String loginRequest = """
                {
                    "email": "",
                    "password": ""
                }""";

        mockMvc.perform(post("/api/v1/auth/login")
                        .content(loginRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void logoutError() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }
}


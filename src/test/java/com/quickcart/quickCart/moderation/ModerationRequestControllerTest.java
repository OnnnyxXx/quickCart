package com.quickcart.quickCart.moderation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickcart.quickCart.user.User;
import com.quickcart.quickCart.user.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Rollback
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ModerationRequestControllerTest {

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

    private User savedUser;
    private Long storeId;

    @BeforeEach
    public void setup() {

        if (userRepository.findByEmail("moder@gmail.com").isEmpty()) {
            User moderUser = new User();
            moderUser.setUsername("MODER");
            moderUser.setEmail("moder@gmail.com");
            moderUser.setPassword("lol-lol");
            moderUser.setRole(User.Role.MODER);
            userRepository.save(moderUser);
        }
        savedUser = userRepository.findByEmail("moder@gmail.com").orElseThrow();
    }

    @Test
    @Order(1)
    public void login() throws Exception {

        // Регистрация пользователя
        String signupRequest = """
                {
                    "username": "TestUser",
                    "password": "testPassword123!",
                    "email": "testuser@gmail.com",
                    "location": "Moscow"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/signup")
                        .content(signupRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());

        // Вход
        String loginRequest = """
                {
                    "email": "testuser@gmail.com",
                    "password": "testPassword123!"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .content(loginRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        // Регистрация магазина
        mockMvc.perform(post("/api/v1/store/register")
                        .param("name", "quickCart")
                        .param("location", "Сочи")
                        .param("description", "Без обмана \uD83D\uDE01")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("testuser@gmail.com"))
                )
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @Order(2)
    public void noStoreList() throws Exception {
        mockMvc.perform(get("/api/v1/store/all/store")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"))
                .andDo(print());
    }

    @Test
    @Order(3)
    public void storeAll() throws Exception {
        mockMvc.perform(get("/api/v1/moderation/admin/store")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user(savedUser.getEmail())
                                .authorities(new SimpleGrantedAuthority("MODER"))))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Order(4)
    public void getStoresForModer() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/moderation/manage/store")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user(savedUser.getEmail())
                                .authorities(new SimpleGrantedAuthority("MODER"))))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        System.out.println("RESULT -> " + responseContent);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(responseContent);

        this.storeId = jsonNode.get(0).get("storeId").asLong();
    }

    @Test
    @Order(5)
    public void changeOfStatus() throws Exception {
        String moderationDTO = """
                {
                    "status": "ACTIVE"
                }""";

        mockMvc.perform(patch("/api/v1/moderation/manage/store/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user(savedUser.getEmail())
                                .authorities(new SimpleGrantedAuthority("MODER")))
                        .content(moderationDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Order(6)
    public void storeList() throws Exception {
        mockMvc.perform(get("/api/v1/store/all/store")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    /**
     * <h1>Clearing Redis Cache<h1/>
     * If you don't do this, then when you run the test again,
     * the noStoreList() endpoint will access the cache and cause an error in the logic.
     */

    @Test
    @Order(7)
    public void changeOfStatusBlock() throws Exception {
        String moderationDTO = """
                {
                    "status": "BLOCKED"
                }""";

        mockMvc.perform(patch("/api/v1/moderation/manage/store/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user(savedUser.getEmail())
                                .authorities(new SimpleGrantedAuthority("MODER")))
                        .content(moderationDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Order(8)
    public void storeListClear() throws Exception {
        mockMvc.perform(get("/api/v1/store/all/store")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"))
                .andDo(print());
    }

    /// <h1>--------------  NEGATIVE TEST --------------------</h1>

    @Test
    public void loginError() throws Exception {

        // Регистрация пользователя
        String signupRequest = """
                {
                    "username": "T",
                    "password": "t",
                    "email": "tesifrest"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/signup")
                        .content(signupRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());

        // Вход
        String loginRequest = """
                {
                    "email": "tesifrest@gmail.com",
                    "password": "testPassword123!"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .content(loginRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());

        // Регистрация магазина
        mockMvc.perform(post("/api/v1/store/register")
                        .param("name", "N")
                        .param("description", "Без обмана \uD83D\uDE01")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("tesifrest@gmail.com"))
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void changeOfStatusError() throws Exception {
        String moderationDTO = """
                {
                    "status": "BLOCKED"
                }""";

        mockMvc.perform(patch("/api/v1/moderation/manage/store/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("testing@gmail.com"))
                        .content(moderationDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }
}
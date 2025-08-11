package com.quickcart.quickCart;

import com.quickcart.quickCart.user.User;
import com.quickcart.quickCart.user.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class QuickCartApplicationTests {

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

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();

        savedUser = new User();
        savedUser.setUsername("Test");
        savedUser.setEmail("test@gmail.com");
        savedUser.setPassword("7474712:L");
        savedUser.setLocation("СПб");
        userRepository.save(savedUser);
    }

    @Test
    public void profileUser() throws Exception {
        mockMvc.perform(get("/api/v1/users/profile/user")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@gmail.com")))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void getUserByEmail() throws Exception {
        mockMvc.perform(get("/api/v1/users/email/" + savedUser.getEmail()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void getUserById() throws Exception {
        mockMvc.perform(get("/api/v1/users/" + savedUser.getId()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void patch() throws Exception {
        String patchNode = """
                {
                    "location": "Сочи",
                    "password": "|@)$)___+_-@*&&&$@!f"
                }""";

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/users/update/" + savedUser.getId())
                        .with(csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@gmail.com").password("7474712:L"))
                        .content(patchNode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    /// <h1>--------------  NEGATIVE TEST --------------------</h1>

    @Test
    public void profileUserError() throws Exception {
        mockMvc.perform(get("/api/v1/users/profile/user")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    public void patchError() throws Exception {
        String patchNode = """
                {
                    "username": "L",
                    "location": "Сочи"
                }""";

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/users/update/2323")
                        .with(csrf())
                        .content(patchNode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    public void getUserByIdError() throws Exception {
        mockMvc.perform(get("/api/v1/users/21900000"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}

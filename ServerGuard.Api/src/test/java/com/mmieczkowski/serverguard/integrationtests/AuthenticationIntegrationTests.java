package com.mmieczkowski.serverguard.integrationtests;

import com.mmieczkowski.serverguard.user.request.LoginRequest;
import com.mmieczkowski.serverguard.user.request.RegisterRequest;
import com.mmieczkowski.serverguard.user.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthenticationIntegrationTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private final String testUserEmail = "user@test.com";
    private final String testUserPassword = "password";

    @BeforeAll
    public void setup() {
        userService.register(new RegisterRequest(testUserEmail, testUserPassword));
    }

    @Test
    void whenNoAuthorizationHeader_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/resourceGroups?pageNumber=0&pageSize=10"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthorizationHeader_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/resourceGroups?pageNumber=0&pageSize=10")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenValidAuthorizationHeader_thenOk() throws Exception {
        var token = userService.login(new LoginRequest(testUserEmail, testUserPassword)).token();
        mockMvc.perform(get("/api/resourceGroups?pageNumber=0&pageSize=10")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}

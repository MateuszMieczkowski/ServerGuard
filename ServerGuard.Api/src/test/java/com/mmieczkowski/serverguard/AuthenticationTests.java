package com.mmieczkowski.serverguard;

import org.junit.jupiter.api.Test;
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
public class AuthenticationTests {

    @Autowired
    MockMvc mockMvc;

    @Test
    void whenNoAuthorizationHeader_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/resource-groups"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidAuthorizationHeader_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/resource-groups")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }
}

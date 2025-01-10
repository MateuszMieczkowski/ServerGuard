package com.mmieczkowski.serverguard.integrationtests;

import com.mmieczkowski.serverguard.resourcegroup.ResourceGroupRepository;
import com.mmieczkowski.serverguard.resourcegroup.model.ResourceGroup;
import com.mmieczkowski.serverguard.resourcegroup.model.ResourceGroupUserRole;
import com.mmieczkowski.serverguard.user.repository.UserRepository;
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
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ResourceGroupAccessIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResourceGroupRepository resourceGroupRepository;

    private final String testUserEmail = "email@test.com";
    private final String testUserPassword = "password";


    @BeforeAll
    void setup() {
        userService.register(new RegisterRequest(testUserEmail, testUserPassword));
    }

    @Test
    @Transactional
    void whenAccessToResourceGroup_thenOk() throws Exception {
        var user = userRepository.findByEmail("email@test.com").orElseThrow();
        var resourceGroupWithAccess = new ResourceGroup("Resource Group 1");
        resourceGroupWithAccess.addUser(user, ResourceGroupUserRole.USER);
        resourceGroupRepository.save(resourceGroupWithAccess);
        var userJwt = userService.login(new LoginRequest(testUserEmail, testUserPassword)).token();

        mockMvc.perform(get(String.format("/api/resourceGroups/%s", resourceGroupWithAccess.getId()))
                        .header("Authorization", "Bearer " + userJwt))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void whenNoAccessToResourceGroup_thenForbidden() throws Exception {
        var resourceGroupWithNoAccess = new ResourceGroup("Resource Group 2");
        resourceGroupRepository.save(resourceGroupWithNoAccess);

        var userJwt = userService.login(new LoginRequest(testUserEmail, testUserPassword)).token();

        mockMvc.perform(get(String.format("/api/resourceGroups/%s", resourceGroupWithNoAccess.getId()))
                        .header("Authorization", "Bearer " + userJwt))
                .andExpect(status().isForbidden());
    }
}

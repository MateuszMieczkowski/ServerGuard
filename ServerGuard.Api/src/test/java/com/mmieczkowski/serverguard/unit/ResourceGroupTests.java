package com.mmieczkowski.serverguard.unit;


import com.mmieczkowski.serverguard.resourcegroup.exception.ResourceGroupInvitationNotFoundException;
import com.mmieczkowski.serverguard.resourcegroup.model.ResourceGroup;
import com.mmieczkowski.serverguard.resourcegroup.model.ResourceGroupInvitation;
import com.mmieczkowski.serverguard.resourcegroup.model.ResourceGroupUserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;

import java.time.Clock;
import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class ResourceGroupTests {

    @Mock
    private Clock clock;

    @Test
    void whenInitializingWithCtor_thenNameIsSet() {
        String name = "Test Resource Group";

        ResourceGroup resourceGroup = new ResourceGroup(name);

        assertThat(resourceGroup.getName()).isEqualTo(name);
    }

    @Test
    void whenInitializingWithCtor_andPassingNullName_thenThrows() {
        assertThatThrownBy(() -> new ResourceGroup(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    void whenInitializingWithInvalidName_thenThrows(String invalidName) {
        assertThatThrownBy(() -> new ResourceGroup(invalidName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void whenSetName_thenNameIsSet() {
        String expectedName = "Test Resource Group";

        ResourceGroup resourceGroup = new ResourceGroup("Initial Name");
        resourceGroup.setName(expectedName);
        assertThat(resourceGroup.getName()).isEqualTo(expectedName);
    }

    @Test
    void whenSetName_andPassingNullName_thenThrows() {
        ResourceGroup resourceGroup = new ResourceGroup("Initial Name");
        assertThatThrownBy(() -> resourceGroup.setName(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    void whenSetName_andInvalidName_thenThrows(String invalidName) {
        ResourceGroup resourceGroup = new ResourceGroup("Initial Name");
        assertThatThrownBy(() -> resourceGroup.setName(invalidName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void whenCreatingInvitation_thenInvitationHasCorrectEmail() {
        Instant instant = Instant.now();
        Mockito.when(clock.instant()).thenReturn(instant);
        ResourceGroup resourceGroup = new ResourceGroup("Test Resource Group");
        var email = "test@email.com";

        ResourceGroupInvitation invitation = resourceGroup.createInvitation(email, ResourceGroupUserRole.USER, clock);

        assertThat(invitation.getEmail()).isEqualTo(email);
    }

    @Test
    void whenCreatingInvitation_thenInvitationTokenSet() {
        Instant instant = Instant.now();
        Mockito.when(clock.instant()).thenReturn(instant);
        ResourceGroup resourceGroup = new ResourceGroup("Test Resource Group");
        var email = "test@email.com";

        ResourceGroupInvitation invitation = resourceGroup.createInvitation(email, ResourceGroupUserRole.USER, clock);

        assertThat(invitation.getToken()).isNotBlank();
    }

    @Test
    void whenCreatingAdminInvitation_thenInvitationHasAdminRole() {
        Instant instant = Instant.now();
        Mockito.when(clock.instant()).thenReturn(instant);
        ResourceGroup resourceGroup = new ResourceGroup("Test Resource Group");
        var email = "test@email.com";

        ResourceGroupInvitation invitation = resourceGroup.createInvitation(email, ResourceGroupUserRole.ADMIN, clock);

        assertThat(invitation.getRole()).isEqualTo(ResourceGroupUserRole.ADMIN);
    }

    @Test
    void whenCreatingUserInvitation_thenInvitationHasUserRole() {
        Instant instant = Instant.now();
        Mockito.when(clock.instant()).thenReturn(instant);
        ResourceGroup resourceGroup = new ResourceGroup("Test Resource Group");
        var email = "test@email.com";

        ResourceGroupInvitation invitation = resourceGroup.createInvitation(email, ResourceGroupUserRole.USER, clock);

        assertThat(invitation.getRole()).isEqualTo(ResourceGroupUserRole.USER);
    }

    @Test
    void whenCreatingInvitation_thenInvitationHasCorrectLink() {
        Instant instant = Instant.now();
        Mockito.when(clock.instant()).thenReturn(instant);
        ResourceGroup resourceGroup = new ResourceGroup("Test Resource Group");
        var email = "test@email.com";

        ResourceGroupInvitation invitation = resourceGroup.createInvitation(email, ResourceGroupUserRole.USER, clock);

        assertThat(invitation.constructLink("http://localhost:8080"))
                .isEqualTo("http://localhost:8080/accept-invitation?token=" + invitation.getToken());
    }

    @Test
    void whenAcceptingInvitation_withInvalidToken_thenThrows() {
        ResourceGroup resourceGroup = new ResourceGroup("Test Resource Group");
        var userDetails = User.builder().username("test@test.com").password("test").build();
        var user = new com.mmieczkowski.serverguard.user.model.User(userDetails);

        assertThatThrownBy(() -> resourceGroup.acceptInvitation("invalid-token", user, clock))
                .isInstanceOf(ResourceGroupInvitationNotFoundException.class);
    }

    @Test
    void whenAcceptingInvitation_withValidToken_thenUserAdded() {
        Instant instant = Instant.now();
        Mockito.when(clock.instant()).thenReturn(instant);
        ResourceGroup resourceGroup = new ResourceGroup("Test Resource Group");
        var userDetails = User.builder().username("test@test.com").password("test").build();
        var user = new com.mmieczkowski.serverguard.user.model.User(userDetails);
        var invitation = resourceGroup.createInvitation("test@test.com", ResourceGroupUserRole.USER, clock);

        resourceGroup.acceptInvitation(invitation.getToken(), user, clock);
        assertThat(resourceGroup.hasPermission(user, ResourceGroupUserRole.USER)).isTrue();
    }
}

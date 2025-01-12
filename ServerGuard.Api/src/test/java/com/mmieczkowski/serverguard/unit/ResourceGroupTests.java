package com.mmieczkowski.serverguard.unit;


import com.mmieczkowski.serverguard.resourcegroup.exception.ResourceGroupInvitationNotFoundException;
import com.mmieczkowski.serverguard.resourcegroup.exception.UserAlreadyInResourceGroupException;
import com.mmieczkowski.serverguard.resourcegroup.model.ResourceGroup;
import com.mmieczkowski.serverguard.resourcegroup.model.ResourceGroupInvitation;
import com.mmieczkowski.serverguard.resourcegroup.model.ResourceGroupUserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;

import java.time.Clock;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class ResourceGroupTests {

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
        ResourceGroup resourceGroup = new ResourceGroup("Test Resource Group");
        var email = "test@email.com";

        ResourceGroupInvitation invitation = resourceGroup.createInvitation(email, ResourceGroupUserRole.USER, Clock.systemUTC());

        assertThat(invitation.getEmail()).isEqualTo(email);
    }

    @Test
    void whenCreatingInvitation_thenInvitationTokenSet() {
        ResourceGroup resourceGroup = new ResourceGroup("Test Resource Group");
        var email = "test@email.com";

        ResourceGroupInvitation invitation = resourceGroup.createInvitation(email, ResourceGroupUserRole.USER, Clock.systemUTC());

        assertThat(invitation.getToken()).isNotBlank();
    }

    @Test
    void whenCreatingAdminInvitation_thenInvitationHasAdminRole() {
        ResourceGroup resourceGroup = new ResourceGroup("Test Resource Group");
        var email = "test@email.com";

        ResourceGroupInvitation invitation = resourceGroup.createInvitation(email, ResourceGroupUserRole.ADMIN, Clock.systemUTC());

        assertThat(invitation.getRole()).isEqualTo(ResourceGroupUserRole.ADMIN);
    }

    @Test
    void whenCreatingUserInvitation_thenInvitationHasUserRole() {
        ResourceGroup resourceGroup = new ResourceGroup("Test Resource Group");
        var email = "test@email.com";

        ResourceGroupInvitation invitation = resourceGroup.createInvitation(email, ResourceGroupUserRole.USER, Clock.systemUTC());

        assertThat(invitation.getRole()).isEqualTo(ResourceGroupUserRole.USER);
    }

    @Test
    void whenCreatingInvitation_thenInvitationHasCorrectLink() {
        ResourceGroup resourceGroup = new ResourceGroup("Test Resource Group");
        var email = "test@email.com";

        ResourceGroupInvitation invitation = resourceGroup.createInvitation(email, ResourceGroupUserRole.USER, Clock.systemUTC());

        assertThat(invitation.constructLink("http://localhost:8080"))
                .isEqualTo("http://localhost:8080/accept-invitation?token=" + invitation.getToken());
    }

    @Test
    void whenAcceptingInvitation_withInvalidToken_thenThrows() {
        ResourceGroup resourceGroup = new ResourceGroup("Test Resource Group");
        var userDetails = User.builder().username("test@test.com").password("test").build();
        var user = new com.mmieczkowski.serverguard.user.model.User(userDetails);

        assertThatThrownBy(() -> resourceGroup.acceptInvitation("invalid-token", user, Clock.systemUTC()))
                .isInstanceOf(ResourceGroupInvitationNotFoundException.class);
    }

    @Test
    void whenAcceptingInvitation_withValidToken_thenUserAdded() {
        ResourceGroup resourceGroup = new ResourceGroup("Test Resource Group");
        var userDetails = User.builder().username("test@test.com").password("test").build();
        var user = new com.mmieczkowski.serverguard.user.model.User(userDetails);
        var invitation = resourceGroup.createInvitation("test@test.com", ResourceGroupUserRole.USER, Clock.systemUTC());

        resourceGroup.acceptInvitation(invitation.getToken(), user, Clock.systemUTC());
        assertThat(resourceGroup.hasUser(user)).isTrue();
    }

    @Test
    void whenAcceptingInvitation_andUserAlreadyInGroup_thenThrows() {
        ResourceGroup resourceGroup = new ResourceGroup("Test Resource Group");
        var userDetails = User.builder().username("test@test.com").password("test").build();
        var user = new com.mmieczkowski.serverguard.user.model.User(userDetails);
        resourceGroup.addUser(user, ResourceGroupUserRole.USER);
        var invitation = resourceGroup.createInvitation("test@test.com", ResourceGroupUserRole.USER, Clock.systemUTC());

        assertThatThrownBy(() -> resourceGroup.acceptInvitation(invitation.getToken(), user, Clock.systemUTC()))
                .isInstanceOf(UserAlreadyInResourceGroupException.class);
    }

    @Test
    void whenGettingInvitation_withInvalidToken_thenThrows() {
        ResourceGroup resourceGroup = new ResourceGroup("Test Resource Group");

        assertThatThrownBy(() -> resourceGroup.getInvitation("invalid-token"))
                .isInstanceOf(ResourceGroupInvitationNotFoundException.class);
    }

    @Test
    void whenGettingInvitation_withValidToken_thenInvitationReturned() {
        ResourceGroup resourceGroup = new ResourceGroup("Test Resource Group");
        var invitation = resourceGroup.createInvitation("test@test.com", ResourceGroupUserRole.USER, Clock.systemUTC());

        assertThat(resourceGroup.getInvitation(invitation.getToken())).isEqualTo(invitation);
    }


    @Test
    void whenAddingUser_thenUserAdded() {
        ResourceGroup resourceGroup = new ResourceGroup("Test Resource Group");
        var userDetails = User.builder().username("test@test.com").password("test").build();
        var user = new com.mmieczkowski.serverguard.user.model.User(userDetails);
        resourceGroup.addUser(user, ResourceGroupUserRole.USER);
        assertThat(resourceGroup.hasUser(user)).isTrue();
    }

    @Test
    void whenAddingUser_andUserAlreadyInGroup_thenThrows() {
        ResourceGroup resourceGroup = new ResourceGroup("Test Resource Group");
        var userDetails = User.builder().username("test@test.com").password("test").build();
        var user = new com.mmieczkowski.serverguard.user.model.User(userDetails);
        resourceGroup.addUser(user, ResourceGroupUserRole.USER);

        assertThatThrownBy(() -> resourceGroup.addUser(user, ResourceGroupUserRole.USER))
                .isInstanceOf(UserAlreadyInResourceGroupException.class);
    }

    @Test
    void whenRemovingUser_thenUserRemoved() {
        ResourceGroup resourceGroup = new ResourceGroup("Test Resource Group");
        var userDetails = User.builder().username("test@test.com").password("test").build();
        var user = new com.mmieczkowski.serverguard.user.model.User(userDetails);
        resourceGroup.addUser(user, ResourceGroupUserRole.USER);

        resourceGroup.removeUser(user);

        assertThat(resourceGroup.hasUser(user)).isFalse();
    }

    @Test
    void whenDeletingResourceGroup_thenPermissionsAreCleared() {
        ResourceGroup resourceGroup = new ResourceGroup("Test Resource Group");
        var userDetails = User.builder().username("test@test.com").password("test").build();
        var user = new com.mmieczkowski.serverguard.user.model.User(userDetails);
        resourceGroup.addUser(user, ResourceGroupUserRole.USER);
        resourceGroup.delete();

        assertThat(resourceGroup.hasUser(user)).isFalse();
    }

}

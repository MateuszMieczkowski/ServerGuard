package com.mmieczkowski.serverguard.resourcegroup;

import com.mmieczkowski.serverguard.annotation.ResourceGroupAccess;
import com.mmieczkowski.serverguard.config.properties.WebProperties;
import com.mmieczkowski.serverguard.email.EmailService;
import com.mmieczkowski.serverguard.email.definitions.ResourceGroupInvitationEmail;
import com.mmieczkowski.serverguard.resourcegroup.exception.CannotDeleteYourselfException;
import com.mmieczkowski.serverguard.resourcegroup.model.ResourceGroup;
import com.mmieczkowski.serverguard.resourcegroup.model.ResourceGroupInvitation;
import com.mmieczkowski.serverguard.resourcegroup.model.ResourceGroupUserRole;
import com.mmieczkowski.serverguard.resourcegroup.request.CreateResourceGroupInvitationRequest;
import com.mmieczkowski.serverguard.resourcegroup.request.CreateResourceGroupRequest;
import com.mmieczkowski.serverguard.resourcegroup.response.CreateResourceGroupResponse;
import com.mmieczkowski.serverguard.resourcegroup.exception.ResourceGroupNotFoundException;
import com.mmieczkowski.serverguard.resourcegroup.response.GetResourceGroupInvitationResponse;
import com.mmieczkowski.serverguard.resourcegroup.response.GetResourceGroupResponse;
import com.mmieczkowski.serverguard.resourcegroup.request.GetResourceGroupPaginatedRequest;
import com.mmieczkowski.serverguard.resourcegroup.response.GetResourceGroupPaginatedResponse;
import com.mmieczkowski.serverguard.service.CurrentUserService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Clock;
import java.util.UUID;

@Service
public class ResourceGroupService {
    private final ResourceGroupRepository resourceGroupRepository;
    private final CurrentUserService currentUserService;
    private final Clock clock;
    private final EmailService emailService;
    private final WebProperties webProperties;

    public ResourceGroupService(ResourceGroupRepository resourceGroupRepository,
                                CurrentUserService currentUserService,
                                Clock clock,
                                EmailService emailService,
                                WebProperties webProperties) {
        this.resourceGroupRepository = resourceGroupRepository;
        this.currentUserService = currentUserService;
        this.clock = clock;
        this.emailService = emailService;
        this.webProperties = webProperties;
    }

    @Transactional
    public CreateResourceGroupResponse createResourceGroup(CreateResourceGroupRequest request) {
        var user = currentUserService.getLoggedInUser()
                .orElseThrow();
        ResourceGroup resourceGroup = new ResourceGroup(request.name());
        resourceGroup.addUser(user, ResourceGroupUserRole.ADMIN);
        resourceGroupRepository.save(resourceGroup);
        return new CreateResourceGroupResponse(resourceGroup.getId(), resourceGroup.getName());
    }

    @ResourceGroupAccess(value = "id", roles = {ResourceGroupUserRole.USER, ResourceGroupUserRole.ADMIN})
    public GetResourceGroupResponse getResourceGroup(UUID id) {
        ResourceGroup resourceGroup = resourceGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceGroupNotFoundException(id));
        return new GetResourceGroupResponse(resourceGroup.getId(), resourceGroup.getName());
    }

    public GetResourceGroupPaginatedResponse getResourceGroups(GetResourceGroupPaginatedRequest request) {
        var user = currentUserService.getLoggedInUser()
                .orElseThrow();
        var pageable = PageRequest.of(request.pageNumber(), request.pageSize())
                .withSort(Sort.by("rg.name"));
        Page<ResourceGroup> page = resourceGroupRepository.findByUserId(user.getId(), pageable);
        var items = page.get()
                .map(GetResourceGroupPaginatedResponse.ResourceGroup::new)
                .toList();
        return new GetResourceGroupPaginatedResponse(items, page.getNumber(), page.getSize(), page.getTotalPages());
    }

    @ResourceGroupAccess(value = "id", roles = {ResourceGroupUserRole.ADMIN})
    public void deleteResourceGroup(UUID id) {
        resourceGroupRepository.deleteById(id);
    }

    @ResourceGroupAccess(value = "id", roles = {ResourceGroupUserRole.ADMIN})
    public void updateResourceGroup(UUID id, CreateResourceGroupRequest request) {
        ResourceGroup resourceGroup = resourceGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceGroupNotFoundException(id));
        resourceGroup.setName(request.name());
        resourceGroupRepository.save(resourceGroup);
    }

    @Transactional
    @ResourceGroupAccess(value = "id", roles = {ResourceGroupUserRole.ADMIN})
    public void createResourceGroupInvitation(UUID id, CreateResourceGroupInvitationRequest request) {
        ResourceGroup resourceGroup = resourceGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceGroupNotFoundException(id));
        ResourceGroupInvitation invitation = resourceGroup.createInvitation(request.email(), request.role(), clock);
        resourceGroupRepository.save(resourceGroup);
        var invitationEmail = new ResourceGroupInvitationEmail(invitation.getEmail(),
                invitation.constructLink(webProperties.url() + "/resourceGroups/" + id),
                resourceGroup.getName(),
                invitation.getRole());
        try {
            emailService.sendEmail(invitationEmail);
        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void acceptResourceGroupInvitation(UUID id, String token) {
        ResourceGroup resourceGroup = resourceGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceGroupNotFoundException(id));
        var user = currentUserService.getLoggedInUser()
                .orElseThrow();
        resourceGroup.acceptInvitation(token, user, clock);
        resourceGroupRepository.save(resourceGroup);
    }

    @ResourceGroupAccess(value = "id", roles = {ResourceGroupUserRole.ADMIN})
    public void removeUserFromResourceGroup(UUID id, UUID userId) {
        if(currentUserService.getLoggedInUser().map(user -> user.getId().equals(userId)).orElse(false)){
            throw new CannotDeleteYourselfException();
        }
        ResourceGroup resourceGroup = resourceGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceGroupNotFoundException(id));
        resourceGroup.removeUser(userId);
        resourceGroupRepository.save(resourceGroup);
    }

    public GetResourceGroupInvitationResponse getResourceGroupInvitation(UUID id, String token) {
        ResourceGroup resourceGroup  = resourceGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceGroupNotFoundException(id));
        ResourceGroupInvitation invitation = resourceGroup.getInvitation(token);
        return new GetResourceGroupInvitationResponse(invitation.getEmail(), invitation.getRole(), resourceGroup.getName());
    }
}

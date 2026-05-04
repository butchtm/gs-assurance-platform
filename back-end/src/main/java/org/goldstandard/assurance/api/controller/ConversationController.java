package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.VVB_ROLE;
import static org.springframework.data.domain.Sort.Direction.DESC;

import jakarta.annotation.security.RolesAllowed;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.ConversationDTO;
import org.goldstandard.assurance.api.dto.ConversationMessageDTO;
import org.goldstandard.assurance.api.dto.MessageCreateDTO;
import org.goldstandard.assurance.api.dto.UnreadMessagesDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.entity.ConversationStatus;
import org.goldstandard.assurance.api.service.conversation.ConversationFacade;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ConversationController {

  private final ConversationFacade facade;

  @RolesAllowed({ARM_TEAM_ROLE, ADMIN_ROLE})
  @PostMapping("/projects/{projectId}/requests/{requestId}/conversations")
  public ResponseEntity<ConversationDTO> openConversation(
      @PathVariable(value = "projectId") Integer projectId,
      @PathVariable(value = "requestId") Integer requestId) {
    var conversation = facade.openConversation(requestId);
    return ResponseEntity.ok(conversation);
  }

  @RolesAllowed({ARM_TEAM_ROLE, ADMIN_ROLE})
  @PutMapping("/projects/{projectId}/requests/{requestId}/conversations/{conversationId}/status")
  public ResponseEntity<ConversationDTO> updateConversationStatus(
      @PathVariable(value = "conversationId") Integer conversationId,
      @RequestBody ConversationStatus status) {
    var conversation = facade.updateConversationStatus(conversationId, status);
    return ResponseEntity.ok(conversation);
  }

  @RolesAllowed({ARM_TEAM_ROLE, ADMIN_ROLE, PROJECT_DEVELOPER_ROLE, VVB_ROLE})
  @PostMapping("/projects/{projectId}/requests/{requestId}/conversations/{conversationId}/messages")
  public ResponseEntity<ConversationMessageDTO> createConversationMessage(
      @PathVariable(value = "requestId") Integer requestId,
      @PathVariable(value = "conversationId") Integer conversationId,
      @RequestBody MessageCreateDTO message) {
    var createdMessage = facade.saveMessage(requestId, conversationId, message);
    return new ResponseEntity<>(createdMessage, HttpStatus.CREATED);
  }

  @RolesAllowed({ARM_TEAM_ROLE, ADMIN_ROLE, PROJECT_DEVELOPER_ROLE, VVB_ROLE})
  @GetMapping(
      "/projects/{projectId}/requests/{requestId}/conversations/{conversationId}/messages/pages")
  public ResponseEntity<PagedResource<ConversationMessageDTO>> getMessages(
      @PathVariable(value = "conversationId") Integer conversationId,
      @SortDefault(sort = "sentAt", direction = DESC) Pageable pageable) {
    return ResponseEntity.ok(facade.getMessages(conversationId, pageable));
  }

  @RolesAllowed({ARM_TEAM_ROLE, ADMIN_ROLE, PROJECT_DEVELOPER_ROLE, VVB_ROLE})
  @PostMapping(
      "/projects/{projectId}/requests/{requestId}/conversations/{conversationId}/messages/reads")
  public ResponseEntity<Void> markConversationMessagesAsRead(
      @PathVariable(value = "conversationId") Integer conversationId) {
    facade.markReadConversation(conversationId);
    return ResponseEntity.noContent().build();
  }

  @RolesAllowed({ARM_TEAM_ROLE, ADMIN_ROLE, PROJECT_DEVELOPER_ROLE, VVB_ROLE})
  @GetMapping("/conversations/unread-messages")
  public ResponseEntity<Collection<UnreadMessagesDTO>> getUnreadMessages() {
    return ResponseEntity.ok(facade.getUnreadMessages());
  }
}

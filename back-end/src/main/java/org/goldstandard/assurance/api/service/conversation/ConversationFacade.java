package org.goldstandard.assurance.api.service.conversation;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.converter.ConversationConverter;
import org.goldstandard.assurance.api.dto.ConversationDTO;
import org.goldstandard.assurance.api.dto.ConversationMessageDTO;
import org.goldstandard.assurance.api.dto.MessageCreateDTO;
import org.goldstandard.assurance.api.dto.UnreadMessagesDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.entity.ConversationMessage;
import org.goldstandard.assurance.api.entity.ConversationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConversationFacade {

  private final ConversationService conversationService;
  private final ConversationMessageService messageService;
  private final ConversationConverter mapper;
  private final UnreadMessageFinder unreadMessageFinder;

  public ConversationDTO openConversation(Integer requestId) {
    var conversationId = conversationService.openConversation(requestId);
    return getConversationDto(conversationId);
  }

  public ConversationDTO updateConversationStatus(
      Integer conversationId, ConversationStatus conversationStatus) {
    conversationService.updateConversationStatus(conversationId, conversationStatus);
    return getConversationDto(conversationId);
  }

  public ConversationMessageDTO saveMessage(
      Integer requestId, Integer conversationId, MessageCreateDTO message) {
    var messageId = messageService.saveMessage(requestId, conversationId, message);
    return mapper.toDto(getMessage(messageId));
  }

  public PagedResource<ConversationMessageDTO> getMessages(
      Integer conversationId, Pageable pageable) {
    var messages = messageService.getMessages(conversationId, pageable);
    return mapper.toPagedDtos(messages);
  }

  public void markReadConversation(Integer conversationId) {
    messageService.markReadConversation(conversationId);
  }

  public ConversationMessage getMessage(Integer messageId) {
    return messageService.getMessage(messageId);
  }

  private ConversationDTO getConversationDto(Integer id) {
    return mapper.toDto(conversationService.getConversation(id));
  }

  public Collection<UnreadMessagesDTO> getUnreadMessages() {
    var unreadMessages = unreadMessageFinder.getUnreadMessagesForAuthenticatedUser();
    return mapper.toUnreadMessagesDtos(unreadMessages);
  }
}

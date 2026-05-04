package org.goldstandard.assurance.api.converter;

import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.ConversationDTO;
import org.goldstandard.assurance.api.dto.ConversationMessageDTO;
import org.goldstandard.assurance.api.dto.RequestConversationDTO;
import org.goldstandard.assurance.api.dto.UnreadMessagesDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Conversation;
import org.goldstandard.assurance.api.entity.ConversationMessage;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;
import org.goldstandard.assurance.api.entity.view.UnreadMessagesView;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ConversationConverter {

  public ConversationDTO toDto(Conversation conversation) {
    return ConversationDTO.builder()
        .id(conversation.getId())
        .requestId(conversation.getRequestId())
        .status(conversation.getStatus())
        .build();
  }

  public RequestConversationDTO toRequestDto(Conversation conversation, Integer unreadMessages) {
    return RequestConversationDTO.builder()
        .id(conversation.getId())
        .requestId(conversation.getRequestId())
        .status(conversation.getStatus())
        .unreadMessages(unreadMessages)
        .build();
  }

  public PagedResource<ConversationMessageDTO> toPagedDtos(Page<ConversationMessage> messages) {
    return new PagedResource<>(messages.map(this::toDto));
  }

  public ConversationMessageDTO toDto(ConversationMessage message) {
    var sentBy = message.getSentByUser();
    return ConversationMessageDTO.builder()
        .id(message.getId())
        .conversationId(message.getConversationId())
        .content(message.getContent())
        .senderUsername(sentBy.getUserName())
        .senderAccountType(toAccountTypeDescription(sentBy))
        .sentTimestamp(message.getSentAt())
        .readByUsers(message.getReadByUsernames())
        .readByAccountTypes(toAccountTypeDescriptions(message.getReadByAccounts()))
        .build();
  }

  private String toAccountTypeDescription(User user) {
    return Optional.ofNullable(user.getAccount())
        .map(Account::getAccountType)
        .map(ReferenceDataType::getDescription)
        .orElse(null);
  }

  private Collection<String> toAccountTypeDescriptions(Collection<Account> accounts) {
    return accounts.stream()
        .map(Account::getAccountType)
        .map(ReferenceDataType::getDescription)
        .toList();
  }

  public Collection<UnreadMessagesDTO> toUnreadMessagesDtos(
      Collection<UnreadMessagesView> unreadMessages) {
    return unreadMessages.stream()
        .filter(UnreadMessagesView::hasAny)
        .map(this::toUnreadMessagesDto)
        .toList();
  }

  public UnreadMessagesDTO toUnreadMessagesDto(UnreadMessagesView unreadMessages) {
    return UnreadMessagesDTO.builder()
        .requestId(unreadMessages.getRequestId())
        .conversationId(unreadMessages.getConversationId())
        .unreadCount(unreadMessages.getUnread())
        .build();
  }
}

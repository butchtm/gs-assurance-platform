package org.goldstandard.assurance.api.service.conversation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.goldstandard.assurance.api.entity.ConversationMessage;
import org.goldstandard.assurance.api.entity.view.UnreadMessagesView;
import org.goldstandard.assurance.api.projection.ConversationMessageId;
import org.goldstandard.assurance.api.projection.ConversationMessageReceiptMessageId;
import org.goldstandard.assurance.api.repository.ConversationMessageReceiptRepository;
import org.goldstandard.assurance.api.repository.ConversationMessageRepository;
import org.goldstandard.assurance.api.repository.UnreadMessagesViewRepository;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UnreadMessageFinder {

  private final AuthenticatedUserSupplier userSupplier;
  private final UnreadMessagesSpecFactory specFactory;
  private final UnreadMessagesViewRepository unreadRepository;
  private final ConversationMessageRepository messageRepository;
  private final ConversationMessageReceiptRepository receiptRepository;
  private final UnreadByAllAccountUsersFilter unreadByAllAccountUsersFilter;

  public int getUnreadMessageCountForAuthenticatedUserAndRequest(Integer requestId) {
    var userId = userSupplier.forceGetAuthenticatedUserPlatformId();
    return unreadRepository
        .findByUserIdAndRequestId(userId, requestId)
        .map(UnreadMessagesView::getUnread)
        .orElse(0);
  }

  public Map<Integer, Integer> getUnreadMessageCountForAuthenticatedUserAndRequests(
      Collection<Integer> requestIds) {
    var userId = userSupplier.forceGetAuthenticatedUserPlatformId();
    var spec = specFactory.build(userId, requestIds);
    return unreadRepository.findAll(spec).stream()
        .collect(
            Collectors.toMap(
                UnreadMessagesView::getRequestId, UnreadMessagesView::getUnread, Integer::max));
  }

  public int getUnreadMessageCountForUsersAndRequest(
      Collection<Integer> userIds, Integer requestId) {
    var spec = specFactory.build(userIds, requestId);
    return unreadRepository.findAll(spec).stream()
        .mapToInt(UnreadMessagesView::getUnread)
        .min()
        .orElse(0);
  }

  public List<UnreadMessagesView> getUnreadByAllAccountUsersMessages(
      Collection<Integer> assignmentTypesToExclude, Collection<Integer> accountTypesToExclude) {
    var unreadMessages =
        unreadRepository.findAll(
            specFactory.build(assignmentTypesToExclude, accountTypesToExclude));
    var accountIds = unreadByAllAccountUsersFilter.toUnreadByAllUsersAccountIds(unreadMessages);
    return unreadMessages.stream().filter(v -> accountIds.contains(v.getAccountId())).toList();
  }

  public int getUnreadMessageCountForUserAndRequest(Integer userId, Integer requestId) {
    return unreadRepository
        .findByUserIdAndRequestId(userId, requestId)
        .map(UnreadMessagesView::getUnread)
        .orElse(0);
  }

  public Collection<UnreadMessagesView> getUnreadMessagesForAuthenticatedUser() {
    return getUnreadMessagesForUser(userSupplier.forceGetAuthenticatedUserPlatformId());
  }

  public Collection<UnreadMessagesView> getUnreadMessagesForUser(Integer userId) {
    return unreadRepository.findAllByUserId(userId).orElse(new ArrayList<>());
  }

  public List<ConversationMessage> getUnreadMessagesForUser(
      Integer conversationId, Integer userId) {
    var unreadMessageIds = getUnreadMessageIds(conversationId, userId);
    return getByIds(unreadMessageIds);
  }

  private Set<Integer> getUnreadMessageIds(Integer conversationId, Integer userId) {
    var readByUserMessageIds = getConversationMessageIdsReadByUser(conversationId, userId);
    var allMessageIds = getAllConversationMessageIds(conversationId);
    var messageIdsSentByUser = getConversationMessageIdsSentByUser(conversationId, userId);
    var unreadMessageIds =
        allMessageIds.stream()
            .filter(id -> !readByUserMessageIds.contains(id) && !messageIdsSentByUser.contains(id))
            .collect(Collectors.toSet());
    log.info(
        "found {} unread message ids for conversation {} and user {}",
        unreadMessageIds.size(),
        conversationId,
        userId);
    return unreadMessageIds;
  }

  private Set<Integer> getConversationMessageIdsReadByUser(Integer conversationId, Integer userId) {
    return receiptRepository
        .findAllByMessageConversationIdAndReadByUserId(conversationId, userId)
        .stream()
        .map(ConversationMessageReceiptMessageId::getMessage)
        .map(ConversationMessageId::getId)
        .collect(Collectors.toSet());
  }

  private Set<Integer> getAllConversationMessageIds(Integer conversationId) {
    return messageRepository.findAllIdsByConversationId(conversationId).stream()
        .map(ConversationMessageId::getId)
        .collect(Collectors.toSet());
  }

  private Set<Integer> getConversationMessageIdsSentByUser(Integer conversationId, Integer userId) {
    return messageRepository
        .findAllIdsByConversationIdAndSentByUserId(conversationId, userId)
        .stream()
        .map(ConversationMessageId::getId)
        .collect(Collectors.toSet());
  }

  private List<ConversationMessage> getByIds(Collection<Integer> messageIds) {
    return IterableUtils.toList(messageRepository.findAllById(messageIds));
  }
}

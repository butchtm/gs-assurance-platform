package org.goldstandard.assurance.api.service.notification;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.view.UnreadMessagesView;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountConversationsGrouper {

  private final AccountFinder accountFinder;
  private final RequestFinder requestFinder;

  public List<UnreadNotificationData> groupUnreadMessagesByAccount(
      List<UnreadMessagesView> unreadMessages) {
    var requestIdsByAccountId = groupRequestIdsByAccountId(unreadMessages);
    return convertToUnreadNotificationData(requestIdsByAccountId);
  }

  private Map<Integer, Set<Integer>> groupRequestIdsByAccountId(
      List<UnreadMessagesView> unreadMessages) {
    return unreadMessages.stream()
        .filter(UnreadMessagesView::hasAny)
        .collect(
            groupingBy(
                UnreadMessagesView::getAccountId,
                mapping(UnreadMessagesView::getRequestId, toSet())));
  }

  private List<UnreadNotificationData> convertToUnreadNotificationData(
      Map<Integer, Set<Integer>> requestIdsByAccount) {
    return requestIdsByAccount.entrySet().stream().map(this::toUnreadNotificationData).toList();
  }

  private UnreadNotificationData toUnreadNotificationData(Map.Entry<Integer, Set<Integer>> entry) {
    var account = accountFinder.forceGetAccountById(entry.getKey());
    var requests = fetchRequestsFromIds(entry.getValue());
    return new UnreadNotificationData(account, requests);
  }

  private List<Request> fetchRequestsFromIds(Set<Integer> requestIds) {
    return requestIds.stream().map(requestFinder::getRequestById).toList();
  }
}

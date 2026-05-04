package org.goldstandard.assurance.api.service.conversation;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.view.UnreadMessagesView;
import org.goldstandard.assurance.api.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UnreadByAllAccountUsersFilter {

  private final UserRepository userRepository;

  public Collection<Integer> toUnreadByAllUsersAccountIds(List<UnreadMessagesView> unreadMessages) {
    var allAccountIds =
        unreadMessages.stream().map(UnreadMessagesView::getAccountId).collect(toSet());
    var allAccountUserCounts = userRepository.allUserCountsByAccountId(allAccountIds);
    var unreadUsersByAccountId = groupUserIdsByAccountId(unreadMessages);
    var unreadByAllUsersAccountIds = new ArrayList<Integer>();
    for (Map.Entry<Integer, Set<Integer>> entry : unreadUsersByAccountId.entrySet()) {
      var accountId = entry.getKey();
      var numberOfUnreadUsers = entry.getValue().size();
      var numberOfUsersInAccount = allAccountUserCounts.get(accountId);
      log.debug(
          "{} unread users of {} in total for account {}",
          numberOfUnreadUsers,
          numberOfUsersInAccount,
          accountId);
      if (numberOfUnreadUsers >= numberOfUsersInAccount) {
        unreadByAllUsersAccountIds.add(accountId);
      }
    }
    log.info(
        "returning unread by all users account ids {} for original list {}",
        unreadByAllUsersAccountIds,
        allAccountIds);
    return Collections.unmodifiableCollection(unreadByAllUsersAccountIds);
  }

  private Map<Integer, Set<Integer>> groupUserIdsByAccountId(
      List<UnreadMessagesView> unreadMessages) {
    return unreadMessages.stream()
        .filter(UnreadMessagesView::hasAny)
        .collect(
            groupingBy(
                UnreadMessagesView::getAccountId, mapping(UnreadMessagesView::getUserId, toSet())));
  }
}

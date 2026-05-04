package org.goldstandard.assurance.api.service.conversation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.goldstandard.assurance.api.entity.view.UnreadMessagesView;
import org.goldstandard.assurance.api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UnreadByAllAccountUsersFilterTest {

  @InjectMocks UnreadByAllAccountUsersFilter filter;

  @Mock UserRepository repository;

  @Test
  void shouldReturnAccountIdsWhereMessagesAreUnreadByAllMembers() {
    var unreadMessage1 = UnreadMessagesView.builder().accountId(1).userId(1).unread(1).build();
    var unreadMessage2 = UnreadMessagesView.builder().accountId(1).userId(2).unread(1).build();
    var unreadMessage3 = UnreadMessagesView.builder().accountId(2).userId(3).unread(1).build();
    when(repository.allUserCountsByAccountId(Set.of(1, 2))).thenReturn(Map.of(1, 2, 2, 2));

    var accountIds =
        filter.toUnreadByAllUsersAccountIds(
            List.of(unreadMessage1, unreadMessage2, unreadMessage3));

    assertThat(accountIds).containsExactly(1);
  }
}

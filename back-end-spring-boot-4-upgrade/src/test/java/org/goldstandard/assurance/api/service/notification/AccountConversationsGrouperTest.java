package org.goldstandard.assurance.api.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.goldstandard.assurance.api.entity.view.UnreadMessagesView;
import org.goldstandard.assurance.api.fixtures.AccountMother;
import org.goldstandard.assurance.api.fixtures.RequestMother;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountConversationsGrouperTest {

  @InjectMocks AccountConversationsGrouper grouper;
  @Mock AccountFinder accountFinder;
  @Mock RequestFinder requestFinder;

  @Test
  void shouldGroupUnreadMessagesByAccount() {
    var account1 = AccountMother.withIdAndName(1, "Account 1");
    var account2 = AccountMother.withIdAndName(2, "Account 2");
    var request1 = RequestMother.withId(10);
    var request2 = RequestMother.withId(20);
    var request3 = RequestMother.withId(30);

    var unreadMessages =
        List.of(
            buildUnreadMessage(1, 1, 10),
            buildUnreadMessage(2, 1, 20),
            buildUnreadMessage(3, 2, 30));

    when(accountFinder.forceGetAccountById(1)).thenReturn(account1);
    when(accountFinder.forceGetAccountById(2)).thenReturn(account2);
    when(requestFinder.getRequestById(10)).thenReturn(request1);
    when(requestFinder.getRequestById(20)).thenReturn(request2);
    when(requestFinder.getRequestById(30)).thenReturn(request3);

    var result = grouper.groupUnreadMessagesByAccount(unreadMessages);

    assertThat(result).hasSize(2);

    var account1Data =
        result.stream().filter(data -> data.account().equals(account1)).findFirst().orElseThrow();
    assertThat(account1Data.requests()).containsExactlyInAnyOrder(request1, request2);

    var account2Data =
        result.stream().filter(data -> data.account().equals(account2)).findFirst().orElseThrow();
    assertThat(account2Data.requests()).containsExactly(request3);
  }

  @Test
  void shouldDeduplicateRequestIdsForSameAccount() {
    var account = AccountMother.withIdAndName(1, "Account 1");
    var request = RequestMother.withId(10);

    var unreadMessages =
        List.of(
            buildUnreadMessage(1, 1, 10),
            buildUnreadMessage(2, 1, 10),
            buildUnreadMessage(3, 1, 10));

    when(accountFinder.forceGetAccountById(1)).thenReturn(account);
    when(requestFinder.getRequestById(10)).thenReturn(request);

    var result = grouper.groupUnreadMessagesByAccount(unreadMessages);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().requests()).containsExactly(request);
  }

  @Test
  void shouldFilterOutMessagesWithNoUnreadCount() {
    var account = AccountMother.withIdAndName(1, "Account 1");
    var request = RequestMother.withId(10);

    var unreadMessages = List.of(buildUnreadMessage(1, 1, 10, 0), buildUnreadMessage(2, 1, 20, 5));

    when(accountFinder.forceGetAccountById(1)).thenReturn(account);
    when(requestFinder.getRequestById(20)).thenReturn(request);

    var result = grouper.groupUnreadMessagesByAccount(unreadMessages);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().requests()).containsExactly(request);
  }

  private UnreadMessagesView buildUnreadMessage(
      Integer userId, Integer accountId, Integer requestId) {
    return buildUnreadMessage(userId, accountId, requestId, 5);
  }

  private UnreadMessagesView buildUnreadMessage(
      Integer userId, Integer accountId, Integer requestId, Integer unreadCount) {
    return UnreadMessagesView.builder()
        .userId(userId)
        .accountId(accountId)
        .requestId(requestId)
        .unread(unreadCount)
        .build();
  }
}

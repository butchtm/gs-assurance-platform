package org.goldstandard.assurance.api.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Comment;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.service.AssociatedAccountsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentNotificationServiceTest {

  @InjectMocks CommentNotificationService commentNotificationService;

  @Mock NotificationService notificationService;

  @Mock AssociatedAccountsService associatedAccountsService;

  @Captor ArgumentCaptor<List<Account>> accountListCaptor;

  @Captor ArgumentCaptor<TemplateKey> templateKeyCaptor;

  @Test
  void shouldSendCommentNotification() {
    var account = buildAccount();
    var request = buildRequest(account.getId());
    var comment = buildComment(request.getId());
    when(associatedAccountsService.findAccountsAssociatedWithRequest(
            request, Collections.emptyList()))
        .thenReturn(List.of(account));

    commentNotificationService.sendCommentNotification(comment, request);

    verify(notificationService)
        .sendAccountNotifications(
            templateKeyCaptor.capture(), eq(request), accountListCaptor.capture());
    assertThat(accountListCaptor.getValue()).containsExactly(account);
    assertThat(templateKeyCaptor.getValue().fullName()).isEqualTo("COMMENT_RECEIVED-v0.0.3");
  }

  private static Account buildAccount() {
    Account account = new Account();
    account.setId(10);
    account.setName("test");
    return account;
  }

  private static Request buildRequest(int accountId) {
    Project project = new Project();
    project.setAccountId(accountId);
    return Request.builder().id(1).project(project).build();
  }

  private static Comment buildComment(int requestId) {
    return Comment.builder()
        .id(0)
        .requestId(requestId)
        .submittedComment("test comment")
        .uploadedByName("test name")
        .uploadedByEmail("test email")
        .uploadedDate(ZonedDateTime.now())
        .addressed(false)
        .publicAccess(true)
        .anonymous(false)
        .build();
  }
}

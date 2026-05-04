package org.goldstandard.assurance.api.service.notification;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import org.goldstandard.assurance.api.fixtures.AccountMother;
import org.goldstandard.assurance.api.fixtures.ProjectMother;
import org.goldstandard.assurance.api.fixtures.RequestMother;
import org.goldstandard.assurance.api.repository.system.RequestStatusHistoryRepository;
import org.goldstandard.assurance.api.service.activity.PathwayGroupDeadlineCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationModelFactoryUnreadMessagesTest {

  @InjectMocks NotificationModelFactory factory;
  @Mock RequestStatusHistoryRepository requestStatusHistoryRepository;
  @Mock PathwayGroupDeadlineCalculator pathwayGroupDeadlineCalculator;

  @Test
  void shouldConvertUnreadNotificationDataToNotificationModels() {
    var account1 = AccountMother.withIdAndName(1, "Account 1");
    var account2 = AccountMother.withIdAndName(2, "Account 2");
    var project1 = ProjectMother.withNameAndGsid("Test Project", "PRJ-001");
    var project2 = ProjectMother.withNameAndGsid("Test Project", "PRJ-002");
    var project3 = ProjectMother.withNameAndGsid("Test Project", "PRJ-003");
    var request1 = RequestMother.withIdNameAndProject(10, "Request 1", project1);
    var request2 = RequestMother.withIdNameAndProject(20, "Request 2", project2);
    var request3 = RequestMother.withIdNameAndProject(30, "Request 3", project3);

    var unreadNotificationData =
        List.of(
            new UnreadNotificationData(account1, List.of(request1, request2)),
            new UnreadNotificationData(account2, List.of(request3)));

    var result = factory.toNotificationModelList(unreadNotificationData);

    assertThat(result).hasSize(2);

    var model1 = result.getFirst();
    assertThat(model1.getUnread().getRecipient()).isEqualTo(account1);
    assertThat(model1.getUnread().getRequests()).hasSize(2);
    assertThat(model1.getUnread().getRequests().getFirst().getRequest().getId()).isEqualTo(10);
    assertThat(model1.getUnread().getRequests().getFirst().getRequest().getName())
        .isEqualTo("Request 1");
    assertThat(model1.getUnread().getRequests().getFirst().getProject().getGsid())
        .isEqualTo("PRJ-001");

    var model2 = result.get(1);
    assertThat(model2.getUnread().getRecipient()).isEqualTo(account2);
    assertThat(model2.getUnread().getRequests()).hasSize(1);
    assertThat(model2.getUnread().getRequests().getFirst().getRequest().getId()).isEqualTo(30);
  }

  @Test
  void shouldHandleEmptyUnreadNotificationData() {
    var result = factory.toNotificationModelList(Collections.emptyList());

    assertThat(result).isEmpty();
  }

  @Test
  void shouldIncludeProjectDetailsForEachRequest() {
    var account = AccountMother.withIdAndName(1, "Test Account");
    var project1 = ProjectMother.withNameAndGsid("Test Project", "PRJ-001");
    var project2 = ProjectMother.withNameAndGsid("Test Project", "PRJ-002");
    var request1 = RequestMother.withIdNameAndProject(10, "Request 1", project1);
    var request2 = RequestMother.withIdNameAndProject(20, "Request 2", project2);

    var unreadNotificationData =
        List.of(new UnreadNotificationData(account, List.of(request1, request2)));

    var result = factory.toNotificationModelList(unreadNotificationData);

    assertThat(result).hasSize(1);
    var requests = result.getFirst().getUnread().getRequests();
    assertThat(requests).hasSize(2);
    assertThat(requests.getFirst().getProject()).isNotNull();
    assertThat(requests.getFirst().getProject().getGsid()).isEqualTo("PRJ-001");
    assertThat(requests.get(1).getProject()).isNotNull();
    assertThat(requests.get(1).getProject().getGsid()).isEqualTo("PRJ-002");
  }

  @Test
  void shouldCreateSeparateModelsForEachAccount() {
    var account1 = AccountMother.withIdAndName(1, "Account 1");
    var account2 = AccountMother.withIdAndName(2, "Account 2");
    var account3 = AccountMother.withIdAndName(3, "Account 3");
    var project1 = ProjectMother.withNameAndGsid("Test Project", "PRJ-001");
    var project2 = ProjectMother.withNameAndGsid("Test Project", "PRJ-002");
    var project3 = ProjectMother.withNameAndGsid("Test Project", "PRJ-003");
    var request1 = RequestMother.withIdNameAndProject(10, "Request 1", project1);
    var request2 = RequestMother.withIdNameAndProject(20, "Request 2", project2);
    var request3 = RequestMother.withIdNameAndProject(30, "Request 3", project3);

    var unreadNotificationData =
        List.of(
            new UnreadNotificationData(account1, List.of(request1)),
            new UnreadNotificationData(account2, List.of(request2)),
            new UnreadNotificationData(account3, List.of(request3)));

    var result = factory.toNotificationModelList(unreadNotificationData);

    assertThat(result).hasSize(3);
    assertThat(result.get(0).getUnread().getRecipient()).isEqualTo(account1);
    assertThat(result.get(1).getUnread().getRecipient()).isEqualTo(account2);
    assertThat(result.get(2).getUnread().getRecipient()).isEqualTo(account3);
  }
}

package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.entity.lookup.RequestStatus.COMPLETENESS_CHECK_ID;
import static org.goldstandard.assurance.api.entity.lookup.RequestStatus.IN_AUDIT_ID;
import static org.goldstandard.assurance.api.entity.lookup.RequestStatus.IN_PREPARATION_ID;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.service.notification.DocumentPiiNotificationService;
import org.goldstandard.assurance.api.service.notification.RequestNotificationService;
import org.goldstandard.assurance.api.util.RequestStatusChecker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestUpdateNotificationSenderTest {

  public static final int PROJECT_ID = 1;

  @InjectMocks RequestUpdateNotificationSender notificationSender;

  @Mock RequestFinder requestFinder;

  @Mock RequestNotificationService requestNotificationService;

  @Mock DocumentPiiNotificationService piiNotificationService;

  @Mock RequestStatusChecker statusChecker;

  @Captor ArgumentCaptor<Request> requestCaptor;

  @Test
  void shouldSendRequestNotification() {
    var originalRequest = buildOriginalRequest();
    var updatedRequest = buildUpdatedRequest();
    when(requestFinder.getRequestById(originalRequest.getId())).thenReturn(updatedRequest);
    var expectedStatusId = originalRequest.getCurrentRequestStatus().getId();

    notificationSender.sendUpdateNotifications(originalRequest);

    verify(requestNotificationService)
        .sendRequestNotification(requestCaptor.capture(), eq(expectedStatusId));
    assertThat(requestCaptor.getValue()).isEqualTo(updatedRequest);
  }

  @ParameterizedTest
  @EnumSource(value = StatusTransition.class)
  void shouldSendDocumentPiiNotificationIfReturningToInPrepOrInAudit(StatusTransition transition) {
    var originalRequest = buildRequestWithStatus(transition.fromStatus);
    var updatedRequest = buildRequestWithStatus(transition.toStatus);
    when(requestFinder.getRequestById(originalRequest.getId())).thenReturn(updatedRequest);
    when(statusChecker.movedBackToInProgressOrInAuditFromCompletionCheck(
            originalRequest.getCurrentRequestStatus(), updatedRequest.getCurrentRequestStatus()))
        .thenReturn(true);

    notificationSender.sendUpdateNotifications(originalRequest);

    verify(piiNotificationService).sendDocumentPiiNotification(requestCaptor.capture());
    assertThat(requestCaptor.getValue()).isEqualTo(originalRequest);
  }

  @Getter
  @AllArgsConstructor
  public enum StatusTransition {
    TO_AUDIT(COMPLETENESS_CHECK_ID, IN_AUDIT_ID),
    TO_IN_PREP(COMPLETENESS_CHECK_ID, IN_PREPARATION_ID);
    private final Integer fromStatus;
    private final Integer toStatus;
  }

  private static Request buildOriginalRequest() {
    return buildRequestWithStatus(2);
  }

  private static Request buildUpdatedRequest() {
    return buildRequestWithStatus(3);
  }

  private static Request buildRequestWithStatus(Integer statusId) {
    return Request.builder().currentRequestStatus(new RequestStatus(statusId)).build();
  }
}

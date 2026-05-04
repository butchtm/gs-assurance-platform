package org.goldstandard.assurance.api.service.notification;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.entity.system.Process;
import org.goldstandard.assurance.api.service.process.ProcessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConsultationNotificationProcessTest {

  @InjectMocks private ConsultationNotificationProcess notificationProcess;
  @Mock private ProcessService processService;
  @Mock private ConsultationNotificationJobRunPolicy runPolicy;
  @Mock private ConsultationNotificationService notificationService;

  @Captor private ArgumentCaptor<User> userCaptor;

  @Test
  void shouldRunNotificationProcessIfRequired() {
    var process = mock(Process.class);
    when(runPolicy.shouldRun()).thenReturn(true);
    when(processService.recordConsultationNotificationJobProcessStart()).thenReturn(process);

    notificationProcess.sendConsultationNotificationsIfRequired();

    verify(runPolicy).shouldRun();
    verify(processService).recordConsultationNotificationJobProcessStart();
    verify(notificationService).sendConsultationNotifications(userCaptor.capture());
    //    assertThat(userCaptor.getValue().getIdpUserId())
    //        .isEqualTo(ConsultationNotificationProcess.SYSTEM_USER_UUID);
    verify(processService).recordProcessComplete(process);
  }

  @Test
  void shouldNotRunPollerIfNotRequired() {
    when(runPolicy.shouldRun()).thenReturn(false);

    notificationProcess.sendConsultationNotificationsIfRequired();

    verify(runPolicy).shouldRun();
    verifyNoInteractions(processService, notificationService);
  }
}

package org.goldstandard.assurance.api.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.ConsultationType;
import org.goldstandard.assurance.api.entity.Ngo;
import org.goldstandard.assurance.api.entity.StakeholderConsultation;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.repository.NgoRepository;
import org.goldstandard.assurance.api.service.StakeholderConsultationService;
import org.goldstandard.assurance.api.service.notification.model.NotificationModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConsultationNotificationServiceTest {

  @InjectMocks ConsultationNotificationService consultationNotificationService;

  @Mock StakeholderConsultationService consultationService;

  @Mock NotificationService notificationService;

  @Mock NgoRepository ngoRepository;

  @Mock NotificationModelFactory modelFactory;

  @Mock AccountConsultationsGrouper accountConsultationsGrouper;

  @Mock NotificationModel model;

  @Captor ArgumentCaptor<List<Account>> accountListCaptor;

  @Captor ArgumentCaptor<List<Ngo>> ngosCaptor;

  @Captor ArgumentCaptor<TemplateKey> templateKeyCaptor;

  @Captor ArgumentCaptor<User> userCaptor;

  private final User authenticatedUser = new User();

  @Test
  void shouldNotSendConsultationNotificationIfPrivateConsultationNotFound() {
    when(consultationService.getLiveUnnotifiedStakeholderConsultations())
        .thenReturn(Collections.emptyList());

    consultationNotificationService.sendConsultationNotifications(authenticatedUser);

    verifyNoInteractions(notificationService);
  }

  @Test
  void sendAccountConsultationNotificationsIfPrivateConsultationFound() {
    var consultations = buildConsultationList(ConsultationType.TAC_CONSULTATION);
    when(consultationService.getLiveUnnotifiedStakeholderConsultations()).thenReturn(consultations);
    var account = buildAccount();
    var expectedMap = new HashMap<Account, Collection<StakeholderConsultation>>();
    expectedMap.put(account, consultations);
    when(modelFactory.toNotificationModel(consultations)).thenReturn(model);
    when(accountConsultationsGrouper.groupConsultationsByAccount(consultations))
        .thenReturn(expectedMap);
    consultationNotificationService.sendConsultationNotifications(authenticatedUser);

    verify(notificationService)
        .sendAccountNotifications(
            templateKeyCaptor.capture(),
            eq(model),
            accountListCaptor.capture(),
            userCaptor.capture());
    verify(notificationService, never()).sendNgoNotifications(any(), any(), any(), any());
    verify(consultationService).markNotificationsSent(consultations);
    assertThat(accountListCaptor.getValue()).containsExactly(account);
    assertThat(templateKeyCaptor.getValue().fullName()).isEqualTo("CONSULTATION_STARTED-v0.0.6");
    assertThat(userCaptor.getValue()).isEqualTo(authenticatedUser);
  }

  @Test
  void shouldNotSendConsultationNotificationsIfPublicConsultationNotFound() {
    when(consultationService.getLiveUnnotifiedStakeholderConsultations())
        .thenReturn(Collections.emptyList());

    consultationNotificationService.sendConsultationNotifications(authenticatedUser);

    verifyNoInteractions(ngoRepository, notificationService);
  }

  @Test
  void shouldSendNgoConsultationNotificationsIfPublicConsultationFound() {
    var consultations = buildConsultationList(ConsultationType.PUBLIC_CONSULTATION);
    when(consultationService.getLiveUnnotifiedStakeholderConsultations()).thenReturn(consultations);
    when(modelFactory.toNotificationModel(consultations)).thenReturn(model);
    var ngo = mock(Ngo.class);
    when(ngoRepository.findAll()).thenReturn(List.of(ngo));

    consultationNotificationService.sendConsultationNotifications(authenticatedUser);

    verify(notificationService)
        .sendNgoNotifications(
            templateKeyCaptor.capture(), eq(model), ngosCaptor.capture(), userCaptor.capture());
    verify(notificationService, never()).sendAccountNotifications(any(), any(), any(), any());
    verify(consultationService).markNotificationsSent(consultations);
    assertThat(templateKeyCaptor.getValue().fullName())
        .isEqualTo("NGO_CONSULTATION_NOTIFICATION-v0.0.3");
    assertThat(ngosCaptor.getValue()).containsExactly(ngo);
    assertThat(userCaptor.getValue()).isEqualTo(authenticatedUser);
  }

  private static Account buildAccount() {
    var account = new Account();
    account.setId(10);
    account.setName("test");
    return account;
  }

  private static List<StakeholderConsultation> buildConsultationList(int typeId) {
    return List.of(
        StakeholderConsultation.builder()
            .id(123)
            .consultationType(new ConsultationType(typeId))
            .build());
  }
}

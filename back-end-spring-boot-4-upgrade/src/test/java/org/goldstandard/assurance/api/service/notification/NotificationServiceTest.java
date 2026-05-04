package org.goldstandard.assurance.api.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Ngo;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.entity.notification.NotificationTemplate;
import org.goldstandard.assurance.api.fixtures.NotificationRecipientMother;
import org.goldstandard.assurance.api.service.notification.model.NotificationModel;
import org.goldstandard.assurance.api.service.notification.model.NotificationParams;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  private static final TemplateKey TEMPLATE_KEY = new TemplateKey("test-key", "v1");
  private static final NotificationTemplate TEMPLATE = buildTemplate(TEMPLATE_KEY);
  private static final NotificationModel MODEL = buildModel();
  private static final NotificationRecipient RECIPIENT = NotificationRecipientMother.build();

  @InjectMocks NotificationService notificationService;

  @Mock ContactMappingService contactMappingService;

  @Mock TemplateService templateService;

  @Mock Clock clock;

  @Mock EmailNotificationSender notificationSender;

  @Mock AuthenticatedUserSupplier userSupplier;

  @Captor ArgumentCaptor<NotificationParams> paramsCaptor;

  @Test
  void shouldNotAttemptToSendNotificationIfMapperReturnsNullEmailAddress() {
    var emailAddress = RECIPIENT.getEmailAddress();
    when(contactMappingService.mapContact(emailAddress)).thenReturn(null);

    notificationService.sendNotification(TEMPLATE, MODEL, RECIPIENT);

    verifyNoInteractions(notificationSender);
  }

  @Test
  void shouldSendSingleNotification() {
    mockContactsMapped(RECIPIENT.getEmailAddress());
    var authenticatedUser = new User();
    when(userSupplier.getAuthenticatedUser()).thenReturn(Optional.of(authenticatedUser));

    notificationService.sendNotification(TEMPLATE, MODEL, RECIPIENT);

    verify(notificationSender).sendNotification(paramsCaptor.capture());
    var params = paramsCaptor.getValue();
    assertThat(params.getTemplate()).isEqualTo(TEMPLATE);
    assertThat(params.getModel()).isEqualTo(MODEL);
    assertThat(params.getRecipient()).isEqualTo(RECIPIENT);
    assertThat(params.getRecipientAddressOverride()).isEqualTo(RECIPIENT.getEmailAddress());
    assertThat(params.getAuthenticatedUser()).contains(authenticatedUser);
  }

  @Test
  void shouldSendMultipleNotifications() {
    var recipient1 = NotificationRecipientMother.build(1);
    var recipient2 = NotificationRecipientMother.build(2);
    mockContactsMapped(recipient1.getEmailAddress(), recipient2.getEmailAddress());

    notificationService.sendNotifications(TEMPLATE, MODEL, List.of(recipient1, recipient2));

    verify(notificationSender, times(2)).sendNotification(paramsCaptor.capture());

    List<NotificationParams> allParams = paramsCaptor.getAllValues();
    assertThat(allParams).hasSize(2);
    var params1 = allParams.getFirst();
    assertThat(params1.getTemplate()).isEqualTo(TEMPLATE);
    assertThat(params1.getModel()).isEqualTo(MODEL);
    assertThat(params1.getRecipientAddressOverride()).isEqualTo(recipient1.getEmailAddress());
    assertThat(params1.getRecipient()).isEqualTo(recipient1);
    var params2 = allParams.get(1);
    assertThat(params2.getTemplate()).isEqualTo(TEMPLATE);
    assertThat(params2.getModel()).isEqualTo(MODEL);
    assertThat(params2.getRecipientAddressOverride()).isEqualTo(recipient2.getEmailAddress());
    assertThat(params2.getRecipient()).isEqualTo(recipient2);
  }

  @Test
  void testSendAccountNotifications() {
    var account = buildAccount();
    mockContactsMapped(account.getEmailAddress());
    when(templateService.forceGetTemplate(TEMPLATE_KEY)).thenReturn(TEMPLATE);

    notificationService.sendAccountNotifications(TEMPLATE_KEY, MODEL, List.of(account));

    verify(notificationSender).sendNotification(paramsCaptor.capture());
    var params = paramsCaptor.getValue();
    assertThat(params.getModel()).isEqualTo(MODEL);
    assertThat(params.getTemplate()).isEqualTo(TEMPLATE);
    var recipient = params.getRecipient();
    assertThat(recipient.getId()).isEqualTo(account.getId());
    assertThat(recipient.getName()).isEqualTo(account.getName());
    assertThat(recipient.getEmailAddress()).isEqualTo(account.getEmailAddress());
  }

  @Test
  void testSendNgoNotifications() {
    var ngo = buildNgo();
    mockContactsMapped(ngo.getEmailAddress());
    when(templateService.forceGetTemplate(TEMPLATE_KEY)).thenReturn(TEMPLATE);
    var authenticatedUser = new User();

    notificationService.sendNgoNotifications(TEMPLATE_KEY, MODEL, List.of(ngo), authenticatedUser);

    verify(notificationSender).sendNotification(paramsCaptor.capture());
    var params = paramsCaptor.getValue();
    assertThat(params.getModel()).isEqualTo(MODEL);
    assertThat(params.getTemplate()).isEqualTo(TEMPLATE);
    assertThat(params.getAuthenticatedUser()).contains(authenticatedUser);
    var recipient = params.getRecipient();
    assertThat(recipient.getId()).isEqualTo(ngo.getId());
    assertThat(recipient.getName()).isEqualTo(ngo.getName());
    assertThat(recipient.getEmailAddress()).isEqualTo(ngo.getEmailAddress());
  }

  private void mockContactsMapped(String... emails) {
    Stream.of(emails)
        .forEach(email -> when(contactMappingService.mapContact(email)).thenReturn(email));
  }

  private void mockTimeNow() {
    ZonedDateTime now = ZonedDateTime.now();
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(clock.instant()).thenReturn(now.toInstant());
  }

  private static NotificationTemplate buildTemplate(TemplateKey key) {
    return NotificationTemplate.builder().name(key.name()).version(key.version()).build();
  }

  private static NotificationModel buildModel() {
    return NotificationModel.builder()
        .request(NotificationModel.RequestModel.builder().id(123).build())
        .build();
  }

  private static Account buildAccount() {
    var account = new Account();
    account.setId(123);
    account.setName("test-account");
    account.setEmailAddress("test-account@test.com");
    return account;
  }

  private static Ngo buildNgo() {
    return Ngo.builder().id(987).name("test-ngo").emailAddress("test-ngo@ttest.com").build();
  }
}

package org.goldstandard.assurance.api.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Invoice;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.InvoiceStatus;
import org.goldstandard.assurance.api.service.AssociatedAccountsService;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InvoiceNotificationServiceTest {

  private static final Integer TEST_INVOICE_ID = 3525;

  private static final Integer TEST_PD_ACCOUNT_ID = 5336;
  private static final Integer TEST_REQUEST_ID = 123;

  @Mock private RequestFinder requestFinder;

  @Mock private AssociatedAccountsService accountService;

  @Mock private NotificationService notificationService;

  @InjectMocks private InvoiceNotificationService invoiceNotificationService;

  @Captor private ArgumentCaptor<List<Account>> accountsCaptor;

  @Captor private ArgumentCaptor<TemplateKey> templateKeyCaptor;

  @Test
  void shouldSendInvoiceNotification() {
    Request request = createTestRequest();
    when(requestFinder.getRequestByInvoiceId(TEST_INVOICE_ID)).thenReturn(request);
    Account account = createTestAccount();
    when(accountService.getInvoiceNotificationAddressees(request)).thenReturn(List.of(account));

    invoiceNotificationService.sendInvoiceNotification(
        Invoice.builder()
            .status(new InvoiceStatus(InvoiceStatus.GENERATED_ID))
            .id(TEST_INVOICE_ID)
            .build());

    verify(notificationService, times(1))
        .sendAccountNotifications(
            templateKeyCaptor.capture(), eq(request), accountsCaptor.capture());
    assertThat(accountsCaptor.getValue()).containsExactly(account);
    assertThat(templateKeyCaptor.getValue().fullName()).isEqualTo("INVOICE_NOTIFICATION-v0.0.2");
  }

  @Test
  void shouldNotSendNotificationIfStatusNotGenerated() {
    invoiceNotificationService.sendInvoiceNotification(
        Invoice.builder().status(new InvoiceStatus(0)).build());

    verifyNoInteractions(requestFinder, accountService, notificationService);
  }

  private Request createTestRequest() {
    final Project project = new Project();
    project.setAccountId(TEST_PD_ACCOUNT_ID);
    return Request.builder().id(TEST_REQUEST_ID).project(project).build();
  }

  private Account createTestAccount() {
    final Account account = new Account();
    account.setId(TEST_PD_ACCOUNT_ID);
    return account;
  }
}

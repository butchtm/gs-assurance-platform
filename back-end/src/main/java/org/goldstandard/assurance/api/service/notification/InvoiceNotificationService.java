package org.goldstandard.assurance.api.service.notification;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Invoice;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.service.AssociatedAccountsService;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class InvoiceNotificationService {

  private static final TemplateKey TEMPLATE_KEY = new TemplateKey("INVOICE_NOTIFICATION", "v0.0.2");

  private final RequestFinder requestFinder;
  private final AssociatedAccountsService accountsService;
  private final NotificationService notificationService;

  public void sendInvoiceNotification(Invoice invoice) {
    if (!invoice.hasGeneratedStatus()) {
      log.info(
          "invoice {} has status {} so not sending notification",
          invoice.getId(),
          invoice.getStatus());
      return;
    }
    Request request = requestFinder.getRequestByInvoiceId(invoice.getId());
    List<Account> addressees = accountsService.getInvoiceNotificationAddressees(request);
    notificationService.sendAccountNotifications(TEMPLATE_KEY, request, addressees);
  }
}

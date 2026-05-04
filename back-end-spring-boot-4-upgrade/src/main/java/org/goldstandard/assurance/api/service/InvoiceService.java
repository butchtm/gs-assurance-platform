package org.goldstandard.assurance.api.service;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Invoice;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.repository.InvoiceRepository;
import org.goldstandard.assurance.api.service.notification.InvoiceNotificationService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class InvoiceService {

  private final RequestFinder requestFinder;
  private final InvoiceTransaction transaction;
  private final InvoiceNotificationService notificationService;
  private final Clock clock;
  private final InvoiceRepository repository;

  public Invoice createInvoice(int requestId, Invoice newInvoice) {
    var request = requestFinder.getRequestById(requestId);
    setStatusChangedNow(newInvoice);
    request.setInvoice(newInvoice);
    return transaction.createInvoiceAgainstRequest(request);
  }

  public Invoice updateInvoice(Integer invoiceId, Invoice updatedInvoice) {
    var originalInvoice = getInvoice(invoiceId);
    var statusChanged = originalInvoice.hasStatusChanged(updatedInvoice);
    applyUpdates(originalInvoice, updatedInvoice);
    var savedInvoice = transaction.updateInvoice(originalInvoice);
    if (statusChanged) {
      notificationService.sendInvoiceNotification(savedInvoice);
    }
    return savedInvoice;
  }

  public Invoice getInvoice(Integer invoiceId) {
    return repository
        .findById(invoiceId)
        .orElseThrow(() -> new NotFoundException("Invoice %d not found", invoiceId));
  }

  private void applyUpdates(Invoice originalInvoice, Invoice updatedInvoice) {
    var updatedStatus = updatedInvoice.getStatus();
    if (Objects.nonNull(updatedStatus) && originalInvoice.hasStatusChanged(updatedInvoice)) {
      originalInvoice.setStatus(updatedStatus);
      setStatusChangedNow(originalInvoice);
    }
    originalInvoice.setAmount(updatedInvoice.getAmount());
  }

  private void setStatusChangedNow(Invoice invoice) {
    invoice.setStatusChangedAt(ZonedDateTime.now(clock));
  }
}

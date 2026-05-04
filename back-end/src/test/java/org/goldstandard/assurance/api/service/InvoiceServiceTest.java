package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.goldstandard.assurance.api.entity.Invoice;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.InvoiceStatus;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.repository.InvoiceRepository;
import org.goldstandard.assurance.api.service.notification.InvoiceNotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

  private static final Integer INVOICE_ID = 31415;

  @Mock RequestFinder requestFinder;

  @Mock InvoiceRepository invoiceRepository;

  @Mock InvoiceNotificationService notificationService;

  @Mock InvoiceTransaction transaction;

  @Mock Clock clock;

  @InjectMocks InvoiceService invoiceService;

  @Captor ArgumentCaptor<Request> requestCaptor;

  @Captor ArgumentCaptor<Invoice> saveInvoiceCaptor;

  @Captor ArgumentCaptor<Invoice> notificationInvoiceCaptor;

  @Test
  void shouldCreateInvoiceAgainstRequest() {
    var now = givenTimeNow();
    var originalRequest = givenRequestFound();
    var invoice = Invoice.builder().status(InvoiceStatus.generated()).build();
    var savedInvoice = givenCreatedInvoiceReturnedFor(originalRequest, invoice);

    var result = invoiceService.createInvoice(originalRequest.getId(), invoice);

    assertThat(result).isEqualTo(savedInvoice);
    verify(transaction).createInvoiceAgainstRequest(requestCaptor.capture());
    var capturedRequest = requestCaptor.getValue();
    assertThat(capturedRequest)
        .usingRecursiveComparison()
        .ignoringFields("invoice")
        .isEqualTo(originalRequest);
    assertThat(capturedRequest.getInvoice()).isEqualTo(invoice);
    assertThat(invoice.getStatusChangedAt()).isEqualTo(now);
  }

  @Test
  void updateInvoiceShouldSaveUpdatedInvoice() {
    var now = givenTimeNow();
    var originalInvoice = givenOriginalInvoiceFoundWithStatus(InvoiceStatus.notTriggered());
    var updatedInvoice = Invoice.builder().status(InvoiceStatus.generated()).build();
    var savedInvoice = givenSavedInvoiceReturnedFor(originalInvoice);

    var result = invoiceService.updateInvoice(INVOICE_ID, updatedInvoice);

    assertThat(result).isEqualTo(savedInvoice);
    verify(transaction).updateInvoice(saveInvoiceCaptor.capture());
    var capturedInvoice = saveInvoiceCaptor.getValue();
    assertThat(capturedInvoice)
        .usingRecursiveComparison()
        .ignoringFields("status", "statusChangedAt")
        .isEqualTo(originalInvoice);
    assertThat(capturedInvoice.getStatus()).isEqualTo(updatedInvoice.getStatus());
    assertThat(capturedInvoice.getStatusChangedAt()).isEqualTo(now);
  }

  @Test
  void updateInvoiceShouldNotUpdateStatusOrStatusChangedAtIfStatusNotChanged() {
    var originalInvoice = givenOriginalInvoiceFoundWithStatus(InvoiceStatus.notTriggered());
    var updatedInvoice = Invoice.builder().status(InvoiceStatus.notTriggered()).build();
    var savedInvoice = givenSavedInvoiceReturnedFor(originalInvoice);

    var result = invoiceService.updateInvoice(INVOICE_ID, updatedInvoice);

    assertThat(result).isEqualTo(savedInvoice);
    verify(transaction).updateInvoice(saveInvoiceCaptor.capture());
    var capturedInvoice = saveInvoiceCaptor.getValue();
    assertThat(capturedInvoice).isEqualTo(originalInvoice);
  }

  @Test
  void updateInvoiceShouldNotUpdateStatusOrStatusChangedAtIfStatusIsNull() {
    var originalInvoice = givenOriginalInvoiceFoundWithStatus(InvoiceStatus.notTriggered());
    var updatedInvoice = Invoice.builder().status(null).build();
    var savedInvoice = givenSavedInvoiceReturnedFor(originalInvoice);

    var result = invoiceService.updateInvoice(INVOICE_ID, updatedInvoice);

    assertThat(result).isEqualTo(savedInvoice);
    verify(transaction).updateInvoice(saveInvoiceCaptor.capture());
    var capturedInvoice = saveInvoiceCaptor.getValue();
    assertThat(capturedInvoice).isEqualTo(originalInvoice);
  }

  @Test
  void updateInvoiceShouldSendNotificationWhenInvoiceStatusUpdated() {
    givenTimeNow();
    var originalInvoice = givenOriginalInvoiceFoundWithStatus(InvoiceStatus.notTriggered());
    var updatedInvoice = Invoice.builder().status(InvoiceStatus.generated()).build();
    var savedInvoice = givenSavedInvoiceReturnedFor(originalInvoice);

    invoiceService.updateInvoice(INVOICE_ID, updatedInvoice);

    verify(notificationService).sendInvoiceNotification(notificationInvoiceCaptor.capture());
    assertThat(notificationInvoiceCaptor.getValue()).isEqualTo(savedInvoice);
  }

  @Test
  void updateInvoiceShouldNoySendNotificationWhenInvoiceStatusNotUpdated() {
    var originalInvoice = givenOriginalInvoiceFoundWithStatus(InvoiceStatus.notTriggered());
    var updatedInvoice = Invoice.builder().status(InvoiceStatus.notTriggered()).build();
    givenSavedInvoiceReturnedFor(originalInvoice);

    invoiceService.updateInvoice(INVOICE_ID, updatedInvoice);

    verifyNoInteractions(notificationService);
  }

  @Test
  void getInvoiceShouldThrowExceptionIfDoesNotExist() {
    when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.empty());

    Throwable error = catchThrowable(() -> invoiceService.getInvoice(INVOICE_ID));

    assertThat(error)
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Invoice %d not found", INVOICE_ID);
  }

  @Test
  void getInvoiceShouldReturnInvoiceIfFound() {
    var expectedInvoice = givenOriginalInvoiceFoundWithStatus(InvoiceStatus.generated());

    var result = invoiceService.getInvoice(INVOICE_ID);

    assertThat(result).isEqualTo(expectedInvoice);
  }

  private Request givenRequestFound() {
    var request = Request.builder().id(10).build();
    when(requestFinder.getRequestById(request.getId())).thenReturn(request);
    return request;
  }

  private Invoice givenOriginalInvoiceFoundWithStatus(InvoiceStatus status) {
    var originalInvoice =
        Invoice.builder().status(status).statusChangedAt(ZonedDateTime.now().minusDays(2)).build();
    when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(originalInvoice));
    return originalInvoice;
  }

  private Invoice givenCreatedInvoiceReturnedFor(Request request, Invoice invoice) {
    var savedInvoice = Invoice.builder().status(invoice.getStatus()).build();
    when(transaction.createInvoiceAgainstRequest(request)).thenReturn(savedInvoice);
    return savedInvoice;
  }

  private Invoice givenSavedInvoiceReturnedFor(Invoice invoice) {
    var savedInvoice = Invoice.builder().status(invoice.getStatus()).build();
    when(transaction.updateInvoice(invoice)).thenReturn(savedInvoice);
    return savedInvoice;
  }

  private ZonedDateTime givenTimeNow() {
    ZonedDateTime now = ZonedDateTime.now();
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(clock.instant()).thenReturn(now.toInstant());
    return now;
  }
}

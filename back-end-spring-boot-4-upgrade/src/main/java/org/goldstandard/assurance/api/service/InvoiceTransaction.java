package org.goldstandard.assurance.api.service;

import static org.goldstandard.assurance.api.service.AuditEventSubject.INVOICE_CREATE;
import static org.goldstandard.assurance.api.service.AuditEventSubject.INVOICE_UPDATE;
import static org.goldstandard.assurance.api.service.AuditService.AUDIT_INFO_INVOICE_ID;
import static org.goldstandard.assurance.api.service.AuditService.AUDIT_INFO_REQUEST_ID;

import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Invoice;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.repository.InvoiceRepository;
import org.goldstandard.assurance.api.repository.RequestRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class InvoiceTransaction {

  private final InvoiceRepository invoiceRepository;
  private final RequestRepository requestRepository;
  private final AuditService auditService;

  @Transactional
  public Invoice createInvoiceAgainstRequest(Request request) {
    var savedRequest = requestRepository.save(request);
    var savedInvoice = savedRequest.getInvoice();
    auditService.auditEvent(
        INVOICE_CREATE, createInvoiceAuditInfo(savedInvoice.getId(), request.getId()));
    return savedInvoice;
  }

  @Transactional
  public Invoice updateInvoice(Invoice invoice) {
    var updatedInvoice = invoiceRepository.save(invoice);
    auditService.auditEvent(INVOICE_UPDATE, invoiceIdAuditInfo(updatedInvoice.getId()));
    return updatedInvoice;
  }

  private static Map<String, Object> createInvoiceAuditInfo(Integer auditId, Integer requestId) {
    Map<String, Object> info = invoiceIdAuditInfo(auditId);
    Optional.ofNullable(requestId).ifPresent(id -> info.put(AUDIT_INFO_REQUEST_ID, id));
    return info;
  }

  private static Map<String, Object> invoiceIdAuditInfo(Integer invoiceID) {
    Map<String, Object> info = new HashMap<>();
    Optional.ofNullable(invoiceID).ifPresent(id -> info.put(AUDIT_INFO_INVOICE_ID, id));
    return info;
  }
}

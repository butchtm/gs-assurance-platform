package org.goldstandard.assurance.api.controller;

import org.goldstandard.assurance.api.dto.InvoiceCreateDTO;
import org.goldstandard.assurance.api.dto.InvoiceDTO;
import org.goldstandard.assurance.api.dto.InvoicePatchDTO;
import org.goldstandard.assurance.api.entity.Invoice;
import org.goldstandard.assurance.api.entity.lookup.InvoiceStatus;
import org.springframework.stereotype.Component;

@Component
public class InvoiceConverter {

  public InvoiceDTO toDto(Invoice invoice) {
    return InvoiceDTO.builder()
        .id(invoice.getId())
        .invoiceStatusId(invoice.getStatus().getId())
        .statusChangedAt(invoice.getStatusChangedAt())
        .amount(invoice.getAmount())
        .build();
  }

  public Invoice toInvoice(InvoiceCreateDTO dto) {
    return Invoice.builder()
        .status(toStatus(dto.getInvoiceStatusId()))
        .amount(dto.getAmount())
        .build();
  }

  public Invoice toInvoice(InvoicePatchDTO dto) {
    return Invoice.builder()
        .status(toStatus(dto.getInvoiceStatusId()))
        .amount(dto.getAmount())
        .build();
  }

  private static InvoiceStatus toStatus(int statusId) {
    return new InvoiceStatus(statusId);
  }
}

package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.FINANCE_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.SUPPORT_ROLE;

import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.goldstandard.assurance.api.dto.InvoiceCreateDTO;
import org.goldstandard.assurance.api.dto.InvoiceDTO;
import org.goldstandard.assurance.api.dto.InvoicePatchDTO;
import org.goldstandard.assurance.api.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("invoices")
public class InvoiceController {

  @Autowired private InvoiceService invoiceService;

  @Autowired private InvoiceConverter converter;

  @RolesAllowed({ADMIN_ROLE, FINANCE_ROLE})
  @Transactional
  @PostMapping
  public ResponseEntity<InvoiceDTO> createInvoice(@Valid @RequestBody InvoiceCreateDTO dto) {
    var newInvoice = invoiceService.createInvoice(dto.getRequestId(), converter.toInvoice(dto));
    return ResponseEntity.ok(converter.toDto(newInvoice));
  }

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE, FINANCE_ROLE, SUPPORT_ROLE})
  @GetMapping("{invoiceId}")
  public ResponseEntity<InvoiceDTO> getInvoice(@PathVariable Integer invoiceId) {
    var invoice = invoiceService.getInvoice(invoiceId);
    return ResponseEntity.ok(converter.toDto(invoice));
  }

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE, FINANCE_ROLE})
  @Transactional
  @PatchMapping("{invoiceId}")
  public ResponseEntity<InvoiceDTO> patchInvoice(
      @PathVariable Integer invoiceId, @Valid @RequestBody InvoicePatchDTO dto) {
    var updatedInvoice = invoiceService.updateInvoice(invoiceId, converter.toInvoice(dto));
    return ResponseEntity.ok(converter.toDto(updatedInvoice));
  }
}

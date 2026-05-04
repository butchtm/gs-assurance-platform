package org.goldstandard.assurance.api;

import static java.time.temporal.ChronoUnit.MILLIS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.Assertions.within;
import static org.goldstandard.assurance.api.populator.RequestPopulator.TEST_INVOICE_FIXED_DATE;
import static org.goldstandard.assurance.api.populator.RequestPopulator.TEST_INVOICE_STATUS;
import static org.goldstandard.assurance.api.service.AuditEventSubject.INVOICE_CREATE;
import static org.goldstandard.assurance.api.service.AuditEventSubject.INVOICE_UPDATE;
import static org.goldstandard.assurance.api.service.AuditService.AUDIT_INFO_INVOICE_ID;
import static org.goldstandard.assurance.api.service.AuditService.AUDIT_INFO_REQUEST_ID;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.FINANCE_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REGISTRY_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REVIEWER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.TAC_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.VVB_ROLE;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Comparator;
import org.goldstandard.assurance.api.dto.InvoiceCreateDTO;
import org.goldstandard.assurance.api.dto.InvoiceDTO;
import org.goldstandard.assurance.api.dto.InvoicePatchDTO;
import org.goldstandard.assurance.api.entity.AuditEvent;
import org.goldstandard.assurance.api.entity.Invoice;
import org.goldstandard.assurance.api.entity.lookup.InvoiceStatus;
import org.goldstandard.assurance.api.helper.RequestHelper;
import org.goldstandard.assurance.api.repository.AuditRepository;
import org.goldstandard.assurance.api.repository.InvoiceRepository;
import org.goldstandard.assurance.api.repository.RequestRepository;
import org.goldstandard.assurance.api.service.AuditEventSubject;
import org.goldstandard.assurance.api.util.JsonConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Import(RequestHelper.class)
class InvoiceApiIntegrationTest extends AbstractApiTest {

  private static final int EXISTING_INVOICE_ID = 1;
  private static final int TEST_NON_EXISTENT_INVOICE_ID = 424242;
  private static final int TEST_NON_EXISTENT_REQUEST_ID = 989898;
  private static final InvoiceStatus TEST_INVOICE_STATUS_UPDATE = new InvoiceStatus(3);

  @Autowired RequestHelper requestHelper;

  @Autowired InvoiceRepository invoiceRepository;

  @Autowired RequestRepository requestRepository;

  @Autowired AuditRepository auditRepository;

  @Autowired ApiClient client;

  @Autowired JsonConverter jsonConverter;

  @Test
  void getInvoiceShouldReturnUnauthorizedIfNotAuthenticated() {
    ResponseEntity<InvoiceDTO> response = client.getInvoiceUnauthenticated(EXISTING_INVOICE_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void getInvoiceShouldReturnNotFoundIfInvoiceDoesNotExist() {
    ResponseEntity<InvoiceDTO> response =
        client.getInvoice(ADMIN_ROLE, TEST_NON_EXISTENT_INVOICE_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {GS_REGISTRY_ROLE, GS_REVIEWER_ROLE, PROJECT_DEVELOPER_ROLE, VVB_ROLE, TAC_ROLE})
  void getInvoiceShouldReturnForbiddenForNonPermittedRoles(String role) {
    ResponseEntity<InvoiceDTO> response = client.getInvoice(role, EXISTING_INVOICE_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @ParameterizedTest
  @ValueSource(strings = {ADMIN_ROLE, ARM_TEAM_ROLE, FINANCE_ROLE})
  void getInvoiceShouldReturnInvoiceForPermittedRoles(String role) {
    ResponseEntity<InvoiceDTO> response = client.getInvoice(role, EXISTING_INVOICE_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    var invoice = response.getBody();
    assertThat(invoice.getInvoiceStatusId()).isEqualTo(TEST_INVOICE_STATUS.getId());
    assertThat(invoice.getStatusChangedAt()).isEqualTo(TEST_INVOICE_FIXED_DATE);
    assertThat(invoice.getAmount()).isEqualTo(new BigDecimal("300.00"));
  }

  @Test
  void createInvoiceShouldReturnUnauthorizedIfNotAuthenticated() {
    var request = requestHelper.createTestRequest(1);
    try {
      var createDto = toInvoiceCreateDto(request.getId());

      ResponseEntity<InvoiceDTO> response = client.createInvoiceUnauthenticated(createDto);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    } finally {
      requestHelper.deleteTestRequest(request);
    }
  }

  @Test
  void createInvoiceShouldReturnNotFoundIfRequestDoesNotExist() {
    var createDto = toInvoiceCreateDto(TEST_NON_EXISTENT_REQUEST_ID);

    var response = client.createInvoice(ADMIN_ROLE, createDto);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        GS_REGISTRY_ROLE,
        GS_REVIEWER_ROLE,
        ARM_TEAM_ROLE,
        PROJECT_DEVELOPER_ROLE,
        VVB_ROLE,
        TAC_ROLE
      })
  void createInvoiceShouldReturnForbiddenForNonPermittedRoles(String role) {
    var request = requestHelper.createTestRequest(1);
    try {
      var createDto = toInvoiceCreateDto(request.getId());

      var response = client.createInvoice(role, createDto);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    } finally {
      requestHelper.deleteTestRequest(request);
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {ADMIN_ROLE, FINANCE_ROLE})
  void shouldCreateInvoiceForPermittedRoles(String role) {
    var request = requestHelper.createTestRequest(1);
    try {
      var createDto = toInvoiceCreateDto(request.getId());

      var response = client.createInvoice(role, createDto);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      var invoice = response.getBody();
      assertThat(invoice.getId()).isPositive();
      assertThat(invoice.getInvoiceStatusId()).isEqualTo(createDto.getInvoiceStatusId());
      assertThat(invoice.getStatusChangedAt())
          .isBefore(ZonedDateTime.now())
          .isCloseTo(ZonedDateTime.now(), within(500, MILLIS));
      assertThat(invoice.getAmount()).isEqualTo(createDto.getAmount());

      var updatedRequest = requestRepository.findById(request.getId()).orElseThrow();
      assertThat(updatedRequest.getInvoice().getId()).isEqualTo(invoice.getId());
      var event = getMostRecentAuditEvent(INVOICE_CREATE, invoice.getId());
      assertThat(jsonConverter.toMap(event.getAdditionalInformation()))
          .hasSize(2)
          .contains(
              entry(AUDIT_INFO_INVOICE_ID, invoice.getId()),
              entry(AUDIT_INFO_REQUEST_ID, request.getId()));
    } finally {
      requestHelper.deleteTestRequest(request);
    }
  }

  @Test
  void patchInvoiceShouldReturnUnauthorizedIfNotAuthenticated() {
    var invoiceToPatch = createTestInvoice();
    try {
      var patchDto = buildInvoicePatchDto();

      ResponseEntity<InvoiceDTO> response =
          client.patchInvoiceUnauthenticated(invoiceToPatch.getId(), patchDto);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    } finally {
      deleteTestInvoice(invoiceToPatch);
    }
  }

  @Test
  void patchInvoiceShouldReturnNotFoundIfInvoiceDoesNotExist() {
    var patchDto = buildInvoicePatchDto();

    var response = client.patchInvoice(ADMIN_ROLE, TEST_NON_EXISTENT_INVOICE_ID, patchDto);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {GS_REGISTRY_ROLE, GS_REVIEWER_ROLE, PROJECT_DEVELOPER_ROLE, VVB_ROLE, TAC_ROLE})
  void patchInvoiceShouldReturnForbiddenForNonPermittedRoles(String role) {
    var invoiceToPatch = createTestInvoice();
    try {
      var patchDto = buildInvoicePatchDto();

      var response = client.patchInvoice(role, invoiceToPatch.getId(), patchDto);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    } finally {
      deleteTestInvoice(invoiceToPatch);
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {ADMIN_ROLE, ARM_TEAM_ROLE, FINANCE_ROLE})
  void shouldPatchInvoiceForPermittedRoles(String role) {
    var invoiceToPatch = createTestInvoice();
    try {
      var patchDto = buildInvoicePatchDto();

      var response = client.patchInvoice(role, invoiceToPatch.getId(), patchDto);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      var invoice = response.getBody();
      assertThat(invoice.getId()).isPositive();
      assertThat(invoice.getInvoiceStatusId()).isEqualTo(patchDto.getInvoiceStatusId());
      assertThat(invoice.getStatusChangedAt()).isAfter(invoiceToPatch.getStatusChangedAt());
      assertThat(invoice.getAmount()).isEqualTo(patchDto.getAmount());
      var event = getMostRecentAuditEvent(INVOICE_UPDATE, invoiceToPatch.getId());
      assertThat(jsonConverter.toMap(event.getAdditionalInformation()))
          .hasSize(1)
          .contains(entry(AUDIT_INFO_INVOICE_ID, invoiceToPatch.getId()));
    } finally {
      deleteTestInvoice(invoiceToPatch);
    }
  }

  private Invoice createTestInvoice() {
    var invoice =
        Invoice.builder()
            .status(TEST_INVOICE_STATUS)
            .statusChangedAt(ZonedDateTime.now().minusHours(1).withNano(0))
            .amount(BigDecimal.ZERO)
            .build();
    return invoiceRepository.save(invoice);
  }

  private void deleteTestInvoice(Invoice invoice) {
    invoiceRepository.delete(invoice);
  }

  private AuditEvent getMostRecentAuditEvent(AuditEventSubject subject, Integer invoiceId) {
    return auditRepository.findAllByEventSubject(subject.name()).stream()
        .filter(
            event ->
                jsonConverter
                    .toMap(event.getAdditionalInformation())
                    .get(AUDIT_INFO_INVOICE_ID)
                    .equals(invoiceId))
        .max(Comparator.comparing(AuditEvent::getEventTimestamp))
        .orElseThrow();
  }

  private static InvoiceCreateDTO toInvoiceCreateDto(Integer requestId) {
    return InvoiceCreateDTO.builder()
        .invoiceStatusId(TEST_INVOICE_STATUS.getId())
        .requestId(requestId)
        .amount(new BigDecimal(100))
        .build();
  }

  private static InvoicePatchDTO buildInvoicePatchDto() {
    return InvoicePatchDTO.builder()
        .invoiceStatusId(TEST_INVOICE_STATUS_UPDATE.getId())
        .amount(new BigDecimal(150))
        .build();
  }
}

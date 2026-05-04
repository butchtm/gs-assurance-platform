package org.goldstandard.assurance.api.service;

import java.time.ZonedDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.AuditEvent;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.repository.AuditRepository;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.goldstandard.assurance.api.util.JsonConverter;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

  public static final String AUDIT_INFO_REQUEST_ID = "requestId";
  public static final String AUDIT_INFO_INVOICE_ID = "invoiceId";
  public static final String AUDIT_INFO_NOTIFICATION_TYPE = "notificationType";
  public static final String AUDIT_INFO_NOTIFICATION_ID = "notificationId";

  private final AuditRepository repository;
  private final JsonConverter jsonConverter;
  private final AuthenticatedUserSupplier userSupplier;

  public void auditEvent(AuditEventSubject subject, Object additionalInformation) {
    var user = userSupplier.getAuthenticatedUser().orElse(null);
    auditEvent(subject, additionalInformation, user);
  }

  public void auditEvent(
      AuditEventSubject subject, Object additionalInformation, User authenticatedUser) {
    if (Objects.nonNull(authenticatedUser)) {
      saveUserAuditEvent(subject, additionalInformation, authenticatedUser);
    } else {
      saveAnonymousAuditEvent(subject, additionalInformation);
    }
  }

  private void saveAnonymousAuditEvent(AuditEventSubject subject, Object additionalInformation) {
    var event = new AuditEvent();
    populateAuditEvent(event, subject, additionalInformation);
    event.setAnonymous(true);
    saveAndLog(event);
  }

  private void saveUserAuditEvent(
      AuditEventSubject subject, Object additionalInformation, User user) {
    var event = new AuditEvent();
    event.setIdpUserId(user.getIdpUserId());
    populateAuditEvent(event, subject, additionalInformation);
    saveAndLog(event);
  }

  private void populateAuditEvent(
      AuditEvent event, AuditEventSubject subject, Object additionalInformation) {
    event.setEventTimestamp(ZonedDateTime.now());
    event.setEventSubject(subject.name());
    event.setAdditionalInformation(jsonConverter.toJson(additionalInformation));
  }

  private void saveAndLog(AuditEvent event) {
    AuditEvent savedEvent = repository.save(event);
    log.info("saved audit event {}", savedEvent);
  }
}

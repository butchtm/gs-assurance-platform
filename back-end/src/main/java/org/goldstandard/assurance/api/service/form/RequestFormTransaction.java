package org.goldstandard.assurance.api.service.form;

import static org.goldstandard.assurance.api.service.AuditEventSubject.REQUEST_FORM_CREATE;
import static org.goldstandard.assurance.api.service.AuditEventSubject.REQUEST_FORM_SUBMIT;
import static org.goldstandard.assurance.api.service.AuditEventSubject.REQUEST_FORM_UPDATE;

import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.RequestForm;
import org.goldstandard.assurance.api.repository.RequestFormRepository;
import org.goldstandard.assurance.api.service.AuditEventSubject;
import org.goldstandard.assurance.api.service.AuditService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RequestFormTransaction {

  private final RequestFormRepository repository;
  private final AuditService auditService;

  @Transactional
  public Integer create(RequestForm form) {
    return persist(form, REQUEST_FORM_CREATE);
  }

  @Transactional
  public void update(RequestForm form) {
    persist(form, REQUEST_FORM_UPDATE);
  }

  @Transactional
  public void submit(RequestForm form) {
    submit(List.of(form));
  }

  @Transactional
  public void submit(Collection<RequestForm> forms) {
    forms.forEach(form -> persist(form, REQUEST_FORM_SUBMIT));
  }

  private Integer persist(RequestForm form, AuditEventSubject subject) {
    var saved = repository.save(form);
    auditService.auditEvent(subject, saved);
    return saved.getId();
  }
}

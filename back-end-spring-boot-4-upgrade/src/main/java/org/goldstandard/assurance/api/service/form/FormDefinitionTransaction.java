package org.goldstandard.assurance.api.service.form;

import static org.goldstandard.assurance.api.service.AuditEventSubject.FORM_DEFINITION_CREATE;
import static org.goldstandard.assurance.api.service.AuditEventSubject.FORM_DEFINITION_UPDATE;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.FormDefinition;
import org.goldstandard.assurance.api.repository.FormDefinitionRepository;
import org.goldstandard.assurance.api.service.AuditEventSubject;
import org.goldstandard.assurance.api.service.AuditService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class FormDefinitionTransaction {

  private final FormDefinitionRepository repository;
  private final AuditService auditService;

  @Transactional
  public Integer create(FormDefinition definition) {
    return persist(definition, FORM_DEFINITION_CREATE);
  }

  @Transactional
  public void update(FormDefinition definition) {
    persist(definition, FORM_DEFINITION_UPDATE);
  }

  @Transactional
  public void publish(FormDefinition definition) {
    persist(definition, FORM_DEFINITION_UPDATE);
  }

  private Integer persist(FormDefinition definition, AuditEventSubject subject) {
    var saved = repository.save(definition);
    auditService.auditEvent(subject, saved);
    return saved.getId();
  }
}

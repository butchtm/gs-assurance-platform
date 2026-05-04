package org.goldstandard.assurance.api.service.form;

import static java.time.ZonedDateTime.now;

import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.FormDefinitionCreateDTO;
import org.goldstandard.assurance.api.entity.DigitalFormType;
import org.goldstandard.assurance.api.entity.FormDefinition;
import org.goldstandard.assurance.api.entity.FormDefinitionStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FormDefinitionFactory {

  private final FormDefinitionValidator validator;
  private final Clock clock;

  public FormDefinition create(FormDefinitionCreateDTO dto) {
    validator.validateCanBeCreated(dto.getTypeId());
    return FormDefinition.builder()
        .status(FormDefinitionStatus.draft())
        .type(new DigitalFormType(dto.getTypeId()))
        .schema(dto.getSchema())
        .createdAt(now(clock))
        .build();
  }
}

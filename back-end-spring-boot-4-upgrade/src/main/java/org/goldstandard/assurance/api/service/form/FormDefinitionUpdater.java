package org.goldstandard.assurance.api.service.form;

import static java.time.ZonedDateTime.now;

import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.FormDefinitionUpdateDTO;
import org.goldstandard.assurance.api.entity.FormDefinition;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FormDefinitionUpdater {

  private final FormDefinitionValidator validator;
  private final Clock clock;

  public FormDefinition update(FormDefinition existing, FormDefinitionUpdateDTO dto) {
    validator.validateCanBeUpdated(existing);
    return existing.withSchema(dto.getSchema(), now(clock));
  }
}

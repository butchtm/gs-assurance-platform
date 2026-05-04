package org.goldstandard.assurance.api.service.form;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.FormDefinition;
import org.goldstandard.assurance.api.exception.BadRequestException;
import org.goldstandard.assurance.api.repository.FormDefinitionRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FormDefinitionValidator {

  private final FormDefinitionRepository repository;

  public void validateCanBeCreated(Integer formTypeId) {
    if (repository.draftExistsByTypeId(formTypeId)) {
      throw new BadRequestException(
          "A draft form definition already exists for form type %d", formTypeId);
    }
  }

  public void validateCanBeUpdated(FormDefinition definition) {
    if (definition.isPublished()) {
      throw new BadRequestException(
          "Form definition %d is published so cannot be updated", definition.getId());
    }
  }

  public void validateCanBePublished(FormDefinition definition) {
    if (definition.isPublished()) {
      throw new BadRequestException("Form definition %s is already published", definition.getId());
    }
  }
}

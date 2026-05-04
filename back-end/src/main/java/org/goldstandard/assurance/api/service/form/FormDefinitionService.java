package org.goldstandard.assurance.api.service.form;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.converter.FormDefinitionConverter;
import org.goldstandard.assurance.api.dto.FormDefinitionCreateDTO;
import org.goldstandard.assurance.api.dto.FormDefinitionDTO;
import org.goldstandard.assurance.api.dto.FormDefinitionPublishDTO;
import org.goldstandard.assurance.api.dto.FormDefinitionUpdateDTO;
import org.goldstandard.assurance.api.repository.FormDefinitionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FormDefinitionService {

  private final FormDefinitionTransaction transaction;
  private final FormDefinitionFactory factory;
  private final FormDefinitionUpdater updater;
  private final FormDefinitionRepository repository;
  private final FormDefinitionPublisher publisher;
  private final FormDefinitionConverter converter;

  public Integer createFormDefinition(FormDefinitionCreateDTO dto) {
    var definition = factory.create(dto);
    return transaction.create(definition);
  }

  public void updateFormDefinition(Integer id, FormDefinitionUpdateDTO dto) {
    var existing = repository.forceFindById(id);
    var updated = updater.update(existing, dto);
    transaction.update(updated);
  }

  public FormDefinitionDTO getFormDefinition(Integer id) {
    var definition = repository.forceFindById(id);
    return converter.toDTO(definition);
  }

  public void publishFormDefinition(Integer id, FormDefinitionPublishDTO dto) {
    var definition = repository.forceFindById(id);
    var published = publisher.publish(definition, dto);
    transaction.publish(published);
  }
}

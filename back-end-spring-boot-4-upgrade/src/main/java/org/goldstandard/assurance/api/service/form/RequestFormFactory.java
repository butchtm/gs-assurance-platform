package org.goldstandard.assurance.api.service.form;

import static java.time.ZonedDateTime.now;

import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.RequestFormCreateDTO;
import org.goldstandard.assurance.api.entity.FormDefinition;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestForm;
import org.goldstandard.assurance.api.entity.RequestFormStatus;
import org.goldstandard.assurance.api.repository.FormDefinitionRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestFormFactory {

  private final RequestFormValidator validator;
  private final FormDefinitionRepository definitionRepository;
  private final RequestFormDataFactory dataFactory;
  private final Clock clock;

  public RequestForm create(Request request, RequestFormCreateDTO dto) {
    var requestId = request.getId();
    validator.validateCanBeCreated(requestId, dto.getTypeId());
    var definition = getLatestPublishedFormDefinition(dto);
    return RequestForm.builder()
        .requestId(requestId)
        .definition(definition)
        .data(dataFactory.build(request, definition.getSchema()))
        .status(RequestFormStatus.draft())
        .createdAt(now(clock))
        .build();
  }

  private FormDefinition getLatestPublishedFormDefinition(RequestFormCreateDTO dto) {
    return definitionRepository.forceFindLatestPublishedVersion(dto.getTypeId());
  }
}

package org.goldstandard.assurance.api.service.form;

import static java.time.ZonedDateTime.now;

import java.time.Clock;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.FormDefinitionPublishDTO;
import org.goldstandard.assurance.api.entity.FormDefinition;
import org.goldstandard.assurance.api.entity.FormDefinitionStatus;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

@Component
@RequiredArgsConstructor
public class FormDefinitionPublisher {

  private final FormDefinitionValidator validator;
  private final Clock clock;
  private final FormDefinitionVersionCalculator versionCalculator;

  public FormDefinition publish(FormDefinition existing, FormDefinitionPublishDTO dto) {
    validator.validateCanBePublished(existing);
    var now = now(clock);
    var builder =
        existing.toBuilder()
            .version(versionCalculator.calculate(existing.getType(), dto))
            .status(FormDefinitionStatus.published())
            .publishedAt(now);
    var schema = dto.getSchema();
    if (isEmpty(schema)) {
      return builder.build();
    }
    return builder.schema(schema).updatedAt(now).build();
  }

  private boolean isEmpty(JsonNode node) {
    return Objects.isNull(node) || node.isEmpty();
  }
}

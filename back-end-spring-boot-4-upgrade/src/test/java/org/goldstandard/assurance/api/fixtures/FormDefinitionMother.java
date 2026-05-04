package org.goldstandard.assurance.api.fixtures;

import static org.goldstandard.assurance.api.fixtures.JsonFactory.objectNode;
import static org.goldstandard.assurance.api.fixtures.JsonFactory.toJsonArray;
import static org.goldstandard.assurance.api.fixtures.JsonFactory.toJsonNode;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.DigitalFormType;
import org.goldstandard.assurance.api.entity.FormDefinition;
import org.goldstandard.assurance.api.entity.FormDefinition.FormDefinitionBuilder;
import org.goldstandard.assurance.api.entity.FormDefinitionStatus;
import tools.jackson.databind.JsonNode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FormDefinitionMother {

  public static FormDefinition build() {
    return builder().build();
  }

  public static FormDefinition withStepsArraySchema() {
    return withSchema(stepsArraySchema());
  }

  public static FormDefinition withSchema(JsonNode jsonNode) {
    return builder().schema(jsonNode).build();
  }

  public static FormDefinition withArraySchema() {
    return withArraySchema(2);
  }

  public static FormDefinition withArraySchema(int size) {
    return builder().schema(arraySchema(size)).build();
  }

  public static FormDefinitionBuilder builder() {
    var publishedAt = ZonedDateTimeMother.fixed();
    return FormDefinition.builder()
        .version(1.0)
        .type(DigitalFormType.annualReport())
        .schema(defaultSchema())
        .status(FormDefinitionStatus.published())
        .createdAt(publishedAt.minusDays(2))
        .updatedAt(publishedAt.minusDays(1))
        .publishedAt(publishedAt);
  }

  public static JsonNode stepsArraySchema() {
    var array = toJsonArray(defaultSchema(), 2);
    var schema = objectNode();
    return schema.set("steps", array);
  }

  public static JsonNode arraySchema(int size) {
    return toJsonArray(defaultSchema(), size);
  }

  private static JsonNode defaultSchema() {
    return toJsonNode(
        """
            {
                "type": "object",
                "properties": {
                  "field": {
                    "type": "string"
                  }
                },
                "required": ["field"]
            }
            """);
  }
}

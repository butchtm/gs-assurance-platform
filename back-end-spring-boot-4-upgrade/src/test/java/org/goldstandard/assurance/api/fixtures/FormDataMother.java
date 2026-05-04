package org.goldstandard.assurance.api.fixtures;

import static org.goldstandard.assurance.api.fixtures.JsonFactory.toJsonArray;
import static org.goldstandard.assurance.api.fixtures.JsonFactory.toJsonNode;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FormDataMother {

  public static JsonNode emptyObject() {
    return toJsonNode("{}");
  }

  public static ArrayNode buildArray() {
    return buildArray(2);
  }

  public static ArrayNode buildArray(int size) {
    return toJsonArray(build(), size);
  }

  public static JsonNode build() {
    return toJsonNode(
        """
            {
                "field": "some value"
            }
            """);
  }
}

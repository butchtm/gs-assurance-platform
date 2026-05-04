package org.goldstandard.assurance.api.fixtures;

import static org.goldstandard.assurance.api.fixtures.JsonFactory.toJsonArray;
import static org.goldstandard.assurance.api.fixtures.JsonFactory.toJsonNode;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FormDataMother {

  public static JsonNode emptyObject() {
    return toJsonNode("{}");
  }

  public static JsonNode buildArray() {
    return buildArray(2);
  }

  public static JsonNode buildArray(int size) {
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

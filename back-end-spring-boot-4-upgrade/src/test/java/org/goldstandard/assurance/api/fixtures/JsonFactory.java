package org.goldstandard.assurance.api.fixtures;

import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.exception.JsonConversionException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonFactory {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static ArrayNode toJsonArray(JsonNode item, int size) {
    var items = IntStream.range(0, size).mapToObj(i -> item).toList();
    return toJsonArray(items);
  }

  public static ArrayNode toJsonArray(JsonNode... items) {
    return toJsonArray(List.of(items));
  }

  public static ArrayNode toJsonArray(Collection<JsonNode> items) {
    var array = MAPPER.createArrayNode();
    items.forEach(array::add);
    return array;
  }

  public static ObjectNode objectNode() {
    return MAPPER.createObjectNode();
  }

  public static JsonNode toJsonNode(String json) {
    try {
      return MAPPER.readTree(json);
    } catch (JacksonException e) {
      throw new JsonConversionException(e);
    }
  }
}

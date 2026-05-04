package org.goldstandard.assurance.api.fixtures;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonFactory {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static JsonNode toJsonArray(JsonNode item, int size) {
    var items = IntStream.range(0, size).mapToObj(i -> item).toList();
    return toJsonArray(items);
  }

  public static JsonNode toJsonArray(JsonNode... items) {
    return toJsonArray(List.of(items));
  }

  public static JsonNode toJsonArray(Collection<JsonNode> items) {
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
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}

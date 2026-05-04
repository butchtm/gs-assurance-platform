package org.goldstandard.assurance.api.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.exception.JsonConversionException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.JsonNodeType;

@Slf4j
@Converter
public class JsonNodeConverter implements AttributeConverter<JsonNode, String> {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(JsonNode node) {
    return Optional.ofNullable(node).map(this::toString).orElse(null);
  }

  @Override
  public JsonNode convertToEntityAttribute(String json) {
    return Optional.ofNullable(json).map(this::toNode).orElse(null);
  }

  private String toString(JsonNode node) {
    try {
      return MAPPER.writeValueAsString(node);
    } catch (JacksonException e) {
      throw new JsonConversionException(e);
    }
  }

  private JsonNode toNode(String json) {
    try {
      var node = MAPPER.readTree(json);
      // H2 compatibility: if JSON was stored as quoted string
      if (node.getNodeType().equals(JsonNodeType.STRING)) {
        return MAPPER.readTree(node.stringValue());
      }
      return node;
    } catch (JacksonException e) {
      throw new JsonConversionException(e);
    }
  }
}

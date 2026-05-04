package org.goldstandard.assurance.api.util;

import java.io.InputStream;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.exception.JsonConversionException;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class JsonConverter {

  private final ObjectMapper mapper;

  public String toJson(Object object) {
    try {
      return mapper.writeValueAsString(object);
    } catch (JacksonException e) {
      throw new JsonConversionException(e);
    }
  }

  public Map<String, Object> toMap(String json) {
    try {
      return mapper.readValue(json, new TypeReference<>() {});
    } catch (JacksonException e) {
      throw new JsonConversionException(e);
    }
  }

  public <O> O toObject(InputStream inputStream, Class<O> type) {
    try {
      return mapper.readValue(inputStream, type);
    } catch (JacksonException e) {
      throw new JsonConversionException(e);
    }
  }
}

package org.goldstandard.assurance.api.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.exception.JsonConversionException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JsonConverter {

  private final ObjectMapper mapper;

  public String toJson(Object object) {
    try {
      return mapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new JsonConversionException(e);
    }
  }

  public Map<String, Object> toMap(String json) {
    try {
      return mapper.readValue(json, new TypeReference<>() {});
    } catch (JsonProcessingException e) {
      throw new JsonConversionException(e);
    }
  }

  public <O> O toObject(InputStream inputStream, Class<O> type) {
    try {
      return mapper.readValue(inputStream, type);
    } catch (IOException e) {
      throw new JsonConversionException(e);
    }
  }
}

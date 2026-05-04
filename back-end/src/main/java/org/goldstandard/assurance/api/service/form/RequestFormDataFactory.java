package org.goldstandard.assurance.api.service.form;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Request;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Builder
public class RequestFormDataFactory {

  private final FormFieldsFactory formFieldsFactory;
  private final FormFieldLocationFinder fieldLocationFinder;
  private final RequestFormDataPopulator dataPopulator;

  public ArrayNode build(Request request, JsonNode schema) {
    var allFields = formFieldsFactory.buildAllFormFields();
    var data = dataPopulator.buildEmptyData();
    for (var fields : allFields) {
      data = populateData(request, schema, data, fields);
    }
    return data;
  }

  private ArrayNode populateData(
      Request request, JsonNode schema, ArrayNode data, FormFields fields) {
    var location = fieldLocationFinder.findLocation(fields, schema);
    log.debug("found location {} in schema {}", location, schema);
    if (location.isEmpty()) {
      return data;
    }
    return dataPopulator.populateData(data, location.get(), request);
  }
}

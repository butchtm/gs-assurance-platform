package org.goldstandard.assurance.api.service.form;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Request;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestFormDataPopulator {

  private final ObjectMapper mapper;

  public ArrayNode buildEmptyData() {
    return mapper.createArrayNode();
  }

  public ArrayNode populateData(ArrayNode steps, FormFieldLocation location, Request request) {
    addStepsIfRequired(steps, location);
    var target = (ObjectNode) steps.get(location.stepIndex());
    location.populateData(target, request);
    return steps;
  }

  private void addStepsIfRequired(ArrayNode steps, FormFieldLocation location) {
    while (steps.size() <= location.stepIndex()) {
      steps.addObject();
    }
  }
}

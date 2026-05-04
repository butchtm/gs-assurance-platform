package org.goldstandard.assurance.api.service.form;

import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Request;
import org.springframework.stereotype.Component;
import tools.jackson.databind.node.ObjectNode;

@Slf4j
@Component
public class RequestFormFieldNoopDataPopulator implements RequestFormFieldDataPopulator {

  @Override
  public void populateData(ObjectNode data, FormFieldLocation location, Request request) {
    log.info("no op request form field data populator called on data {}", data.toString());
  }
}

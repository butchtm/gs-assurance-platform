package org.goldstandard.assurance.api.service.form;

import org.goldstandard.assurance.api.entity.Request;
import tools.jackson.databind.node.ObjectNode;

public interface RequestFormFieldDataPopulator {

  void populateData(ObjectNode data, FormFieldLocation location, Request request);
}

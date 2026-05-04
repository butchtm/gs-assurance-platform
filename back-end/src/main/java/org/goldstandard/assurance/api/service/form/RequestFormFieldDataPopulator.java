package org.goldstandard.assurance.api.service.form;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.goldstandard.assurance.api.entity.Request;

public interface RequestFormFieldDataPopulator {

  void populateData(ObjectNode data, FormFieldLocation location, Request request);
}

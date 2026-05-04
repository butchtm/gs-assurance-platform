package org.goldstandard.assurance.api.service.form;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Collection;
import org.goldstandard.assurance.api.entity.Request;

public record FormFieldLocation(FormFields fields, int stepIndex, Collection<String> childPaths) {

  public boolean hasStepIndex() {
    return stepIndex > -1;
  }

  public String parentField() {
    return fields.parent();
  }

  public void populateData(ObjectNode input, Request request) {
    var populator = fields.dataPopulator();
    populator.populateData(input, this, request);
  }

  public Collection<String> getChildFields() {
    return childPaths.stream().map(this::toField).toList();
  }

  private String toField(String path) {
    return path.substring(path.lastIndexOf('.') + 1);
  }
}

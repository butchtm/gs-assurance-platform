package org.goldstandard.assurance.api.service.form;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class FormFieldLocationFinder {

  private static final String PROPERTIES = "properties";

  public Optional<FormFieldLocation> findLocation(FormFields fields, JsonNode schema) {
    var paths = findPaths(fields, schema);
    if (!paths.isEmpty()) {
      return Optional.of(new FormFieldLocation(fields, -1, paths));
    }
    return findLocationFromSteps(fields, schema);
  }

  private Optional<FormFieldLocation> findLocationFromSteps(FormFields fields, JsonNode schema) {
    var steps = schema.path("steps");
    if (!steps.isArray()) {
      return Optional.empty();
    }
    for (int i = 0; i < steps.size(); i++) {
      var step = steps.get(i);
      var stepSchema = step.path("schema");
      if (stepSchema.isObject()) {
        var paths = findPathsFromStep(fields, i, stepSchema);
        if (!paths.isEmpty()) {
          return Optional.of(new FormFieldLocation(fields, i, paths));
        }
      }
    }
    return Optional.empty();
  }

  private Collection<String> findPaths(FormFields fields, JsonNode schema) {
    var result = new LinkedHashSet<String>();
    collectFromSchemaRoot(fields, schema, "", result);
    return result;
  }

  private Collection<String> findPathsFromStep(FormFields fields, int i, JsonNode step) {
    var result = new LinkedHashSet<String>();
    collectFromSchemaRoot(fields, step, toStepBasePath(i), result);
    return result;
  }

  private String toStepBasePath(int i) {
    return String.format("steps[%d].", i);
  }

  private void collectFromSchemaRoot(
      FormFields fields, JsonNode schemaRoot, String basePath, Set<String> result) {
    var parentNode = findParentNode(fields, schemaRoot);
    if (!parentNode.isObject()) {
      return;
    }
    var properties = findProperties(parentNode);
    if (!properties.isObject()) {
      return;
    }
    addChildrenIfPresent(fields, basePath, properties, result);
  }

  private JsonNode findParentNode(FormFields fields, JsonNode schemaRoot) {
    if (fields.hasParent()) {
      return schemaRoot.path(PROPERTIES).path(fields.parent());
    }
    return schemaRoot;
  }

  private JsonNode findProperties(JsonNode parentNode) {
    if (!parentNode.has("type")) {
      return MissingNode.getInstance();
    }
    var type = parentNode.get("type");
    if ("array".equals(type.asText())) {
      return parentNode.path("items").path(PROPERTIES);
    }
    return parentNode.path(PROPERTIES);
  }

  private void addChildrenIfPresent(
      FormFields fields, String basePath, JsonNode itemProperties, Set<String> result) {
    fields
        .children()
        .forEach(child -> addIfPresent(fields, basePath, itemProperties, result, child));
  }

  private void addIfPresent(
      FormFields fields,
      String basePath,
      JsonNode itemProperties,
      Set<String> result,
      String childField) {
    if (itemProperties.has(childField)) {
      var path = toPath(fields, basePath, childField);
      result.add(path);
    }
  }

  private String toPath(FormFields fields, String basePath, String childField) {
    if (fields.hasParent()) {
      return String.format("%s%s.%s", basePath, fields.parent(), childField);
    }
    return String.format("%s%s", basePath, childField);
  }
}

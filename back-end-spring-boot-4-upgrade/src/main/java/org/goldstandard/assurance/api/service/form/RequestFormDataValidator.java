package org.goldstandard.assurance.api.service.form;

import com.networknt.schema.Error;
import com.networknt.schema.Schema;
import com.networknt.schema.SchemaRegistry;
import com.networknt.schema.SpecificationVersion;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.RequestForm;
import org.goldstandard.assurance.api.exception.BadRequestException;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestFormDataValidator {

  private final SchemaRegistry schemaRegistry;

  public RequestFormDataValidator() {
    this(SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12));
  }

  public void validateDataAgainstSchema(RequestForm form) {
    var schema = form.getSchema();
    if (schema.isArray()) {
      validateDataStepsAgainstSchemaSteps((ArrayNode) schema, form.getData());
      return;
    }
    var stepsArray = findStepsArrayIfPresent(schema);
    if (stepsArray.isPresent()) {
      validateDataStepsAgainstSchemaSteps(stepsArray.get(), form.getData());
      return;
    }
    validateDataAgainstSchema(schema, form.getData());
  }

  private Optional<ArrayNode> findStepsArrayIfPresent(JsonNode node) {
    return findField(node, "steps").filter(JsonNode::isArray).map(ArrayNode.class::cast);
  }

  private void validateDataStepsAgainstSchemaSteps(ArrayNode schemaArray, JsonNode data) {
    if (!data.isArray()) {
      throw new BadRequestException("form schema is an array, form data must also be an array");
    }
    var dataArray = (ArrayNode) data;
    if (schemaArray.size() != dataArray.size()) {
      throw new BadRequestException(
          "form data array size %d must match schema array size %d",
          dataArray.size(), schemaArray.size());
    }
    validateSteps(schemaArray, dataArray);
  }

  private void validateSteps(ArrayNode schemaArray, ArrayNode dataArray) {
    for (int i = 0; i < schemaArray.size(); i++) {
      validateDataAgainstSchema(schemaArray.get(i), dataArray.get(i));
    }
  }

  private Schema toValidationSchema(JsonNode node) {
    return schemaRegistry.getSchema(toSchemaNode(node));
  }

  private JsonNode toSchemaNode(JsonNode node) {
    return findSchemaObjectIfPresent(node).orElse(node);
  }

  private Optional<JsonNode> findSchemaObjectIfPresent(JsonNode node) {
    return findField(node, "schema").filter(JsonNode::isObject);
  }

  private Optional<JsonNode> findField(JsonNode node, String fieldName) {
    if (!node.has(fieldName)) {
      return Optional.empty();
    }
    JsonNode field = node.get(fieldName);
    if (field.isMissingNode()) {
      return Optional.empty();
    }
    return Optional.of(field);
  }

  private void validateDataAgainstSchema(JsonNode jsonSchema, JsonNode data) {
    var validationSchema = toValidationSchema(jsonSchema);
    var errors = validationSchema.validate(data);
    log.info(
        "validating form data {} against form schema {} returned errors {}",
        data,
        validationSchema,
        errors);
    if (!errors.isEmpty()) {
      throw new BadRequestException(
          errors.stream().map(Error::getMessage).collect(Collectors.joining(",")));
    }
  }
}

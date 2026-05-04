package org.goldstandard.assurance.api.service.form;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.mruoc.file.FileLoader.loadContentFromClasspath;

import java.util.Set;
import org.goldstandard.assurance.api.exception.JsonConversionException;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

class FormFieldLocationFinderTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final FormFieldLocationFinder locationFinder = new FormFieldLocationFinder();

  @Test
  void shouldNotFindProjectArrayPathsInEmptySchema() {
    var fields = buildProjectFields();
    var schema = toJsonNode("{}");

    var location = locationFinder.findLocation(fields, schema);

    assertThat(location).isEmpty();
  }

  @Test
  void shouldFindProjectArrayPathsAtRoot() {
    var fields = buildProjectFields();
    var schema = loadFormSchema("schema-projects-array-at-root.json");

    var location = locationFinder.findLocation(fields, schema).orElseThrow();

    assertThat(location.hasStepIndex()).isFalse();
    assertThat(location.stepIndex()).isEqualTo(-1);
    assertThat(location.fields()).isEqualTo(fields);
    assertThat(location.childPaths()).containsExactlyInAnyOrder("projects.gsid", "projects.title");
  }

  @Test
  void shouldFindProjectArrayPathsInSteps() {
    var fields = buildProjectFields();
    var schema = loadFormSchema("schema-projects-array-in-step.json");

    var location = locationFinder.findLocation(fields, schema).orElseThrow();

    assertThat(location.hasStepIndex()).isTrue();
    assertThat(location.stepIndex()).isOne();
    assertThat(location.fields()).isEqualTo(fields);
    assertThat(location.childPaths())
        .containsExactlyInAnyOrder("steps[1].projects.gsid", "steps[1].projects.registryLink");
  }

  @Test
  void shouldFindProjectObjectPathsInExampleSchema() {
    var fields = buildProjectFields("project");
    var schema = loadFormSchema("schema-project-object-in-step.json");

    var location = locationFinder.findLocation(fields, schema).orElseThrow();

    assertThat(location.hasStepIndex()).isTrue();
    assertThat(location.stepIndex()).isZero();
    assertThat(location.fields()).isEqualTo(fields);
    assertThat(location.childPaths())
        .containsExactlyInAnyOrder(
            "steps[0].project.gsid", "steps[0].project.title", "steps[0].project.registryLink");
  }

  @Test
  void shouldFindProjectArrayPathsInExampleSchema() {
    var fields = buildProjectFields();
    var schema = loadFormSchema("schema-projects-array-example-in-step.json");

    var location = locationFinder.findLocation(fields, schema).orElseThrow();

    assertThat(location.hasStepIndex()).isTrue();
    assertThat(location.stepIndex()).isZero();
    assertThat(location.fields()).isEqualTo(fields);
    assertThat(location.childPaths())
        .containsExactlyInAnyOrder(
            "steps[0].projects.gsid", "steps[0].projects.title", "steps[0].projects.registryLink");
  }

  @Test
  void shouldFindIndividualFieldPathsInStepSchema() {
    var fields = new FormFields(Set.of("projectTitles", "projectGsids", "projectRegistryLinks"));
    var schema = loadFormSchema("schema-individual-fields-in-step.json");

    var location = locationFinder.findLocation(fields, schema).orElseThrow();

    assertThat(location.hasStepIndex()).isTrue();
    assertThat(location.stepIndex()).isZero();
    assertThat(location.fields()).isEqualTo(fields);
    assertThat(location.childPaths())
        .containsExactlyInAnyOrder(
            "steps[0].projectTitles", "steps[0].projectGsids", "steps[0].projectRegistryLinks");
  }

  private static JsonNode loadFormSchema(String filename) {
    var path = String.format("json/form/schema/%s", filename);
    var json = loadContentFromClasspath(path);
    return toJsonNode(json);
  }

  private static JsonNode toJsonNode(String json) {
    try {
      return MAPPER.readTree(json);
    } catch (JacksonException e) {
      throw new JsonConversionException(e);
    }
  }

  private static FormFields buildProjectFields() {
    return buildProjectFields("projects");
  }

  private static FormFields buildProjectFields(String parent) {
    return new FormFields(parent, Set.of("gsid", "title", "registryLink"));
  }
}

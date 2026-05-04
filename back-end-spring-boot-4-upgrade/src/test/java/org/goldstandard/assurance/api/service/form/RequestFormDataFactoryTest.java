package org.goldstandard.assurance.api.service.form;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.co.mruoc.file.FileLoader.loadContentFromClasspath;

import java.util.Collection;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.exception.JsonConversionException;
import org.goldstandard.assurance.api.fixtures.ProjectMother;
import org.goldstandard.assurance.api.fixtures.RequestMother;
import org.goldstandard.assurance.api.service.BatchRequestFinder;
import org.goldstandard.assurance.api.service.external.registry.RegistryUiLinkFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

class RequestFormDataFactoryTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private final BatchRequestFinder batchRequestFinder = mock(BatchRequestFinder.class);
  private final RegistryUiLinkFactory registryLinkFactory =
      new RegistryUiLinkFactory("http://localhost:3002");
  private final RequestFormFieldProjectDataPopulator projectPopulator =
      RequestFormFieldProjectDataPopulator.builder()
          .mapper(MAPPER)
          .batchRequestFinder(batchRequestFinder)
          .registryLinkFactory(registryLinkFactory)
          .build();
  private final RequestFormFieldOldProjectDataPopulator oldFieldPopulator =
      RequestFormFieldOldProjectDataPopulator.builder()
          .batchRequestFinder(batchRequestFinder)
          .registryLinkFactory(registryLinkFactory)
          .build();

  private final RequestFormDataFactory dataFactory =
      RequestFormDataFactory.builder()
          .formFieldsFactory(new FormFieldsFactory(projectPopulator, oldFieldPopulator))
          .fieldLocationFinder(new FormFieldLocationFinder())
          .dataPopulator(new RequestFormDataPopulator(MAPPER))
          .build();

  @Test
  void shouldReturnEmptyDataForEmptySchema() {
    var request = givenAnyRequest();
    var schema = toJsonNode("[]");

    var data = dataFactory.build(request, schema);

    assertThatJson(data.toString()).isEqualTo("[]");
  }

  @ParameterizedTest
  @MethodSource("schemaAndExpectedDataFileNames")
  void shouldPopulateDataForGivenSchema(String schemaFileName, String expectedDataFileName) {
    var request = givenAnyRequest();
    var projects = buildNProjects(2);
    givenBatchProjectsForRequest(request, projects);
    var schema = loadFormSchema(schemaFileName);

    var data = dataFactory.build(request, schema);

    var expectedData = loadExpectedFormData(expectedDataFileName);
    assertThatJson(data.toString()).isEqualTo(expectedData);
  }

  private Request givenAnyRequest() {
    return RequestMother.build();
  }

  private void givenBatchProjectsForRequest(Request request, Collection<Project> projects) {
    when(batchRequestFinder.getAllBatchProjects(request)).thenReturn(projects);
  }

  private Collection<Project> buildNProjects(int n) {
    return IntStream.rangeClosed(1, n).mapToObj(this::buildProject).toList();
  }

  private Project buildProject(int n) {
    return ProjectMother.builder()
        .gsid(String.format("GS%d", n))
        .name(String.format("Test Project %d", n))
        .registryId(n)
        .build();
  }

  private static Stream<Arguments> schemaAndExpectedDataFileNames() {
    return Stream.of(
        Arguments.of("schema-projects-array-in-step.json", "data-projects-array-in-step.json"),
        Arguments.of(
            "schema-projects-array-example-in-step.json",
            "data-projects-array-example-in-step.json"),
        Arguments.of(
            "schema-individual-fields-in-step.json", "data-individual-fields-in-step.json"));
  }

  private static JsonNode loadFormSchema(String filename) {
    var path = String.format("json/form/schema/%s", filename);
    var json = loadContentFromClasspath(path);
    return toJsonNode(json);
  }

  private static String loadExpectedFormData(String filename) {
    var path = String.format("json/form/data/%s", filename);
    return loadContentFromClasspath(path);
  }

  private static JsonNode toJsonNode(String json) {
    try {
      return MAPPER.readTree(json);
    } catch (JacksonException e) {
      throw new JsonConversionException(e);
    }
  }
}

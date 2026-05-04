package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

import java.util.stream.Stream;
import org.goldstandard.assurance.api.dto.RequestFormCreateDTO;
import org.goldstandard.assurance.api.dto.RequestFormDTO;
import org.goldstandard.assurance.api.dto.RequestFormSubmitDTO;
import org.goldstandard.assurance.api.dto.RequestFormUpdateDTO;
import org.goldstandard.assurance.api.entity.FormDefinition;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.DocumentDisplayCategory;
import org.goldstandard.assurance.api.fixtures.FormDefinitionMother;
import org.goldstandard.assurance.api.helper.RequestHelper;
import org.goldstandard.assurance.api.repository.FormDefinitionRepository;
import org.goldstandard.assurance.api.repository.RequestFormRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Import({RequestHelper.class})
class RequestFormApiIntegrationTest extends AbstractApiTest {

  @Autowired ApiClient client;
  @Autowired RequestHelper requestHelper;
  @Autowired FormDefinitionRepository formDefinitionRepository;
  @Autowired RequestFormRepository requestFormRepository;
  @Autowired ObjectMapper objectMapper;

  @AfterEach
  void tearDown() {
    requestFormRepository.deleteAll();
    formDefinitionRepository.deleteAll();
  }

  @Test
  void shouldCreateRequestForm() {
    var request = requestHelper.createTestRequest(1);
    try {
      var definition = givenFormDefinitionExists();
      var dto = buildCreateDTO(definition.getType().getId());

      var response = client.createRequestForm(request.getId(), dto);

      assertThat(response.getStatusCode()).isEqualTo(OK);
      assertThat(response.getBody()).isNotNull();
    } finally {
      requestHelper.deleteTestRequest(request);
    }
  }

  @Test
  void shouldNotCreateMultipleFormsOfTheSameTypeForTheSameRequest() {
    var request = requestHelper.createTestRequest(1);
    try {
      var definition = givenFormDefinitionExists();
      var dto = buildCreateDTO(definition.getType().getId());
      givenRequestFormExists(request, dto);

      var response = client.createRequestForm(request.getId(), dto);

      assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    } finally {
      requestHelper.deleteTestRequest(request);
    }
  }

  @Test
  void shouldGetRequestForm() {
    var request = requestHelper.createTestRequest(1);
    var definition = givenFormDefinitionExists();
    try {
      var formId = givenRequestFormExists(request, definition);

      var response = client.getRequestForm(request.getId(), formId);

      assertThat(response.getStatusCode()).isEqualTo(OK);
      var body = response.getBody();
      assertThat(body).isNotNull();
      assertThat(body.getId()).isPositive();
      assertThat(body.getVersion()).isEqualTo(definition.getVersion());
      assertThat(body.getType().getCode()).isEqualTo("ANNUAL_REPORT");
      assertThat(body.getSchema()).isEqualTo(definition.getSchema());
      assertThat(body.getData()).isEmpty();
      assertThat(body.getStatus().getCode()).isEqualTo("DRAFT");
      assertThat(body.getCreatedAt()).isNotNull();
      assertThat(body.getUpdatedAt()).isNull();
      assertThat(body.getSubmittedAt()).isNull();
    } finally {
      requestHelper.deleteTestRequest(request);
    }
  }

  @Test
  void shouldUpdateRequestFormData() {
    var request = requestHelper.createTestRequest(1);
    var definition = givenFormDefinitionExists();
    try {
      var formId = givenRequestFormExists(request, definition);
      var dto = buildUpdateDTO();

      var response = client.updateRequestForm(request.getId(), formId, dto);

      assertThat(response.getStatusCode()).isEqualTo(OK);
      var body = response.getBody();
      assertThat(body).isNotNull();
      assertThat(body.getData()).isEqualTo(dto.getData());
      assertThat(body.getUpdatedAt()).isNotNull();
    } finally {
      requestHelper.deleteTestRequest(request);
    }
  }

  @Test
  void submitRequestFormShouldReturnErrorIfDataDoesNotMatchSchema() {
    var request = requestHelper.createTestRequest(1);
    var definition = givenFormDefinitionExists();
    try {
      var formId = givenRequestFormExists(request, definition);
      var dto = buildSubmitDTO(buildData("{}"));

      var response = client.submitRequestForm(request.getId(), formId, dto);

      assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
      assertThat(response.getBody()).isNull();
    } finally {
      requestHelper.deleteTestRequest(request);
    }
  }

  @Test
  void shouldSubmitRequestForm() {
    var request = requestHelper.createTestRequest(1);
    var definition = givenFormDefinitionExists();
    try {
      var formId = givenRequestFormExists(request, definition);
      var dto = buildSubmitDTO();

      var response = client.submitRequestForm(request.getId(), formId, dto);

      assertThat(response.getStatusCode()).isEqualTo(OK);
      var body = response.getBody();
      assertThat(body).isNotNull();
      assertThat(body.getData()).isEqualTo(dto.getData());
      assertThat(body.getStatus().getCode()).isEqualTo("SUBMITTED");
      assertThat(body.getUpdatedAt()).isNotNull();
      assertThat(body.getSubmittedAt()).isNotNull();
    } finally {
      requestHelper.deleteTestRequest(request);
    }
  }

  @Test
  void shouldNotReSubmitAlreadySubmittedRequestForm() {
    var request = requestHelper.createTestRequest(1);
    var definition = givenFormDefinitionExists();
    try {
      var formId = givenRequestFormExists(request, definition);
      var dto = buildSubmitDTO();
      givenRequestFormSubmitted(request, formId, dto);

      var response = client.submitRequestForm(request.getId(), formId, dto);

      assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    } finally {
      requestHelper.deleteTestRequest(request);
    }
  }

  @Test
  void shouldNotUpdateRequestFormWithSubmittedStatus() {
    var request = requestHelper.createTestRequest(1);
    var definition = givenFormDefinitionExists();
    try {
      var formId = givenRequestFormExists(request, definition);
      givenRequestFormSubmitted(request, formId);
      var dto = buildUpdateDTO();

      var response = client.updateRequestForm(request.getId(), formId, dto);

      assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    } finally {
      requestHelper.deleteTestRequest(request);
    }
  }

  @Test
  void shouldReturnEmptyPageOfRequestFormsIfNonePresentForRequest() {
    var request = requestHelper.createTestRequest(1);
    try {
      var response = client.getRequestFormsPage(request.getId());

      assertThat(response.getStatusCode()).isEqualTo(OK);
      var body = response.getBody();
      assertThat(body).isNotNull();
      assertThat(body.getTotalElements()).isZero();
    } finally {
      requestHelper.deleteTestRequest(request);
    }
  }

  @Test
  void shouldReturnPageOfRequestFormsForRequest() {
    var request = requestHelper.createTestRequest(1);
    var definition = givenFormDefinitionExists();
    try {
      var formId = givenRequestFormExists(request, definition);

      var response = client.getRequestFormsPage(request.getId());

      assertThat(response.getStatusCode()).isEqualTo(OK);
      var body = response.getBody();
      assertThat(body).isNotNull();
      assertThat(body.getTotalElements()).isOne();
      assertThat(body.getContent()).map(RequestFormDTO::getId).containsExactly(formId);
    } finally {
      requestHelper.deleteTestRequest(request);
    }
  }

  @ParameterizedTest
  @MethodSource("displayCategoryIdsAndExpectedNumberOfForms")
  void shouldFilterPageOfRequestFormsByDisplayCategoryId(
      Integer displayCategoryId, Integer expectedNumberOfForms) {
    var request = requestHelper.createTestRequest(1);
    var definition = givenFormDefinitionExists();
    try {
      givenRequestFormExists(request, definition);

      var response = client.getRequestFormsPage(request.getId(), displayCategoryId);

      assertThat(response.getStatusCode()).isEqualTo(OK);
      var body = response.getBody();
      assertThat(body).isNotNull();
      assertThat(body.getTotalElements()).isEqualTo(expectedNumberOfForms.longValue());
    } finally {
      requestHelper.deleteTestRequest(request);
    }
  }

  @Test
  void shouldReturnEmptyRequestFormsIfNonePresentForRequest() {
    var request = requestHelper.createTestRequest(1);
    try {
      var response = client.getRequestForms(request.getId());

      assertThat(response.getStatusCode()).isEqualTo(OK);
      assertThat(response.getBody()).isEmpty();
    } finally {
      requestHelper.deleteTestRequest(request);
    }
  }

  @Test
  void shouldReturnAllRequestFormsForRequest() {
    var request = requestHelper.createTestRequest(1);
    var definition = givenFormDefinitionExists();
    try {
      var formId = givenRequestFormExists(request, definition);

      var response = client.getRequestForms(request.getId());

      assertThat(response.getStatusCode()).isEqualTo(OK);
      assertThat(response.getBody()).map(RequestFormDTO::getId).containsExactly(formId);
    } finally {
      requestHelper.deleteTestRequest(request);
    }
  }

  @ParameterizedTest
  @MethodSource("displayCategoryIdsAndExpectedNumberOfForms")
  void shouldFilterRequestFormsByDisplayCategoryId(
      Integer displayCategoryId, Integer expectedNumberOfForms) {
    var request = requestHelper.createTestRequest(1);
    var definition = givenFormDefinitionExists();
    try {
      givenRequestFormExists(request, definition);

      var response = client.getRequestForms(request.getId(), displayCategoryId);

      assertThat(response.getStatusCode()).isEqualTo(OK);
      assertThat(response.getBody()).hasSize(expectedNumberOfForms);
    } finally {
      requestHelper.deleteTestRequest(request);
    }
  }

  private RequestFormCreateDTO buildCreateDTO(Integer formTypeId) {
    return RequestFormCreateDTO.builder().typeId(formTypeId).build();
  }

  private RequestFormUpdateDTO buildUpdateDTO() {
    return RequestFormUpdateDTO.builder().data(buildValidDataWithValue("value-1")).build();
  }

  private RequestFormSubmitDTO buildSubmitDTO() {
    return buildSubmitDTO(buildValidDataWithValue("value-2"));
  }

  private RequestFormSubmitDTO buildSubmitDTO(JsonNode data) {
    return RequestFormSubmitDTO.builder().data(data).build();
  }

  private FormDefinition givenFormDefinitionExists() {
    return formDefinitionRepository.save(FormDefinitionMother.build());
  }

  private int givenRequestFormExists(Request request, FormDefinition definition) {
    var dto = buildCreateDTO(definition.getType().getId());
    return givenRequestFormExists(request, dto);
  }

  private int givenRequestFormExists(Request request, RequestFormCreateDTO dto) {
    var response = client.createRequestForm(request.getId(), dto);
    assertThat(response.getBody()).isNotNull();
    return response.getBody().getId();
  }

  private void givenRequestFormSubmitted(Request request, Integer formId) {
    var dto = buildSubmitDTO();
    givenRequestFormSubmitted(request, formId, dto);
  }

  private void givenRequestFormSubmitted(
      Request request, Integer formId, RequestFormSubmitDTO dto) {
    var response = client.submitRequestForm(request.getId(), formId, dto);
    assertThat(response.getBody()).isNotNull();
  }

  private JsonNode buildValidDataWithValue(String value) {
    return buildData(String.format("{\"field\":\"%s\"}", value));
  }

  private JsonNode buildData(String json) {
    try {
      return objectMapper.readTree(json);
    } catch (JacksonException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private static Stream<Arguments> displayCategoryIdsAndExpectedNumberOfForms() {
    return Stream.of(
        Arguments.of(DocumentDisplayCategory.REQUIRED_DISPLAY_CATEGORY_ID, 0),
        Arguments.of(DocumentDisplayCategory.SUPPORTING_DISPLAY_CATEGORY_ID, 1),
        Arguments.of(DocumentDisplayCategory.OTHER_DISPLAY_CATEGORY_ID, 0));
  }
}

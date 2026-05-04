package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import org.goldstandard.assurance.api.dto.FormDefinitionCreateDTO;
import org.goldstandard.assurance.api.dto.FormDefinitionPublishDTO;
import org.goldstandard.assurance.api.dto.FormDefinitionUpdateDTO;
import org.goldstandard.assurance.api.entity.FormDefinitionStatus;
import org.goldstandard.assurance.api.repository.FormDefinitionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

class FormDefinitionApiIntegrationTest extends AbstractApiTest {

  private static final Integer FORM_TYPE_ID = 0;

  @Autowired ApiClient client;
  @Autowired FormDefinitionRepository formDefinitionRepository;
  @Autowired ObjectMapper objectMapper;

  @AfterEach
  void tearDown() {
    formDefinitionRepository.deleteAll();
  }

  @Test
  void testCreateFormDefinition() {
    var dto = buildCreateDTO();

    var response = client.createFormDefinition(dto);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void testCreateFormDefinitionUnauthenticatedIsDenied() {
    var dto = buildCreateDTO();

    var response = client.createFormDefinitionUnauthenticated(dto);

    assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
  }

  @Test
  void testCannotCreateMultipleDraftsForSameFormType() {
    var dto = buildCreateDTO();
    var firstResponse = client.createFormDefinition(dto);
    assertThat(firstResponse.getStatusCode()).isEqualTo(OK);

    var secondResponse = client.createFormDefinition(dto);
    assertThat(secondResponse.getStatusCode()).isEqualTo(BAD_REQUEST);
  }

  @Test
  void testGetFormDefinition() {
    var dto = buildCreateDTO();
    var id = givenFormDefinitionExists(dto);

    var response = client.getFormDefinition(id);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    var definition = response.getBody();
    assertThat(definition).isNotNull();
    assertThat(definition.getId()).isEqualTo(id);
    assertThat(definition.getVersion()).isNull();
    assertThat(definition.getType().getId()).isEqualTo(dto.getTypeId());
    assertThat(definition.getStatus().getId()).isEqualTo(FormDefinitionStatus.DRAFT_STATUS_ID);
    assertThat(definition.getSchema()).isEqualTo(dto.getSchema());
    assertThat(definition.getCreatedAt()).isNotNull();
    assertThat(definition.getUpdatedAt()).isNull();
    assertThat(definition.getPublishedAt()).isNull();
  }

  @Test
  void testGetFormDefinitionUnauthenticatedIsDenied() {
    var dto = buildCreateDTO();
    var id = givenFormDefinitionExists(dto);

    var response = client.getFormDefinitionUnauthenticated(id);
    assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
  }

  @Test
  void testGetFormDefinitionNotFound() {
    var id = 99999;

    var response = client.getFormDefinition(id);

    assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
  }

  @Test
  void testUpdateFormDefinition() {
    var createDto = buildCreateDTO();
    var id = givenFormDefinitionExists(createDto);
    var updateDto = buildUpdateDTO();

    var response = client.updateFormDefinition(id, updateDto);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    var definition = response.getBody();
    assertThat(definition).isNotNull();
    assertThat(definition.getSchema()).isEqualTo(updateDto.getSchema());
    assertThat(definition.getUpdatedAt()).isNotNull();
  }

  @Test
  void testUpdateFormDefinitionUnauthenticatedIsDenied() {
    var createDto = buildCreateDTO();
    var id = givenFormDefinitionExists(createDto);
    var updateDto = buildUpdateDTO();

    var response = client.updateFormDefinitionUnauthenticated(id, updateDto);

    assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
  }

  @Test
  void testCannotUpdatePublishedFormDefinition() {
    var id = givenPublishedFormDefinitionExists();
    var updateDto = buildUpdateDTO();

    var response = client.updateFormDefinition(id, updateDto);

    assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
  }

  @Test
  void testPublishFormDefinition() {
    var createDto = buildCreateDTO();
    var id = givenFormDefinitionExists(createDto);
    var publishDto = buildPublishDTO();

    var response = client.publishFormDefinition(id, publishDto);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    var definition = response.getBody();
    assertThat(definition).isNotNull();
    assertThat(definition.getVersion()).isEqualTo(0.01);
    assertThat(definition.getSchema()).isEqualTo(publishDto.getSchema());
    assertThat(definition.getPublishedAt()).isNotNull();
  }

  @Test
  void testPublishFormDefinitionUnauthenticatedIsDenied() {
    var createDto = buildCreateDTO();
    var id = givenFormDefinitionExists(createDto);
    var publishDto = buildPublishDTO();

    var response = client.publishFormDefinitionUnauthenticated(id, publishDto);

    assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
  }

  @Test
  void testCannotRepublishFormDefinition() {
    var id = givenPublishedFormDefinitionExists();
    var publishDto = buildPublishDTO();

    var response = client.publishFormDefinition(id, publishDto);

    assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
  }

  private FormDefinitionCreateDTO buildCreateDTO() {
    return FormDefinitionCreateDTO.builder()
        .typeId(FORM_TYPE_ID)
        .schema(buildSchemaWithValue("value-1"))
        .build();
  }

  private FormDefinitionUpdateDTO buildUpdateDTO() {
    return FormDefinitionUpdateDTO.builder().schema(buildSchemaWithValue("value-2")).build();
  }

  private FormDefinitionPublishDTO buildPublishDTO() {
    return FormDefinitionPublishDTO.builder()
        .schema(buildSchemaWithValue("value-3"))
        .increaseMajorVersion(false)
        .build();
  }

  private JsonNode buildSchemaWithValue(String value) {
    try {
      return objectMapper.readTree(
          String.format("{\"type\":\"object\",\"properties\":{\"field\":\"%s\"}}", value));
    } catch (JacksonException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private int givenPublishedFormDefinitionExists() {
    var createDto = buildCreateDTO();
    var id = givenFormDefinitionExists(createDto);
    var publishDto = buildPublishDTO();
    givenFormDefinitionIsPublished(id, publishDto);
    return id;
  }

  private int givenFormDefinitionExists(FormDefinitionCreateDTO dto) {
    var response = client.createFormDefinition(dto);
    assertThat(response.getBody()).isNotNull();
    return response.getBody().getId();
  }

  private void givenFormDefinitionIsPublished(int id, FormDefinitionPublishDTO dto) {
    var response = client.publishFormDefinition(id, dto);
    assertThat(response.getBody()).isNotNull();
  }
}

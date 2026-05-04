package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.dto.common.PageableParams.DEFAULT_PAGE_SIZE;
import static org.goldstandard.assurance.api.service.PermissionsService.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.goldstandard.assurance.api.dto.MethodologyManagementDTO;
import org.goldstandard.assurance.api.repository.MethodologyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class MethodologyApiIntegrationTest extends AbstractApiTest {

  @Autowired ApiClient client;
  @Autowired MethodologyRepository repository;

  @Test
  void shouldReturnAllMethodologiesPaged() {
    var actualCount = repository.findAll().size();
    var result = client.getAllMethodologiesPagedAsAdmin();
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).isNotNull();
    assertThat(result.getBody().getContent()).isNotEmpty();
    assertThat(result.getBody().getPageSize()).isEqualTo(DEFAULT_PAGE_SIZE);
    assertThat(result.getBody().getTotalElements()).isEqualTo(actualCount);
  }

  @Test
  void shouldReturnAllMethodologiesPagedMatchingDescriptionFilter() {
    var createDTO =
        MethodologyManagementDTO.builder()
            .description("page description filter test")
            .active(true)
            .build();
    Integer idToDelete = null;
    try {
      var result = client.createMethodologyAsAdmin(createDTO);
      assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

      var pageResult =
          client.getAllMethodologiesPagedAsAdmin(
              createDTO.getDescription().substring(5, 10).toUpperCase());
      assertThat(pageResult.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(pageResult.getBody()).isNotNull();
      assertThat(pageResult.getBody().getContent().getFirst().getDescription())
          .isEqualTo(createDTO.getDescription());
      assertThat(pageResult.getBody().getTotalElements()).isEqualTo(1);

      idToDelete = result.getBody().getId();
    } finally {
      if (idToDelete != null) {
        cleanUpMethodologies(List.of(idToDelete));
      }
    }
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        ARM_TEAM_ROLE,
        GS_REGISTRY_ROLE,
        FINANCE_ROLE,
        GS_REVIEWER_ROLE,
        PROJECT_DEVELOPER_ROLE,
        VVB_ROLE,
        ADMIN_ROLE
      })
  void shouldReturnAllMethodologies(String role) {
    var actualCount = repository.findAll().size();
    var result = client.getAllMethodologiesAs(role);
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).isNotNull();
    assertThat(result.getBody()).hasSize(actualCount);
  }

  @Test
  void shouldCreateNewMethodology() {

    var createDTO = MethodologyManagementDTO.builder().description("test").active(true).build();
    Integer idToDelete = null;
    try {
      var result = client.createMethodologyAsAdmin(createDTO);
      assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
      var created = result.getBody();

      assertSavedCorrectly(created, createDTO);

      assertNotNull(created);
      idToDelete = created.getId();
    } finally {
      if (idToDelete != null) {
        cleanUpMethodologies(List.of(idToDelete));
      }
    }
  }

  @Test
  void shouldUpdateMethodology() {
    var createDTO = MethodologyManagementDTO.builder().description("test").active(true).build();
    List<Integer> idsToDelete = new ArrayList<>();
    try {
      var createResult = client.createMethodologyAsAdmin(createDTO);
      assertThat(createResult.getBody()).isNotNull();
      assertThat(createResult.getStatusCode()).isEqualTo(HttpStatus.OK);

      var updateDTO =
          MethodologyManagementDTO.builder().description("update").active(false).build();
      var result = client.updateMethodologyAsAdmin(updateDTO, createResult.getBody().getId());
      assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

      var updated = result.getBody();
      assertSavedCorrectly(updated, updateDTO);

      assertNotNull(updated);
      idsToDelete.addAll(List.of(createResult.getBody().getId(), updated.getId()));
    } finally {
      cleanUpMethodologies(idsToDelete);
    }
  }

  @Test
  void shouldDeleteMethodology() {
    var createDTO = MethodologyManagementDTO.builder().description("test").active(true).build();

    var createResult = client.createMethodologyAsAdmin(createDTO);
    assertThat(createResult.getBody()).isNotNull();
    assertThat(createResult.getStatusCode()).isEqualTo(HttpStatus.OK);

    var result = client.deleteMethodologyAsAdmin(createResult.getBody().getId());
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(repository.findById(createResult.getBody().getId())).isEmpty();
  }

  private void cleanUpMethodologies(Collection<Integer> ids) {
    repository.deleteAllById(ids);
  }

  private static void assertSavedCorrectly(
      MethodologyManagementDTO actual, MethodologyManagementDTO dto) {
    assertThat(actual).isNotNull();
    assertThat(actual.getDescription()).isEqualTo(dto.getDescription());
    assertThat(actual.getActive()).isEqualTo(dto.getActive());
    assertThat(actual.getCode()).isEqualTo(dto.getDescription().toUpperCase());
  }
}

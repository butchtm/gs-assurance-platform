package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.populator.ProjectPopulator.NUMBER_OF_PROJECTS;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.goldstandard.assurance.api.dto.ProjectDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

class PaginatedApiIntegrationTest extends AbstractApiTest {

  private static final String ENDPOINT_TO_TEST = "/projects";

  @Test
  void testGetAllProjectsWithNoParams() {
    var requestEntity = this.getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var response =
        this.restTemplate.exchange(
            baseUrl + ENDPOINT_TO_TEST,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<PagedResource<ProjectDTO>>() {});

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
    assertNotNull(response.getBody());
    assertThat(response.getBody().getPageSize()).isEqualTo(25);
    assertThat(response.getBody().getPageNumber()).isEqualTo(1);
    assertThat(response.getBody().getTotalPages()).isEqualTo(1);
    assertThat(response.getBody().getContent().size()).isEqualTo(NUMBER_OF_PROJECTS);
  }

  @Test
  void testGetAllProjectsWithInvalidParams() {
    var requestEntity = this.getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var response =
        this.restTemplate.exchange(
            baseUrl + ENDPOINT_TO_TEST + "?size=-1&page=-1",
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<PagedResource<ProjectDTO>>() {});

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
    assertNotNull(response.getBody());
    assertThat(response.getBody().getPageSize()).isEqualTo(25);
    assertThat(response.getBody().getPageNumber()).isEqualTo(1);
    assertThat(response.getBody().getTotalPages()).isEqualTo(1);
    assertThat(response.getBody().getContent().size()).isEqualTo(NUMBER_OF_PROJECTS);
  }

  @Test
  void testGetAllProjectsWithValidParams() {
    var requestEntity = this.getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var response =
        this.restTemplate.exchange(
            baseUrl + ENDPOINT_TO_TEST + "?size=1&page=4",
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<PagedResource<ProjectDTO>>() {});

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
    assertNotNull(response.getBody());
    assertThat(response.getBody().getPageSize()).isEqualTo(1);
    assertThat(response.getBody().getPageNumber()).isEqualTo(4);
    assertThat(response.getBody().getTotalPages()).isEqualTo(4);
    assertThat(response.getBody().getContent().size()).isEqualTo(1);
  }
}

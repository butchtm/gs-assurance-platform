package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.populator.ProjectPopulator.NUMBER_OF_PROJECTS;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class PaginatedApiIntegrationTest extends AbstractApiTest {

  @Autowired private ApiClient client;

  @Test
  void testGetAllProjectsWithNoParams() {
    var response = client.getProjects(ADMIN_ROLE);

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
    assertNotNull(response.getBody());
    assertThat(response.getBody().getPageSize()).isEqualTo(25);
    assertThat(response.getBody().getPageNumber()).isEqualTo(1);
    assertThat(response.getBody().getTotalPages()).isEqualTo(1);
    assertThat(response.getBody().getContent().size()).isEqualTo(NUMBER_OF_PROJECTS);
  }

  @Test
  void testGetAllProjectsWithInvalidParams() {
    var response = client.getFilteredProjects(ADMIN_ROLE, "size=-1&page=-1");

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
    assertNotNull(response.getBody());
    assertThat(response.getBody().getPageSize()).isEqualTo(25);
    assertThat(response.getBody().getPageNumber()).isEqualTo(1);
    assertThat(response.getBody().getTotalPages()).isEqualTo(1);
    assertThat(response.getBody().getContent().size()).isEqualTo(NUMBER_OF_PROJECTS);
  }

  @Test
  void testGetAllProjectsWithValidParams() {
    var response = client.getFilteredProjects(ADMIN_ROLE, "size=1&page=4");

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
    assertNotNull(response.getBody());
    assertThat(response.getBody().getPageSize()).isEqualTo(1);
    assertThat(response.getBody().getPageNumber()).isEqualTo(4);
    assertThat(response.getBody().getTotalPages()).isEqualTo(4);
    assertThat(response.getBody().getContent().size()).isEqualTo(1);
  }
}

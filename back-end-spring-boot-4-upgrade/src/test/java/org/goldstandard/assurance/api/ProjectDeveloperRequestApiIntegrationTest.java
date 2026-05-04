package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.populator.ProjectPopulator.PROJECT_DEVELOPER_PROJECT_ID;

import java.util.List;
import org.goldstandard.assurance.api.dto.GetProjectDeveloperRequestsDTO;
import org.goldstandard.assurance.api.dto.ProjectDeveloperRequestDTO;
import org.goldstandard.assurance.api.fixtures.GetProjectDeveloperRequestsDTOMother;
import org.goldstandard.assurance.api.fixtures.ProjectMother;
import org.goldstandard.assurance.api.helper.RequestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

@Import(RequestHelper.class)
class ProjectDeveloperRequestApiIntegrationTest extends AbstractApiTest {

  @Autowired RequestHelper requestHelper;

  @Autowired ApiClient client;

  @Test
  void testGetProjectDeveloperRequests() {
    var request1 = requestHelper.createTestRequest(PROJECT_DEVELOPER_PROJECT_ID);
    var request2 = requestHelper.createTestRequest(PROJECT_DEVELOPER_PROJECT_ID);
    try {
      var requestDto = GetProjectDeveloperRequestsDTOMother.empty();

      var response = client.getProjectDeveloperRequests(requestDto);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getContent())
          .map(ProjectDeveloperRequestDTO::getId)
          .containsExactly(request2.getId(), request1.getId());
    } finally {
      requestHelper.deleteTestRequest(request1);
      requestHelper.deleteTestRequest(request2);
    }
  }

  @Test
  void testGetProjectDeveloperRequestsFilteredByRequestStatus() {
    var statusId = 4;
    var project = ProjectMother.withIdOnly(PROJECT_DEVELOPER_PROJECT_ID);
    var request1 = requestHelper.createTestRequestWithRequestStatus(project, statusId);
    var request2 = requestHelper.createTestRequest(project.getId());
    try {
      var requestDto =
          GetProjectDeveloperRequestsDTOMother.builder()
              .requestStatusIds(List.of(statusId))
              .build();

      var response = client.getProjectDeveloperRequests(requestDto);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getContent())
          .map(ProjectDeveloperRequestDTO::getId)
          .containsExactly(request1.getId());
    } finally {
      requestHelper.deleteTestRequest(request1);
      requestHelper.deleteTestRequest(request2);
    }
  }

  @Test
  void testGetProjectDeveloperRequestsFilteredByGsid() {
    var request1 = requestHelper.createTestRequest(PROJECT_DEVELOPER_PROJECT_ID);
    try {
      var requestDto = GetProjectDeveloperRequestsDTO.builder().projectSearch("GS1004").build();

      var response = client.getProjectDeveloperRequests(requestDto);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getContent())
          .map(ProjectDeveloperRequestDTO::getId)
          .containsExactly(request1.getId());
    } finally {
      requestHelper.deleteTestRequest(request1);
    }
  }

  @Test
  void testGetProjectDeveloperRequestProjectDetails() {
    var request1 = requestHelper.createTestRequest(PROJECT_DEVELOPER_PROJECT_ID);
    try {
      var response = client.getProjectDeveloperRequestProjects();

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody()).hasSize(1);
      assertThat(response.getBody().getFirst().getGsid()).isEqualTo("GS1004");
    } finally {
      requestHelper.deleteTestRequest(request1);
    }
  }
}

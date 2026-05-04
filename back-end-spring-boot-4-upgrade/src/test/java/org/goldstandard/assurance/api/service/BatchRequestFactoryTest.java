package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.goldstandard.assurance.api.dto.BatchRequestCreateDTO;
import org.goldstandard.assurance.api.entity.Batch;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BatchRequestFactoryTest {

  @InjectMocks BatchRequestFactory batchRequestFactory;

  @Mock RequestFactory requestFactory;

  @Test
  void shouldConvertCreateDtoToRequests() {
    var project1 = new BatchRequestCreateDTO.ProjectRequestInfo();
    var project2 = new BatchRequestCreateDTO.ProjectRequestInfo();
    var dto =
        BatchRequestCreateDTO.builder().projectRequestInfos(List.of(project1, project2)).build();
    var request1 = givenConvertsToRequestWithId(dto, project1, 1);
    var request2 = givenConvertsToRequestWithId(dto, project2, 2);

    List<Request> requests = batchRequestFactory.buildRequests(dto);

    assertThat(requests).containsExactly(request1, request2);
  }

  @Test
  void shouldBuildBatch() {
    int batchTypeId = 1;
    var parentRequest = new Request();

    var batch = batchRequestFactory.buildBatch(batchTypeId, parentRequest);

    assertThat(batch.getBatchType().getId()).isEqualTo(batchTypeId);
    assertThat(batch.getParentRequest()).isEqualTo(parentRequest);
  }

  @Test
  void shouldConvertRequestsToBatchRequests() {
    var batch = new Batch();
    var project1 = buildProject(1);
    var request1 = buildRequest(11, project1);
    var project2 = buildProject(2);
    var request2 = buildRequest(22, project2);
    var requests = List.of(request1, request2);

    var batchRequests = batchRequestFactory.toBatchRequests(batch, requests);

    var batchRequest1 = batchRequests.getFirst();
    assertThat(batchRequest1.getRequest()).isEqualTo(request1);
    assertThat(batchRequest1.getProject()).isEqualTo(project1);
    assertThat(batchRequest1.getBatch()).isEqualTo(batch);
    var batchRequest2 = batchRequests.get(1);
    assertThat(batchRequest2.getRequest()).isEqualTo(request2);
    assertThat(batchRequest2.getProject()).isEqualTo(project2);
    assertThat(batchRequest2.getBatch()).isEqualTo(batch);
  }

  private Request givenConvertsToRequestWithId(
      BatchRequestCreateDTO dto,
      BatchRequestCreateDTO.ProjectRequestInfo projectRequestInfo,
      int id) {
    var request = buildRequest(id);
    when(requestFactory.buildRequestForBatch(dto, projectRequestInfo)).thenReturn(request);
    return request;
  }

  private static Request buildRequest(int id) {
    return buildRequest(id, buildProject(id));
  }

  private static Request buildRequest(int id, Project project) {
    var request = new Request();
    request.setId(id);
    request.setProject(project);
    return request;
  }

  private static Project buildProject(int id) {
    var project = new Project();
    project.setId(id);
    return project;
  }
}

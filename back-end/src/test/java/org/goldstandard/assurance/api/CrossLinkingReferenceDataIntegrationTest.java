package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.springframework.http.HttpMethod.POST;

import java.util.Comparator;
import java.util.List;
import org.goldstandard.assurance.api.dto.GetBatchCandidateProjectsDTO;
import org.goldstandard.assurance.api.dto.GetBatchTypesDTO;
import org.goldstandard.assurance.api.dto.ProjectDTO;
import org.goldstandard.assurance.api.entity.BatchMapping;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.lookup.BatchType;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;
import org.goldstandard.assurance.api.populator.ProjectPopulator;
import org.goldstandard.assurance.api.repository.BatchMappingRepository;
import org.goldstandard.assurance.api.repository.ProjectClassificationRepository;
import org.goldstandard.assurance.api.repository.ProjectRepository;
import org.goldstandard.assurance.api.service.BatchTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;

class CrossLinkingReferenceDataIntegrationTest extends AbstractApiTest {

  private static final int PROGRAMME_OF_ACTIVITIES_ID = 7;
  private static final int REAL_CASE_VPA_ID = 8;
  private static final int REGULAR_VPA_ID = 9;
  private static final int REAL_CASE_VPA_CROSS_LINKAGE_ID = 10;
  private static final int REGULAR_VPA_CROSS_LINKAGE_ID = 11;
  private static final int PRODUCTION_LIKE_REQUEST_TYPE_ID = 1000;
  private static final int REQUEST_TYPE_WITH_REGULAR_VPA_BATCHING = 1001;
  private static final int REAL_CASE_BATCH_TYPE_ID = 1;
  private static final int REGULAR_VPA_BATCH_TYPE_ID = 0;
  private static final int REAL_CASE_PLUS_REGULAR_BATCH_TYPE_ID = 2;
  private static final int TWO_OR_MORE_REGULAR_BATCH_TYPE_ID = 3;

  @Autowired ProjectClassificationRepository projectClassificationRepository;

  @Autowired BatchMappingRepository batchMappingRepository;

  @Autowired BatchTypeService batchTypeService;

  @Autowired ProjectRepository projectRepository;

  @Autowired ProjectPopulator projectPopulator;

  @Test
  void crossLinkClassificationsExistInactiveAndBatchMappingsMirrorBaseVpaTypes() {
    assertClassification(
        REAL_CASE_VPA_CROSS_LINKAGE_ID,
        "REAL_CASE_VPA_CROSS_LINKAGE",
        "Real Case VPA (cross linkage)");
    assertClassification(
        REGULAR_VPA_CROSS_LINKAGE_ID,
        "REGULAR_VPA_CROSS_LINKAGE",
        "Regular Case VPA (cross linkage)");

    assertThat(batchTypeIds(1000, REAL_CASE_VPA_CROSS_LINKAGE_ID))
        .isEqualTo(batchTypeIds(1000, REAL_CASE_VPA_ID));
    assertThat(batchTypeIds(1000, REGULAR_VPA_CROSS_LINKAGE_ID))
        .isEqualTo(batchTypeIds(1000, REGULAR_VPA_ID));
    assertThat(batchTypeIds(1001, REAL_CASE_VPA_CROSS_LINKAGE_ID))
        .isEqualTo(batchTypeIds(1001, REAL_CASE_VPA_ID));
    assertThat(batchTypeIds(1001, REGULAR_VPA_CROSS_LINKAGE_ID))
        .isEqualTo(batchTypeIds(1001, REGULAR_VPA_ID));
  }

  @Test
  void crossLinkBatchRuntimePathMirrorsBaseVpaTypes() {
    var nativePoa = createBatchProject(PROGRAMME_OF_ACTIVITIES_ID, 920001, null);
    var nativeRealCase = createBatchProject(REAL_CASE_VPA_ID, 920001, nativePoa);
    var nativeRegular = createBatchProject(REGULAR_VPA_ID, 920001, nativeRealCase);
    var nativeRegularSibling = createBatchProject(REGULAR_VPA_ID, 920001, nativeRealCase);
    var crossPoa = createBatchProject(PROGRAMME_OF_ACTIVITIES_ID, 920002, null);
    var crossRealCase = createBatchProject(REAL_CASE_VPA_CROSS_LINKAGE_ID, 920002, crossPoa);
    var crossRegular = createBatchProject(REGULAR_VPA_CROSS_LINKAGE_ID, 920002, crossRealCase);
    var crossRegularSibling =
        createBatchProject(REGULAR_VPA_CROSS_LINKAGE_ID, 920002, crossRealCase);

    try {
      assertBatchTypeServiceParity(
          REQUEST_TYPE_WITH_REGULAR_VPA_BATCHING, nativeRealCase, crossRealCase);
      assertBatchTypeServiceParity(
          REQUEST_TYPE_WITH_REGULAR_VPA_BATCHING, nativeRegular, crossRegular);
      assertCandidateProjectApiParity(
          REQUEST_TYPE_WITH_REGULAR_VPA_BATCHING,
          nativeRealCase,
          List.of(nativeRegular, nativeRegularSibling),
          crossRealCase,
          List.of(crossRegular, crossRegularSibling),
          REAL_CASE_BATCH_TYPE_ID);
      assertCandidateProjectApiParity(
          REQUEST_TYPE_WITH_REGULAR_VPA_BATCHING,
          nativeRegular,
          List.of(nativeRegularSibling),
          crossRegular,
          List.of(crossRegularSibling),
          REGULAR_VPA_BATCH_TYPE_ID);
    } finally {
      projectPopulator.deleteProjects(
          nativeRegularSibling,
          nativeRegular,
          nativeRealCase,
          nativePoa,
          crossRegularSibling,
          crossRegular,
          crossRealCase,
          crossPoa);
    }
  }

  @Test
  void crossLinkProductionLikeBatchRuntimePathMirrorsBaseVpaTypes() {
    var nativePoa = createBatchProject(PROGRAMME_OF_ACTIVITIES_ID, 930001, null);
    var nativeRealCase = createBatchProject(REAL_CASE_VPA_ID, 930001, nativePoa);
    var nativeRegular = createBatchProject(REGULAR_VPA_ID, 930001, nativeRealCase);
    var nativeRegularSibling = createBatchProject(REGULAR_VPA_ID, 930001, nativeRealCase);
    var crossPoa = createBatchProject(PROGRAMME_OF_ACTIVITIES_ID, 930002, null);
    var crossRealCase = createBatchProject(REAL_CASE_VPA_CROSS_LINKAGE_ID, 930002, crossPoa);
    var crossRegular = createBatchProject(REGULAR_VPA_CROSS_LINKAGE_ID, 930002, crossRealCase);
    var crossRegularSibling =
        createBatchProject(REGULAR_VPA_CROSS_LINKAGE_ID, 930002, crossRealCase);

    try {
      assertBatchTypeServiceParity(PRODUCTION_LIKE_REQUEST_TYPE_ID, nativeRealCase, crossRealCase);
      assertBatchTypeServiceParity(PRODUCTION_LIKE_REQUEST_TYPE_ID, nativeRegular, crossRegular);
      assertCandidateProjectApiParity(
          PRODUCTION_LIKE_REQUEST_TYPE_ID,
          nativeRealCase,
          List.of(nativeRegular, nativeRegularSibling),
          crossRealCase,
          List.of(crossRegular, crossRegularSibling),
          REAL_CASE_PLUS_REGULAR_BATCH_TYPE_ID);
      assertCandidateProjectApiParity(
          PRODUCTION_LIKE_REQUEST_TYPE_ID,
          nativeRegular,
          List.of(nativeRegularSibling),
          crossRegular,
          List.of(crossRegularSibling),
          TWO_OR_MORE_REGULAR_BATCH_TYPE_ID);
    } finally {
      projectPopulator.deleteProjects(
          nativeRegularSibling,
          nativeRegular,
          nativeRealCase,
          nativePoa,
          crossRegularSibling,
          crossRegular,
          crossRealCase,
          crossPoa);
    }
  }

  private void assertClassification(Integer id, String code, String description) {
    var classification = projectClassificationRepository.findById(id).orElseThrow();

    assertThat(classification)
        .extracting(
            ProjectClassification::getId,
            ProjectClassification::getCode,
            ProjectClassification::getDescription,
            ProjectClassification::isActive)
        .containsExactly(id, code, description, false);
  }

  private List<Integer> batchTypeIds(Integer requestTypeId, Integer projectClassificationId) {
    return batchMappingRepository
        .findAllByRequestTypeIdAndProjectClassificationId(requestTypeId, projectClassificationId)
        .stream()
        .sorted(Comparator.comparing(BatchMapping::getBatchTypeId))
        .map(BatchMapping::getBatchTypeId)
        .toList();
  }

  private void assertBatchTypeServiceParity(
      int requestTypeId, Project nativeProject, Project crossLinkProject) {
    assertThat(getBatchTypeIds(requestTypeId, nativeProject))
        .isEqualTo(getBatchTypeIds(requestTypeId, crossLinkProject));
  }

  private List<Integer> getBatchTypeIds(int requestTypeId, Project project) {
    return batchTypeService
        .getBatchTypes(new GetBatchTypesDTO(requestTypeId, project.getId()))
        .stream()
        .map(BatchType::getId)
        .toList();
  }

  private void assertCandidateProjectApiParity(
      int requestTypeId,
      Project nativeProject,
      List<Project> nativeExpectedCandidates,
      Project crossLinkProject,
      List<Project> crossLinkExpectedCandidates,
      int batchTypeId) {
    var nativeCandidates = getCandidateProjects(requestTypeId, nativeProject, batchTypeId);
    var crossLinkCandidates = getCandidateProjects(requestTypeId, crossLinkProject, batchTypeId);

    assertThat(crossLinkCandidates).hasSize(nativeCandidates.size());
    assertThat(nativeCandidates)
        .extracting(ProjectDTO::getId)
        .containsExactlyInAnyOrderElementsOf(toProjectIds(nativeExpectedCandidates));
    assertThat(crossLinkCandidates)
        .extracting(ProjectDTO::getId)
        .containsExactlyInAnyOrderElementsOf(toProjectIds(crossLinkExpectedCandidates));
  }

  private List<Integer> toProjectIds(List<Project> projects) {
    return projects.stream().map(Project::getId).toList();
  }

  private List<ProjectDTO> getCandidateProjects(
      int requestTypeId, Project project, int batchTypeId) {
    var body = new GetBatchCandidateProjectsDTO(requestTypeId, batchTypeId, project.getId());
    var requestEntity = getHttpEntityWithTokenAndBodyForRole(body, ADMIN_ROLE);
    var response =
        restTemplate.exchange(
            String.format("%s/batch-requests/candidate-projects", baseUrl),
            POST,
            requestEntity,
            new ParameterizedTypeReference<List<ProjectDTO>>() {});
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    return response.getBody();
  }

  private Project createBatchProject(int classificationId, int accountId, Project parentProject) {
    var project = new Project();
    project.setName("Cross-link batch runtime " + classificationId);
    project.setProjectClassification(new ProjectClassification(classificationId));
    project.setAccountId(accountId);
    project.setProjectStatus(ProjectStatus.newStatus());
    project.setMasterProject(parentProject);
    project.setParentProject(parentProject);
    return projectRepository.save(project);
  }
}

package org.goldstandard.assurance.api.populator;

import static org.goldstandard.assurance.api.populator.AccountPopulator.PROJECT_DEVELOPER_ACCOUNT_ID;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.TransitionProject;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriodId;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriodMethodology;
import org.goldstandard.assurance.api.entity.lookup.Bundle;
import org.goldstandard.assurance.api.entity.lookup.CurrentStandardVersion;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.entity.lookup.ProjectSize;
import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;
import org.goldstandard.assurance.api.entity.lookup.ProjectType;
import org.goldstandard.assurance.api.entity.lookup.SustainableDevelopmentGoal;
import org.goldstandard.assurance.api.entity.lookup.TransitionApprovalProcedure;
import org.goldstandard.assurance.api.entity.lookup.TransitionFromOtherScheme;
import org.goldstandard.assurance.api.entity.lookup.TransitionPathway;
import org.goldstandard.assurance.api.repository.CurrentStandardVersionRepository;
import org.goldstandard.assurance.api.repository.ProjectCreditingPeriodRepository;
import org.goldstandard.assurance.api.repository.ProjectRepository;
import org.goldstandard.assurance.api.repository.ProjectSizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Component
@Slf4j
public class ProjectPopulator {

  public static final int NUMBER_OF_BASE_PROJECTS = 3;
  public static final int NUMBER_OF_PROJECTS = NUMBER_OF_BASE_PROJECTS + 1;

  public static final ZonedDateTime FIXED_DATE = ZonedDateTime.parse("2023-10-30T11:42:12.4948Z");
  public static final int PROGRAMME_OF_ACTIVITIES_ID = 7;
  public static final int REAL_CASE_ID = 8;
  public static final int REGULAR_CASE_ID = 9;
  public static final int TEST_BUNDLE_ID = 2;
  public static final String TEST_GS_ID = "GS100";
  public static final int PROJECT_DEVELOPER_PROJECT_ID = 4;
  public static final List<ProjectType> PROJECT_TYPES = List.of(new ProjectType(4));

  public static List<Project> TEST_PROJECTS;
  public static Project TEST_PD_PROJECT;

  @Autowired ProjectRepository projectRepository;

  @Autowired ProjectCreditingPeriodRepository creditingPeriodRepository;

  @Autowired CurrentStandardVersionRepository currentStandardVersionRepository;

  @Autowired TransactionTemplate transactionTemplate;

  @Autowired ProjectSizeRepository projectSizeRepository;

  public boolean isPopulated() {
    return projectRepository.count() > 0;
  }

  public void setProjectStatusAsNew(Project project) {
    project.setProjectStatus(new ProjectStatus(1));
    projectRepository.save(project);
  }

  public void resetProjects() {
    TEST_PROJECTS.forEach(this::resetProject);
  }

  public Project findProjectByGsid(String gsid) {
    return projectRepository.findByGsid(gsid).orElseThrow();
  }

  public ProjectCreditingPeriod buildCreditingPeriod(Project project, ZonedDateTime startDate) {
    var id = toCreditingPeriodId(project.getId());
    return ProjectCreditingPeriod.builder()
        .projectId(id.getProjectId())
        .periodId(id.getPeriodId())
        .startDate(startDate)
        .endDate(startDate.plusDays(1))
        .methodologies(List.of(buildCreditingPeriodMethodology(id)))
        .build();
  }

  public void populate() {
    var currentStandardVersion = getCurrentStandardVersion();
    Project masterProject = null;
    var projects = new ArrayList<Project>();
    for (int i = 1; i <= NUMBER_OF_BASE_PROJECTS; i++) {
      var project = buildProject(i, currentStandardVersion, masterProject, projects);
      if (Objects.isNull(masterProject)) {
        masterProject = project;
      }
      projects.add(project);
    }
    projectRepository.saveAll(projects);

    addCreditingPeriodToProjects();

    // We have to re-fetch the test projects to get the db-assigned GSIDs
    TEST_PROJECTS =
        StreamSupport.stream(
                projectRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).spliterator(), false)
            .toList();

    TEST_PROJECTS.forEach(this::logPopulated);
    TEST_PD_PROJECT = projectRepository.save(buildProjectDeveloperProject(currentStandardVersion));
    logPopulated(TEST_PD_PROJECT);
  }

  public void deleteProjects(Project... projects) {
    Arrays.stream(projects).map(Project::getId).forEach(this::deleteProject);
  }

  public void deleteProject(Integer projectId) {
    transactionTemplate.executeWithoutResult(
        status -> {
          creditingPeriodRepository.deleteAllByProjectId(projectId);
          projectRepository.deleteById(projectId);
        });
  }

  public Project populateProject(int id) {
    var currentStandardVersion = getCurrentStandardVersion();
    var project = buildProject(id, currentStandardVersion, null, TEST_PROJECTS);
    return populateProject(project);
  }

  public Project populateProject(Project project) {
    var savedProject = projectRepository.save(project);
    var loadedProject = projectRepository.findById(savedProject.getId()).orElseThrow();
    addCreditingPeriodToProject(loadedProject);
    logPopulated(loadedProject);
    return loadedProject;
  }

  private CurrentStandardVersion getCurrentStandardVersion() {
    return currentStandardVersionRepository.findById(1).orElseThrow();
  }

  private ProjectSize getProjectSize() {
    return projectSizeRepository.findById(1).orElseThrow();
  }

  private void addCreditingPeriodToProjects() {
    projectRepository.findAll().forEach(this::addCreditingPeriodToProject);
  }

  private void addCreditingPeriodToProject(Project project) {
    var startDate = ZonedDateTime.parse("2024-05-20T00:00:00.000Z");
    var periods = List.of(buildCreditingPeriod(project, startDate));
    project.setCreditingPeriods(periods);
    creditingPeriodRepository.saveAll(periods);
  }

  private void logPopulated(Project project) {
    var status = project.getProjectStatus();
    var classification = project.getProjectClassification();
    log.info(
        "populated project {} {} with status {} and classification {} with account id {}",
        project.getId(),
        project.getGsid(),
        status.getDescription(),
        classification.getDescription(),
        project.getAccountId());
  }

  private void resetProject(Project project) {
    transactionTemplate.executeWithoutResult(
        status -> {
          creditingPeriodRepository.deleteAllByProjectId(project.getId());
          addCreditingPeriodToProject(project);
          var resetProject = projectRepository.save(reset(project));
          logPopulated(resetProject);
        });
  }

  private Project reset(Project project) {
    return project.toBuilder()
        .projectTypes(PROJECT_TYPES)
        .projectStatus(toProjectStatus(project.getId()))
        .build();
  }

  private Project buildProjectDeveloperProject(CurrentStandardVersion currentStandardVersion) {
    return buildProject(
        PROJECT_DEVELOPER_PROJECT_ID,
        currentStandardVersion,
        getProjectSize(),
        null,
        PROJECT_DEVELOPER_ACCOUNT_ID,
        new ArrayList<>());
  }

  private Project buildProject(
      int index,
      CurrentStandardVersion currentStandardVersion,
      Project masterProject,
      List<Project> allProjects) {
    var projectSize = getProjectSize();
    return buildProject(
        index, currentStandardVersion, projectSize, masterProject, index, allProjects);
  }

  private static Project buildProject(
      int index,
      CurrentStandardVersion currentStandardVersion,
      ProjectSize projectSize,
      Project masterProject,
      int accountId,
      List<Project> allProjects) {
    var project = new Project();
    project.setGsid(TEST_GS_ID + index);
    project.setGsidNum(index);
    project.setName("Project " + index);
    project.setDescription("Project Description " + index);
    project.setBundle(toBundle(index));
    project.setCurrentRequestId(index);
    project.setEstimatedAnnualUnits(index * 20);
    project.setHasProjectAssets(true);
    project.setStatus(false);
    project.setDesignCertificationDateManualSet(false);
    project.setProjectStartDate(FIXED_DATE);
    project.setDesignCertificationDate(FIXED_DATE);
    project.setProjectSize(projectSize);
    var transitionProject =
        TransitionProject.builder()
            .projectLinkInOtherScheme("https://a.link")
            .transitionApprovalProcedure(new TransitionApprovalProcedure(1))
            .transitionFromOtherScheme(new TransitionFromOtherScheme(1))
            .transitionPathway(new TransitionPathway(1))
            .build();
    project.setTransitionProject(transitionProject);
    project.setProjectStatus(toProjectStatus(index));
    project.setListingDateManualSet(false);
    project.setCurrentStandardVersion(currentStandardVersion);
    // The first one can be the master project and all others can have that as their master
    if (masterProject == null) {
      // This is the POA master
      project.setProjectClassification(new ProjectClassification(PROGRAMME_OF_ACTIVITIES_ID));
    } else {
      // Always set the shared masterProject link
      project.setMasterProject(masterProject);

      boolean isRealCase = ((index - 2) % 2 == 0);
      if (isRealCase) {
        project.setProjectClassification(new ProjectClassification(REAL_CASE_ID));
        // parent = POA
        project.setParentProject(masterProject);
      } else {
        project.setProjectClassification(new ProjectClassification(REGULAR_CASE_ID));
        // parent = real‐case entry in allProjects
        project.setParentProject(allProjects.get(index - 2));
      }
    }
    project.setAccountId(accountId);
    project.setProjectTypes(PROJECT_TYPES);
    project.setMethodologies(List.of(new Methodology(2)));
    project.setSustainableDevelopmentGoals(
        List.of(new SustainableDevelopmentGoal(1), new SustainableDevelopmentGoal(2)));
    project.setLastActivity(FIXED_DATE);
    project.setNotes("some notes");
    project.setDesignCertificationDate(FIXED_DATE);
    project.setCertifiedProjectDate(FIXED_DATE);
    project.setLastDesignCertificationRenewalDate(FIXED_DATE);
    project.setLastPerformanceReviewDate(FIXED_DATE);
    return project;
  }

  private static ProjectStatus toProjectStatus(Integer id) {
    if (id % 2 == 0) {
      return new ProjectStatus(1);
    }
    return new ProjectStatus(2);
  }

  private static Bundle toBundle(int index) {
    if (index > 4) {
      return null;
    }
    return Bundle.builder().id(index).name("Test Bundle " + index).build();
  }

  private static ProjectCreditingPeriodId toCreditingPeriodId(Integer projectId) {
    return ProjectCreditingPeriodId.builder().periodId(1).projectId(projectId).build();
  }

  private static ProjectCreditingPeriodMethodology buildCreditingPeriodMethodology(
      ProjectCreditingPeriodId id) {
    var methodology = new Methodology(0);
    return ProjectCreditingPeriodMethodology.builder()
        .projectId(id.getProjectId())
        .periodId(id.getPeriodId())
        .version(0.1)
        .methodology(methodology)
        .build();
  }
}

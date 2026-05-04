package org.goldstandard.assurance.api.service.external.registry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.goldstandard.assurance.api.entity.lookup.CarbonStream;
import org.goldstandard.assurance.api.entity.lookup.CurrentStandardVersion;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.entity.lookup.ProjectLocation;
import org.goldstandard.assurance.api.entity.lookup.ProjectSize;
import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;
import org.goldstandard.assurance.api.entity.lookup.SustainableDevelopmentGoal;
import org.goldstandard.assurance.api.service.external.ExternalCountryConverter;
import org.goldstandard.assurance.api.service.external.ExternalCreditingPeriod;
import org.goldstandard.assurance.api.service.external.ExternalCreditingPeriodsConverter;
import org.goldstandard.assurance.api.service.external.ExternalProjectStatusConverter;
import org.goldstandard.assurance.api.service.external.ExternalProjectTypeConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegistryProjectFactoryTest {

  RegistryProjectFactory factory;

  @Mock ExternalProjectStatusConverter statusConverter;

  @Mock ExternalCountryConverter countryConverter;

  @Mock RegistryProjectClassificationConverter classificationConverter;

  @Mock ExternalProjectTypeConverter typeConverter;

  @Mock RegistryProjectMethodologyConverter methodologyConverter;

  @Mock RegistryProjectCreditingPeriodDateConverter creditingPeriodDateConverter;

  @Mock ExternalCreditingPeriodsConverter creditingPeriodsConverter;

  @Mock Project project;

  @Mock Account account;

  @BeforeEach
  void setUp() {
    factory =
        RegistryProjectFactory.builder()
            .linkTemplate("http://localhost:3001/projects/%s")
            .statusConverter(statusConverter)
            .countryConverter(countryConverter)
            .classificationConverter(classificationConverter)
            .typeConverter(typeConverter)
            .methodologyConverter(methodologyConverter)
            .creditingPeriodDateConverter(creditingPeriodDateConverter)
            .creditingPeriodsConverter(creditingPeriodsConverter)
            .build();
  }

  @Test
  void shouldPopulateIdWithProjectId() {
    var expectedId = 1;
    when(project.getId()).thenReturn(expectedId);

    var registryProject = factory.build(project, account);

    assertThat(registryProject.getId()).isEqualTo(expectedId);
  }

  @Test
  void shouldPopulateProjectIdWithGsIdNum() {
    var expectedGsIdNum = 1234;
    when(project.getGsidNum()).thenReturn(expectedGsIdNum);

    var registryProject = factory.build(project, account);

    assertThat(registryProject.getProjectId()).isEqualTo(expectedGsIdNum);
  }

  @Test
  void shouldPopulatePoaIdWithMasterProjectGsId() {
    var expectedGsIdNum = 5678;
    var masterProject = mock(Project.class);
    when(masterProject.getGsidNum()).thenReturn(expectedGsIdNum);
    when(project.getMasterProject()).thenReturn(masterProject);

    var registryProject = factory.build(project, account);

    assertThat(registryProject.getPoaId()).isEqualTo(expectedGsIdNum);
  }

  @Test
  void shouldPopulateSustainCertIdWithGsIdNumber() {
    var expectedId = 1234;
    when(project.getGsidNum()).thenReturn(expectedId);

    var registryProject = factory.build(project, account);

    assertThat(registryProject.getGsidNum()).isEqualTo(expectedId);
  }

  @Test
  void shouldPopulateName() {
    var expectedName = "project name";
    when(project.getName()).thenReturn(expectedName);

    var registryProject = factory.build(project, account);

    assertThat(registryProject.getName()).isEqualTo(expectedName);
  }

  @Test
  void shouldPopulateDescription() {
    var expectedDescription = "project description";
    when(project.getDescription()).thenReturn(expectedDescription);

    var registryProject = factory.build(project, account);

    assertThat(registryProject.getDescription()).isEqualTo(expectedDescription);
  }

  @Test
  void shouldPopulateMappedStatusCode() {
    var status = buildStatus();
    when(project.getProjectStatus()).thenReturn(status);
    var expectedStatusCode = "expected-status-code";
    when(statusConverter.toExternalProjectStatus(status.getCode())).thenReturn(expectedStatusCode);

    var registryProject = factory.build(project, account);

    assertThat(registryProject.getStatus()).isEqualTo(expectedStatusCode);
  }

  @Test
  void shouldPopulateStandardsVersionDescription() {
    var standardsVersion = buildStandardVersion();
    when(project.getCurrentStandardVersion()).thenReturn(standardsVersion);

    var registryProject = factory.build(project, account);

    assertThat(registryProject.getStandardsVersion()).isEqualTo(standardsVersion.getDescription());
  }

  @Test
  void shouldPopulateMappedCountryCode() {
    var location = buildLocation();
    when(project.getProjectLocations()).thenReturn(List.of(location));
    var expectedCountryCode = "expected-country-code";
    when(countryConverter.toExternalCountry(location.getCode())).thenReturn(expectedCountryCode);

    var registryProject = factory.build(project, account);

    assertThat(registryProject.getCountryCode()).isEqualTo(expectedCountryCode);
  }

  @Test
  void shouldPopulateEstimatedAnnualCredits() {
    var expectedCredits = 123;
    when(project.getEstimatedAnnualUnits()).thenReturn(expectedCredits);

    var registryProject = factory.build(project, account);

    assertThat(registryProject.getEstimatedAnnualCredits()).isEqualTo(expectedCredits);
  }

  @Test
  void shouldPopulateMappedCreditingPeriodStartDate() {
    var expectedStartDate = ZonedDateTime.now();
    when(creditingPeriodDateConverter.toEarliestCreditingPeriodStartDate(project))
        .thenReturn(expectedStartDate);

    var registryProject = factory.build(project, account);

    assertThat(registryProject.getCreditingPeriodStartDate()).isEqualTo(expectedStartDate);
  }

  @Test
  void shouldPopulateMappedCreditingPeriodEndDate() {
    var expectedEndDate = ZonedDateTime.now();
    when(creditingPeriodDateConverter.toLatestCreditingPeriodEndDate(project))
        .thenReturn(expectedEndDate);

    var registryProject = factory.build(project, account);

    assertThat(registryProject.getCreditingPeriodEndDate()).isEqualTo(expectedEndDate);
  }

  @Test
  void shouldPopulateMappedMethodology() {
    var expectedMethodology = "expected-methodology";
    when(methodologyConverter.toMethodologyDescription(project)).thenReturn(expectedMethodology);

    var registryProject = factory.build(project, account);

    assertThat(registryProject.getMethodology()).isEqualTo(expectedMethodology);
  }

  @Test
  void shouldPopulateCarbonStream() {
    var carbonStream = buildCarbonStream();
    when(project.getCarbonStream()).thenReturn(carbonStream);

    var registryProject = factory.build(project, account);

    assertThat(registryProject.getCarbonStream()).isEqualTo(carbonStream.getCode());
  }

  @Test
  void shouldPopulateMappedType() {
    var expectedType = "expected-type";
    when(typeConverter.toExternalProjectType(project)).thenReturn(expectedType);

    var registryProject = factory.build(project, account);

    assertThat(registryProject.getType()).isEqualTo(expectedType);
  }

  @Test
  void shouldPopulateSize() {
    var size = buildProjectSize();
    when(project.getProjectSize()).thenReturn(size);

    var registryProject = factory.build(project, account);

    assertThat(registryProject.getSize()).isEqualTo(size.getDescription());
  }

  @Test
  void shouldPopulateUrl() {
    var gsId = "GS1234";
    when(project.getGsid()).thenReturn(gsId);

    var registryProject = factory.build(project, account);

    assertThat(registryProject.getUrl()).isEqualTo("http://localhost:3001/projects/GS1234");
  }

  @Test
  void shouldPopulateSustainableDevelopmentGoals() {
    var goals =
        List.of(
            buildSustainableDevelopmentGoal("GOAL-1"), buildSustainableDevelopmentGoal("GOAL-2"));
    when(project.getSustainableDevelopmentGoals()).thenReturn(goals);

    var registryProject = factory.build(project, account);

    assertThat(registryProject.getSustainableDevelopmentGoals())
        .containsExactly("GOAL-1", "GOAL-2");
  }

  @Test
  void shouldPopulateProjectDeveloper() {
    var expectedDeveloperName = "expected-developer-name";
    when(account.getName()).thenReturn(expectedDeveloperName);

    var registryProject = factory.build(project, account);

    assertThat(registryProject.getProjectDeveloper()).isEqualTo(expectedDeveloperName);
  }

  @Test
  void shouldPopulateMappedClassification() {
    var classification = buildClassification();
    when(project.getProjectClassification()).thenReturn(classification);
    var expectedClassificationCode = "expected-classification-code";
    when(classificationConverter.toRegistryClassification(classification.getCode()))
        .thenReturn(expectedClassificationCode);

    var registryProject = factory.build(project, account);

    assertThat(registryProject.getProgramOfActivities()).isEqualTo(expectedClassificationCode);
  }

  @Test
  void shouldPopulateMappedCreditingPeriods() {
    var creditingPeriods = List.of(new ProjectCreditingPeriod());
    when(project.getCreditingPeriods()).thenReturn(creditingPeriods);
    var expectedCreditingPeriods = List.of(mock(ExternalCreditingPeriod.class));
    when(creditingPeriodsConverter.toExternalCreditingPeriods(creditingPeriods))
        .thenReturn(expectedCreditingPeriods);

    var registryProject = factory.build(project, account);

    assertThat(registryProject.getCreditingPeriods()).isEqualTo(expectedCreditingPeriods);
  }

  @Test
  void shouldPopulateMappedDesignCertificationDate() {
    var designCertificationDate = LocalDate.of(2025, 1, 1).atStartOfDay(ZoneOffset.UTC);
    when(project.getDesignCertificationDate()).thenReturn(designCertificationDate);

    var registryProject = factory.build(project, account);

    assertThat(registryProject.getDesignCertificationDate()).isEqualTo(designCertificationDate);
  }

  private static ProjectStatus buildStatus() {
    var status = new ProjectStatus();
    status.setCode("NEW");
    return status;
  }

  private static CurrentStandardVersion buildStandardVersion() {
    var standardVersion = new CurrentStandardVersion();
    standardVersion.setDescription("Gold Standard for the Global Goals");
    return standardVersion;
  }

  private static ProjectLocation buildLocation() {
    var location = new ProjectLocation();
    location.setCode("INTERNATIONAL");
    return location;
  }

  private static CarbonStream buildCarbonStream() {
    var stream = new CarbonStream();
    stream.setCode("GS_CER");
    return stream;
  }

  private static ProjectSize buildProjectSize() {
    var size = new ProjectSize();
    size.setDescription("Large Scale");
    return size;
  }

  private static SustainableDevelopmentGoal buildSustainableDevelopmentGoal(String code) {
    var goal = new SustainableDevelopmentGoal();
    goal.setCode(code);
    return goal;
  }

  private static ProjectClassification buildClassification() {
    var classification = new ProjectClassification();
    classification.setCode("PROGRAMME_OF_ACTIVITIES");
    return classification;
  }
}

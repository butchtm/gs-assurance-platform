package org.goldstandard.assurance.api.service.external.sdg;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Stream;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.goldstandard.assurance.api.entity.lookup.CarbonStream;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.entity.lookup.ProjectLocation;
import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;
import org.goldstandard.assurance.api.service.external.ExternalCountryConverter;
import org.goldstandard.assurance.api.service.external.ExternalCreditingPeriod;
import org.goldstandard.assurance.api.service.external.ExternalCreditingPeriodsConverter;
import org.goldstandard.assurance.api.service.external.ExternalProjectStatusConverter;
import org.goldstandard.assurance.api.service.external.ExternalProjectTypeConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SdgProjectFactoryTest {

  @InjectMocks SdgProjectFactory factory;

  @Mock ExternalProjectStatusConverter statusConverter;

  @Mock ExternalCountryConverter countryConverter;

  @Mock SdgProjectClassificationConverter classificationConverter;

  @Mock ExternalProjectTypeConverter typeConverter;

  @Mock ExternalCreditingPeriodsConverter creditingPeriodsConverter;

  @Mock Project project;

  @Mock Account account;

  @Test
  void shouldPopulateIdWithProjectId() {
    var expectedId = 1;
    when(project.getId()).thenReturn(expectedId);

    var sdgProject = factory.build(project, account);

    assertThat(sdgProject.getId()).isEqualTo(expectedId);
  }

  @Test
  void shouldPopulatePoaProjectIdWithMasterProjectGsId() {
    var expectedGsIdNum = 5678;
    var masterProject = mock(Project.class);
    when(masterProject.getGsidNum()).thenReturn(expectedGsIdNum);
    when(project.getMasterProject()).thenReturn(masterProject);

    var sdgProject = factory.build(project, account);

    assertThat(sdgProject.getPoaProjectId()).isEqualTo(expectedGsIdNum);
  }

  @Test
  void shouldPopulateName() {
    var expectedName = "project name";
    when(project.getName()).thenReturn(expectedName);

    var sdgProject = factory.build(project, account);

    assertThat(sdgProject.getName()).isEqualTo(expectedName);
  }

  @Test
  void shouldPopulateDescription() {
    var expectedDescription = "project description";
    when(project.getDescription()).thenReturn(expectedDescription);

    var sdgProject = factory.build(project, account);

    assertThat(sdgProject.getDescription()).isEqualTo(expectedDescription);
  }

  @Test
  void shouldPopulateMappedStatusCode() {
    var status = buildStatus();
    when(project.getProjectStatus()).thenReturn(status);
    var expectedStatusCode = "expected-status-code";
    when(statusConverter.toExternalProjectStatus(status.getCode())).thenReturn(expectedStatusCode);

    var sdgProject = factory.build(project, account);

    assertThat(sdgProject.getStatus()).isEqualTo(expectedStatusCode);
  }

  @Test
  void shouldPopulateMappedCountryCodes() {
    var locations = buildLocations();
    when(project.getProjectLocations()).thenReturn(locations);
    var expectedCountryCodes = List.of("expected-country-code-1", "expected-country-code-2");
    when(countryConverter.toExternalCountries(locations)).thenReturn(expectedCountryCodes);

    var sdgProject = factory.build(project, account);

    assertThat(sdgProject.getCountryCodes()).isEqualTo(expectedCountryCodes);
  }

  @Test
  void shouldPopulateAnnualEstimatedCredits() {
    var expectedCredits = 123;
    when(project.getEstimatedAnnualUnits()).thenReturn(expectedCredits);

    var sdgProject = factory.build(project, account);

    assertThat(sdgProject.getAnnualEstimatedCredits()).isEqualTo(expectedCredits);
  }

  @Test
  void shouldPopulateCarbonStream() {
    var carbonStream = buildCarbonStream();
    when(project.getCarbonStream()).thenReturn(carbonStream);

    var sdgProject = factory.build(project, account);

    assertThat(sdgProject.getCarbonStream()).isEqualTo(carbonStream.getCode());
  }

  @Test
  void shouldPopulateMappedTypes() {
    var expectedTypes = List.of("expected-type-1", "expected-type-2");
    when(typeConverter.toExternalProjectTypes(project)).thenReturn(expectedTypes);

    var sdgProject = factory.build(project, account);

    assertThat(sdgProject.getTypes()).isEqualTo(expectedTypes);
  }

  @Test
  void shouldPopulateProjectDeveloper() {
    var expectedDeveloperName = "expected-developer-name";
    when(account.getName()).thenReturn(expectedDeveloperName);

    var sdgProject = factory.build(project, account);

    assertThat(sdgProject.getProjectDeveloper()).isEqualTo(expectedDeveloperName);
  }

  @Test
  void shouldPopulateMappedClassification() {
    var classification = buildClassification();
    when(project.getProjectClassification()).thenReturn(classification);
    var expectedClassificationCode = "expected-classification-code";
    when(classificationConverter.toSdgClassification(classification.getCode()))
        .thenReturn(expectedClassificationCode);

    var sdgProject = factory.build(project, account);

    assertThat(sdgProject.getProgramOfActivities()).isEqualTo(expectedClassificationCode);
  }

  @Test
  void shouldPopulateMappedCreditingPeriods() {
    var creditingPeriods = List.of(new ProjectCreditingPeriod());
    when(project.getCreditingPeriods()).thenReturn(creditingPeriods);
    var expectedCreditingPeriods = List.of(mock(ExternalCreditingPeriod.class));
    when(creditingPeriodsConverter.toExternalCreditingPeriods(creditingPeriods))
        .thenReturn(expectedCreditingPeriods);

    var sdgProject = factory.build(project, account);

    assertThat(sdgProject.getCreditingPeriods()).isEqualTo(expectedCreditingPeriods);
  }

  private static ProjectStatus buildStatus() {
    var status = new ProjectStatus();
    status.setCode("NEW");
    return status;
  }

  private static List<ProjectLocation> buildLocations() {
    return Stream.of("INTERNATIONAL", "GB").map(SdgProjectFactoryTest::buildLocation).toList();
  }

  private static ProjectLocation buildLocation(String code) {
    var location = new ProjectLocation();
    location.setCode(code);
    return location;
  }

  private static CarbonStream buildCarbonStream() {
    var stream = new CarbonStream();
    stream.setCode("GS_CER");
    return stream;
  }

  private static ProjectClassification buildClassification() {
    var classification = new ProjectClassification();
    classification.setCode("PROGRAMME_OF_ACTIVITIES");
    return classification;
  }
}

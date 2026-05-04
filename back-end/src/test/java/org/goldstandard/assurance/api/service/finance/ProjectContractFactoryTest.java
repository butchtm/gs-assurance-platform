package org.goldstandard.assurance.api.service.finance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import org.goldstandard.assurance.api.dto.finance.XmlMapperFactory;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriodMethodology;
import org.goldstandard.assurance.api.entity.lookup.CurrentStandardVersion;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.entity.lookup.ProjectLocation;
import org.goldstandard.assurance.api.entity.lookup.ProjectSize;
import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;
import org.goldstandard.assurance.api.entity.lookup.ProjectType;
import org.goldstandard.assurance.api.entity.lookup.SustainableDevelopmentGoal;
import org.goldstandard.assurance.api.fixtures.ZonedDateTimeMother;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.xmlunit.assertj.XmlAssert;
import uk.co.mruoc.file.FileLoader;

class ProjectContractFactoryTest {

  private final AccountFinder accountFinder = mock(AccountFinder.class);

  private final ProjectContractFactory factory =
      ProjectContractFactory.builder()
          .accountFinder(accountFinder)
          .mapper(XmlMapperFactory.build())
          .idSupplier(() -> UUID.fromString("e3ef1a46-f2c1-4c00-bdb6-70bb77008974"))
          .dateTimeFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx"))
          .classificationFactory(new FinanceProjectClassificationFactory())
          .build();

  @ParameterizedTest
  @CsvSource({
    "REGULAR_CASE_VPA,REGULAR_VPA",
    "REGULAR_VPA,REGULAR_VPA",
    "REAL_CASE_VPA_LEGACY,REAL_CASE_VPA",
    "REAL_CASE_VPA,REAL_CASE_VPA",
    "POA,PROGRAMME_OF_ACTIVITIES",
    "REAL_OR_REGULAR_CASE_POA,PROGRAMME_OF_ACTIVITIES",
    "PROGRAMME_OF_ACTIVITIES,PROGRAMME_OF_ACTIVITIES",
    "STANDALONE_PROJECT,STANDALONE_PROJECT",
    "NONE,NONE",
    "VPA_CPA,VPA_CPA",
    "ANY_OTHER,ANY_OTHER"
  })
  void shouldCreateXmlSalesOrderForSupportedClassification(
      String assuranceCode, String expectedFinanceCode) {
    var classification = toClassification(assuranceCode);
    var account = buildAccount();
    var project =
        projectBuilder().accountId(account.getId()).projectClassification(classification).build();
    when(accountFinder.forceGetAccountById(project.getAccountId())).thenReturn(account);

    var xml = factory.buildProjectContractXml(project);

    var expectedXml =
        FileLoader.loadContentFromClasspath("xml/unit-test/expected-project-contract.xml")
            .replace("%EXPECTED_CLASSIFICATION%", expectedFinanceCode);
    assertThat(xml).isPresent();
    XmlAssert.assertThat(xml.get()).and(expectedXml).ignoreWhitespace().areIdentical();
  }

  private static ProjectClassification toClassification(String code) {
    var classification = new ProjectClassification();
    classification.setCode(code);
    return classification;
  }

  private static Account buildAccount() {
    return Account.builder().id(141).name("Zsombor").address("To be added").build();
  }

  private static Project.ProjectBuilder projectBuilder() {
    var startDate = ZonedDateTimeMother.fixed();
    return Project.builder()
        .gsid("GS12480")
        .projectLocations(List.of(toLocation("HR"), toLocation("HU"), toLocation("RO")))
        .projectStatus(toStatus("LISTED"))
        .projectTypes(List.of(toType("AGRICULTURE"), toType("SOIL_ORGANIC_CARBON")))
        .creditingPeriods(List.of(buildCreditingPeriod()))
        .projectSize(toSize("LARGE_SCALE"))
        .description("BlackSoil regenerative farming program – Hungary")
        .projectStartDate(startDate)
        .currentStandardVersion(toStandardVersion("GS4GG"))
        .parentProject(Project.builder().gsid("GS12480").build())
        .name("GS13201 - GS12480 BlackSoil regenerative farming program")
        .sustainableDevelopmentGoals(buildDevelopmentGoals());
  }

  private static ProjectLocation toLocation(String code) {
    var type = new ProjectLocation();
    type.setCode(code);
    return type;
  }

  private static ProjectStatus toStatus(String code) {
    var type = new ProjectStatus();
    type.setCode(code);
    return type;
  }

  private static ProjectType toType(String code) {
    var type = new ProjectType();
    type.setCode(code);
    return type;
  }

  private static ProjectCreditingPeriod buildCreditingPeriod() {
    return ProjectCreditingPeriod.builder()
        .methodologies(
            List.of(
                toMethodology(1, "ACM0001_FLARING_OR_USE_OF_LANDFILL_GAS"),
                toMethodology(
                    2, "ACM0002_GRID_CONNECTED_ELECTRICITY_GENERATION_FROM_RENEWABLE_SOURCES"),
                toMethodology(
                    3,
                    "ACM0003_PARTIAL_SUBSTITUTION_OF_FOSSIL_FUELS_WITH_ALTERNATIVE_FUELS_OR_LESS_CARBON_INTENSIVE_FUELS_IN_CEMENT_MANUFACTURE")))
        .build();
  }

  private static List<SustainableDevelopmentGoal> buildDevelopmentGoals() {
    return List.of(
        toDevelopmentGoal(1, "NO_POVERTY"),
        toDevelopmentGoal(2, "ZERO_HUNGER"),
        toDevelopmentGoal(3, "GOOD_HEALTH_AND_WELL_BEING"),
        toDevelopmentGoal(4, "QUALITY_EDUCATION"),
        toDevelopmentGoal(5, "GENDER_EQUALITY"),
        toDevelopmentGoal(6, "CLEAN_WATER_AND_SANITATION"),
        toDevelopmentGoal(7, "AFFORDABLE_AND_CLEAN_ENERGY"),
        toDevelopmentGoal(8, "DECENT_WORK_AND_ECONOMIC_GROWTH"),
        toDevelopmentGoal(9, "INDUSTRY_INNOVATION_AND_INFRASTRUCTURE"),
        toDevelopmentGoal(10, "REDUCED_INEQUALITIES"),
        toDevelopmentGoal(11, "SUSTAINABLE_CITIES_AND_COMMUNITIES"),
        toDevelopmentGoal(12, "RESPONSIBLE_PRODUCTION_AND_CONSUMPTION"),
        toDevelopmentGoal(13, "CLIMATE_ACTION"),
        toDevelopmentGoal(14, "LIFE_BELOW_WATER"),
        toDevelopmentGoal(15, "LIFE_ON_LAND"),
        toDevelopmentGoal(16, "PEACE_JUSTICE_AND_STRONG_INSTITUTIONS"),
        toDevelopmentGoal(17, "PARTNERSHIPS_FOR_THE_GOALS"));
  }

  private static SustainableDevelopmentGoal toDevelopmentGoal(int id, String code) {
    var goal = new SustainableDevelopmentGoal();
    goal.setId(id);
    goal.setCode(code);
    return goal;
  }

  private static ProjectCreditingPeriodMethodology toMethodology(int id, String code) {
    var methodology = new Methodology();
    methodology.setId(id);
    methodology.setCode(code);
    return ProjectCreditingPeriodMethodology.builder().methodology(methodology).build();
  }

  private static ProjectSize toSize(String code) {
    var size = new ProjectSize();
    size.setCode(code);
    return size;
  }

  private static CurrentStandardVersion toStandardVersion(String code) {
    var version = new CurrentStandardVersion();
    version.setCode(code);
    return version;
  }
}

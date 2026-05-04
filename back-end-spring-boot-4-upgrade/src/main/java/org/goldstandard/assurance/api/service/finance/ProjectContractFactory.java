package org.goldstandard.assurance.api.service.finance;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.goldstandard.assurance.api.dto.finance.XmlMapperFactory;
import org.goldstandard.assurance.api.dto.finance.project.FundingSource;
import org.goldstandard.assurance.api.dto.finance.project.ProjectContract;
import org.goldstandard.assurance.api.dto.finance.project.ProjectContractDocument;
import org.goldstandard.assurance.api.dto.finance.project.ProjectEntity;
import org.goldstandard.assurance.api.dto.finance.project.ProjectEntity.ProjectEntityBuilder;
import org.goldstandard.assurance.api.dto.finance.project.ProjectFundingRuleEntity;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;
import org.goldstandard.assurance.api.entity.lookup.SustainableDevelopmentGoal;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Builder
@RequiredArgsConstructor
@Component
@Slf4j
public class ProjectContractFactory {

  private final AccountFinder accountFinder;
  private final ObjectMapper mapper;
  private final Supplier<UUID> idSupplier;
  private final DateTimeFormatter dateTimeFormatter;
  private final FinanceProjectClassificationFactory classificationFactory;

  @Autowired
  public ProjectContractFactory(AccountFinder accountFinder) {
    this(
        accountFinder,
        XmlMapperFactory.build(),
        UUID::randomUUID,
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx"),
        new FinanceProjectClassificationFactory());
  }

  public Optional<String> buildProjectContractXml(Project project) {
    var classification =
        classificationFactory.toFinanceProjectClassificationCode(
            project.getProjectClassification());
    var contract = toProjectContract(project, classification);
    return Optional.of(toXml(contract));
  }

  private ProjectContractDocument toProjectContract(Project project, String classification) {
    var account = accountFinder.forceGetAccountById(project.getAccountId());
    return toProjectContract(project, account, classification);
  }

  private ProjectContractDocument toProjectContract(
      Project project, Account account, String classification) {
    var types = toPipeDelimitedStringOfCodes(project.getProjectTypes());
    var fundingRule =
        ProjectFundingRuleEntity.defaultBuilder()
            .projectContractId(project.getGsid())
            .projectId(project.getGsid())
            .fundingSourceId(account.getName())
            .build();
    var startDate = project.getProjectStartDate();
    var entityBuilder =
        ProjectEntity.defaultBuilder()
            .id(project.getGsid())
            .gsfProjectId(project.getGsid())
            .contractId(project.getGsid())
            .geographicalLocation(toPipeDelimitedStringOfCodes(project.getProjectLocations()))
            .gsfStatus(project.getProjectStatus().getCode())
            .projectActivityType(types)
            .gsfProjectType(types)
            .startDate1(dateTimeFormatter.format(startDate))
            .endDate1(dateTimeFormatter.format(toEndDate(startDate)))
            .sectoral(toPipeDelimitedStringOfCodes(project.getAllMethodologies()))
            .scale(
                Optional.ofNullable(project.getProjectSize())
                    .map(ReferenceDataType::getCode)
                    .orElse(null))
            .projectClassification(classification)
            .requestId(idSupplier.get().toString())
            .projectDescription(project.getDescription())
            .standard(
                Optional.ofNullable(project.getCurrentStandardVersion())
                    .map(ReferenceDataType::getCode)
                    .orElse(null))
            .projectName(project.getName())
            .fundingRuleEntity(fundingRule)
            .customerAccount(Integer.toString(account.getId()));
    populateSustainableDevelopmentGoals(entityBuilder, project.getSustainableDevelopmentGoals());
    var entity = entityBuilder.build();
    var fundingSource =
        FundingSource.defaultBuilder()
            .id(account.getName())
            .projectContractId(project.getGsid())
            .customerOrOrganization(Integer.toString(account.getId()))
            .build();
    var contract =
        ProjectContract.defaultBuilder()
            .id(project.getGsid())
            .fundingSource(fundingSource)
            .project(entity)
            .name(account.getName())
            .build();
    return new ProjectContractDocument(contract);
  }

  private String toXml(ProjectContractDocument contract) {
    try {
      return mapper.writeValueAsString(contract);
    } catch (JacksonException e) {
      throw new ProjectContractCreationFailedException(contract.getId(), e);
    }
  }

  private static ZonedDateTime toEndDate(ZonedDateTime startDate) {
    return startDate.plusYears(5);
  }

  private static void populateSustainableDevelopmentGoals(
      ProjectEntityBuilder builder, List<SustainableDevelopmentGoal> goals) {
    builder
        .sdg1("")
        .sdg2("")
        .sdg3("")
        .sdg4("")
        .sdg5("")
        .sdg6("")
        .sdg7("")
        .sdg8("")
        .sdg9("")
        .sdg10("")
        .sdg11("")
        .sdg12("")
        .sdg13("")
        .sdg14("")
        .sdg15("")
        .sdg16("")
        .sdg17("");
    for (int i = 0; i < goals.size(); i++) {
      populateSustainableDevelopmentGoal(builder, goals, i);
    }
  }

  private static void populateSustainableDevelopmentGoal(
      ProjectEntityBuilder builder, List<SustainableDevelopmentGoal> goals, int index) {
    var goal = goals.get(index);
    var code = goal.getCode();
    switch (index) {
      case 0:
        builder.sdg1(code);
        return;
      case 1:
        builder.sdg2(code);
        return;
      case 2:
        builder.sdg3(code);
        return;
      case 3:
        builder.sdg4(code);
        return;
      case 4:
        builder.sdg5(code);
        return;
      case 5:
        builder.sdg6(code);
        return;
      case 6:
        builder.sdg7(code);
        return;
      case 7:
        builder.sdg8(code);
        return;
      case 8:
        builder.sdg9(code);
        return;
      case 9:
        builder.sdg10(code);
        return;
      case 10:
        builder.sdg11(code);
        return;
      case 11:
        builder.sdg12(code);
        return;
      case 12:
        builder.sdg13(code);
        return;
      case 13:
        builder.sdg14(code);
        return;
      case 14:
        builder.sdg15(code);
        return;
      case 15:
        builder.sdg16(code);
        return;
      case 16:
        builder.sdg17(code);
        return;
      default:
        throw new ProjectContractCreationFailedException(
            String.format("sdg code not supported %d %s", goal.getId(), code));
    }
  }

  private static <T extends ReferenceDataType> String toPipeDelimitedStringOfCodes(
      Collection<T> items) {
    if (CollectionUtils.isEmpty(items)) {
      return "";
    }
    return items.stream().map(ReferenceDataType::getCode).collect(Collectors.joining("|"));
  }
}

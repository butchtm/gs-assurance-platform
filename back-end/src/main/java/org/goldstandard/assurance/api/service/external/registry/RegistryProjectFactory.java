package org.goldstandard.assurance.api.service.external.registry;

import java.util.Optional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;
import org.goldstandard.assurance.api.service.external.ExternalCountryConverter;
import org.goldstandard.assurance.api.service.external.ExternalCreditingPeriodsConverter;
import org.goldstandard.assurance.api.service.external.ExternalProjectStatusConverter;
import org.goldstandard.assurance.api.service.external.ExternalProjectTypeConverter;

@RequiredArgsConstructor
@Builder
public class RegistryProjectFactory {

  private final String linkTemplate;
  private final ExternalProjectStatusConverter statusConverter;
  private final ExternalCountryConverter countryConverter;
  private final RegistryProjectClassificationConverter classificationConverter;
  private final ExternalProjectTypeConverter typeConverter;
  private final RegistryProjectMethodologyConverter methodologyConverter;
  private final RegistryProjectCreditingPeriodDateConverter creditingPeriodDateConverter;
  private final ExternalCreditingPeriodsConverter creditingPeriodsConverter;

  public RegistryProjectFactory(String linkTemplate) {
    this(
        linkTemplate,
        new ExternalProjectStatusConverter(),
        new ExternalCountryConverter(),
        new RegistryProjectClassificationConverter(),
        new ExternalProjectTypeConverter(),
        new RegistryProjectMethodologyConverter(),
        new RegistryProjectCreditingPeriodDateConverter(),
        new ExternalCreditingPeriodsConverter());
  }

  public RegistryProject build(Project project, Account account) {
    return RegistryProject.builder()
        .id(project.getId())
        .projectId(project.getGsidNum())
        .poaId(
            Optional.ofNullable(project.getMasterProject()).map(Project::getGsidNum).orElse(null))
        .gsidNum(project.getGsidNum())
        .name(toNullIfEmpty(project.getName()))
        .description(toNullIfEmpty(project.getDescription()))
        .status(
            Optional.ofNullable(project.getProjectStatus())
                .map(ReferenceDataType::getCode)
                .map(statusConverter::toExternalProjectStatus)
                .orElse(null))
        .standardsVersion(
            Optional.ofNullable(project.getCurrentStandardVersion())
                .map(ReferenceDataType::getDescription)
                .map(RegistryProjectFactory::toNullIfEmpty)
                .orElse(null))
        .countryCode(
            project.getProjectLocations().stream()
                .findFirst()
                .map(ReferenceDataType::getCode)
                .map(countryConverter::toExternalCountry)
                .orElse(null))
        .estimatedAnnualCredits(project.getEstimatedAnnualUnits())
        .creditingPeriodStartDate(
            creditingPeriodDateConverter.toEarliestCreditingPeriodStartDate(project))
        .creditingPeriodEndDate(
            creditingPeriodDateConverter.toLatestCreditingPeriodEndDate(project))
        .methodology(methodologyConverter.toMethodologyDescription(project))
        .carbonStream(
            Optional.ofNullable(project.getCarbonStream())
                .map(ReferenceDataType::getCode)
                .orElse(null))
        .type(typeConverter.toExternalProjectType(project))
        .size(
            Optional.ofNullable(project.getProjectSize())
                .map(ReferenceDataType::getDescription)
                .map(RegistryProjectFactory::toNullIfEmpty)
                .orElse(null))
        .url(String.format(linkTemplate, project.getGsid()))
        .sustainableDevelopmentGoals(
            project.getSustainableDevelopmentGoals().stream()
                .map(ReferenceDataType::getCode)
                .toList())
        .projectDeveloper(toNullIfEmpty(account.getName()))
        .programOfActivities(
            Optional.ofNullable(project.getProjectClassification())
                .map(ReferenceDataType::getCode)
                .map(classificationConverter::toRegistryClassification)
                .orElse(null))
        .creditingPeriods(
            creditingPeriodsConverter.toExternalCreditingPeriods((project.getCreditingPeriods())))
        .designCertificationDate(project.getDesignCertificationDate())
        .build();
  }

  private static String toNullIfEmpty(String value) {
    if (StringUtils.isEmpty(value)) {
      return null;
    }
    return value;
  }
}

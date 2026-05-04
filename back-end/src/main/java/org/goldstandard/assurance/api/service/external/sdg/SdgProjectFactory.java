package org.goldstandard.assurance.api.service.external.sdg;

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
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Builder
@Component
public class SdgProjectFactory {

  private final ExternalProjectStatusConverter statusConverter;
  private final ExternalCountryConverter countryConverter;
  private final ExternalProjectTypeConverter typeConverter;
  private final SdgProjectClassificationConverter classificationConverter;
  private final ExternalCreditingPeriodsConverter creditingPeriodsConverter;

  public SdgProject build(Project project, Account account) {
    return SdgProject.builder()
        .id(project.getId())
        .gsid(project.getGsid())
        .poaProjectId(
            Optional.ofNullable(project.getMasterProject()).map(Project::getGsidNum).orElse(null))
        .name(project.getName())
        .description(project.getDescription())
        .status(
            Optional.ofNullable(project.getProjectStatus())
                .map(ReferenceDataType::getCode)
                .map(statusConverter::toExternalProjectStatus)
                .orElse(null))
        .countryCodes(countryConverter.toExternalCountries(project.getProjectLocations()))
        .annualEstimatedCredits(project.getEstimatedAnnualUnits())
        .carbonStream(
            Optional.ofNullable(project.getCarbonStream())
                .map(ReferenceDataType::getCode)
                .orElse(null))
        .types(typeConverter.toExternalProjectTypes(project))
        .projectDeveloper(toNullIfEmpty(account.getName()))
        .programOfActivities(
            Optional.ofNullable(project.getProjectClassification())
                .map(ReferenceDataType::getCode)
                .map(classificationConverter::toSdgClassification)
                .orElse(null))
        .creditingPeriods(
            creditingPeriodsConverter.toExternalCreditingPeriods(project.getCreditingPeriods()))
        .build();
  }

  private static String toNullIfEmpty(String value) {
    if (StringUtils.isEmpty(value)) {
      return null;
    }
    return value;
  }
}

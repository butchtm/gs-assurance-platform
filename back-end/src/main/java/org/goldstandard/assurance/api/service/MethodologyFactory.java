package org.goldstandard.assurance.api.service;

import static org.goldstandard.assurance.api.entity.lookup.ReferenceDataType.buildCode;

import java.util.Objects;
import org.goldstandard.assurance.api.dto.MethodologyManagementDTO;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.springframework.stereotype.Component;

@Component
public class MethodologyFactory {

  // suppress sonar false positive for lombok super builder discussed here:
  // https://community.sonarsource.com/t/potential-false-positive-for-s3252-with-lombok-superbuilder/38082
  // the "correct" fix would be to remove methodology from the extended reference inheritance tree
  // but that would require extracting and interface and updating all reference data types which is
  // beyond the scope of the current ticket
  @SuppressWarnings("java:S3252")
  public Methodology create(MethodologyManagementDTO methodology) {
    return Methodology.builder()
        .description(methodology.getDescription())
        .code(buildCode(methodology.getDescription()))
        .isActive(methodology.getActive())
        .paa(methodology.isPaa())
        .build();
  }

  public Methodology update(Methodology existing, MethodologyManagementDTO update) {
    if (Objects.nonNull(update.getDescription())) {
      existing =
          existing.toBuilder()
              .description(update.getDescription())
              .code(buildCode(update.getDescription()))
              .paa(update.isPaa())
              .build();
    }
    if (Objects.nonNull(update.getActive())) {
      existing = existing.toBuilder().isActive(update.getActive()).build();
    }
    return existing;
  }
}

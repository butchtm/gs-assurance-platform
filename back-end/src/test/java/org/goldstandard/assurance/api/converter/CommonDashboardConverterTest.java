package org.goldstandard.assurance.api.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.goldstandard.assurance.api.dto.ProjectClassificationDTO;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.entity.view.CommonDashboardView;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class CommonDashboardConverterTest {

  private final CommonDashboardConverter converter =
      new CommonDashboardConverter(
          new InvoiceStatusConverter(),
          new ProjectStatusConverter(),
          new RequestStatusConverter(),
          new ProjectClassificationConverter(),
          new FeeScheduleConverter());

  static Stream<Arguments> crossLinkClassifications() {
    return Stream.of(
        Arguments.of(
            ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_ID,
            ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_CODE,
            "Real Case VPA - Cross Linking",
            ProjectClassification.REAL_CASE_VPA_ID,
            ProjectClassification.REAL_CASE_VPA_CODE,
            ProjectClassification.REAL_CASE_VPA_DESCRIPTION),
        Arguments.of(
            ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_ID,
            ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_CODE,
            "Regular VPA - Cross Linking",
            ProjectClassification.REGULAR_CASE_VPA_ID,
            ProjectClassification.REGULAR_VPA_CODE,
            ProjectClassification.REGULAR_VPA_DESCRIPTION));
  }

  @ParameterizedTest
  @MethodSource("crossLinkClassifications")
  void shouldNormalizeCrossLinkClassificationInDashboardDto(
      Integer inputId,
      String inputCode,
      String inputDescription,
      Integer expectedId,
      String expectedCode,
      String expectedDescription) {
    var view =
        CommonDashboardView.builder()
            .projectClassification(classification(inputId, inputCode, inputDescription))
            .build();
    var page = new PageImpl<>(java.util.List.of(view), PageRequest.of(0, 1), 1);

    var result = converter.toPagedResource(page);

    ProjectClassificationDTO classification =
        result.getContent().getFirst().getProject().getClassification();
    assertThat(classification.getId()).isEqualTo(expectedId);
    assertThat(classification.getCode()).isEqualTo(expectedCode);
    assertThat(classification.getDescription()).isEqualTo(expectedDescription);
  }

  private static ProjectClassification classification(Integer id, String code, String description) {
    var classification = new ProjectClassification();
    classification.setId(id);
    classification.setCode(code);
    classification.setDescription(description);
    return classification;
  }
}

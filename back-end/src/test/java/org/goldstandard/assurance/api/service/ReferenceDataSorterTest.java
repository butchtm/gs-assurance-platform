package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.goldstandard.assurance.api.entity.lookup.ExtendedReferenceDataType;
import org.junit.jupiter.api.Test;

class ReferenceDataSorterTest {

  private final ReferenceDataSorter sorter = new ReferenceDataSorter();

  @Test
  void shouldSortCarbonStreamsInSpecificOrder() {
    var gsTverPt = typeWithCode("GS_TVER_PT");
    var gsTverIt = typeWithCode("GS_TVER_IT");
    var reportingOnly = typeWithCode("REPORTING_ONLY");
    var gsVer = typeWithCode("GS_VER");
    var gsCer = typeWithCode("GS_CER");
    var key = "CarbonStream";
    var refData = Map.of(key, List.of(gsTverPt, gsTverIt, reportingOnly, gsVer, gsCer));

    var sortedRefData = sorter.sort(refData);

    assertThat(sortedRefData)
        .hasSize(1)
        .containsEntry(key, List.of(gsVer, gsCer, reportingOnly, gsTverIt, gsTverPt));
  }

  @Test
  void shouldSortProjectSizesInSpecificOrder() {
    var large = typeWithCode("LARGE_SCALE");
    var micro = typeWithCode("MICROSCALE");
    var small = typeWithCode("SMALL_SCALE");
    var key = "ProjectSize";
    var refData = Map.of(key, List.of(large, micro, small));

    var sortedRefData = sorter.sort(refData);

    assertThat(sortedRefData).hasSize(1).containsEntry(key, List.of(micro, small, large));
  }

  @Test
  void shouldSortProjectTypesAlphabeticallyByDescriptionWithOtherLast() {
    var otherType = typeWithCode("OTHER");
    var bType = typeWithDescription("b-type");
    var aType = typeWithDescription("a-type");
    var key = "ProjectType";
    var refData = Map.of(key, List.of(otherType, bType, aType));

    var sortedRefData = sorter.sort(refData);

    assertThat(sortedRefData).hasSize(1).containsEntry(key, List.of(aType, bType, otherType));
  }

  @Test
  void shouldSortRequestTypesAlphabeticallyWithSpecificOrderForLastCodes() {
    var issuance = typeWithCode("ISSUANCE");
    var manageProject = typeWithCode("MANAGE_PROJECT_INFORMATION");
    var generic = typeWithCode("GENERIC");
    var preliminaryReview = typeWithDescription("PRELIMINARY_REVIEW");
    var annualReport = typeWithDescription("ANNUAL_REPORT");
    var key = "RequestType";
    var refData =
        Map.of(key, List.of(generic, preliminaryReview, issuance, annualReport, manageProject));

    var sortedRefData = sorter.sort(refData);

    assertThat(sortedRefData)
        .hasSize(1)
        .containsEntry(
            key, List.of(annualReport, preliminaryReview, issuance, manageProject, generic));
  }

  @Test
  void shouldSortDocumentTypesAlphabeticallyByDescriptionWithOtherLast() {
    var otherType = typeWithCode("OTHER");
    var coverLetter = typeWithDescription("COVER_LETTER");
    var monitoringReport = typeWithDescription("MONITORING_REPORT");
    var key = "DocumentType";
    var refData = Map.of(key, List.of(otherType, coverLetter, monitoringReport));

    var sortedRefData = sorter.sort(refData);

    assertThat(sortedRefData)
        .hasSize(1)
        .containsEntry(key, List.of(coverLetter, monitoringReport, otherType));
  }

  @Test
  void shouldSortAnyOtherItemsAlphabeticallyByDescription() {
    var cType = typeWithDescription("c-type");
    var aType = typeWithDescription("a-type");
    var bType = typeWithDescription("b-type");
    var key = "OtherKey";
    var refData = Map.of(key, List.of(cType, aType, bType));

    var sortedRefData = sorter.sort(refData);

    assertThat(sortedRefData).hasSize(1).containsEntry(key, List.of(aType, bType, cType));
  }

  private static ExtendedReferenceDataType typeWithCode(String code) {
    return createType(code, code.toLowerCase());
  }

  private static ExtendedReferenceDataType typeWithDescription(String description) {
    return createType(description.toUpperCase(), description);
  }

  private static ExtendedReferenceDataType createType(String code, String description) {
    return ExtendedReferenceDataType.builder().description(description).code(code).build();
  }
}

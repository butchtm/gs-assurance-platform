package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.goldstandard.assurance.api.dto.AssignmentSummaryFiltersDTO;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;
import org.goldstandard.assurance.api.service.report.AssignmentSummaryFilenameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AssignmentSummaryFilenameGeneratorTest {

  private final Clock clock = mock(Clock.class);

  private final ReferenceDataService refDataService = mock(ReferenceDataService.class);

  private final AssignmentSummaryFilenameGenerator generator =
      new AssignmentSummaryFilenameGenerator(clock, refDataService);

  @BeforeEach
  void setUp() {
    mockTimeNow();
  }

  @Test
  void prefixesFilenameQaQcDescription() {
    var filters = buildFilters();
    when(refDataService.getAssignmentType(filters.getAssignmentTypeId()))
        .thenReturn(typeWithCode("QA/QC"));

    var filename = generator.generateFilename(filters);

    assertThat(filename).startsWith("qa-qc-assignment-summary-report-13102025-053003005.xlsx");
  }

  @Test
  void appendsFormattedCurrentDateToFilename() {
    var filters = buildFilters();
    when(refDataService.getAssignmentType(filters.getAssignmentTypeId()))
        .thenReturn(typeWithCode("REVIEW"));

    var filename = generator.generateFilename(filters);

    assertThat(filename).isEqualTo("review-assignment-summary-report-13102025-053003005.xlsx");
  }

  private void mockTimeNow() {
    var now = ZonedDateTime.of(2025, 10, 13, 5, 30, 3, 5555555, ZoneId.of("UTC"));
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(clock.instant()).thenReturn(now.toInstant());
  }

  private AssignmentSummaryFiltersDTO buildFilters() {
    return AssignmentSummaryFiltersDTO.builder().assignmentTypeId(1).build();
  }

  private ReferenceDataType typeWithCode(String code) {
    return ReferenceDataType.builder().code(code).build();
  }
}

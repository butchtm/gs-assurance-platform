package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.goldstandard.assurance.api.converter.AssignmentSummaryConverter;
import org.goldstandard.assurance.api.dto.AssignmentSummaryDTO;
import org.goldstandard.assurance.api.dto.AssignmentSummaryFiltersDTO;
import org.goldstandard.assurance.api.dto.AssignmentSummaryReport;
import org.goldstandard.assurance.api.dto.AssignmentSummaryResponseDTO;
import org.goldstandard.assurance.api.enumeration.AssignmentSummaryType;
import org.goldstandard.assurance.api.service.report.AssignmentSummaryReportGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class AssignmentSummaryServiceTest {

  @InjectMocks private AssignmentSummaryService service;
  @Mock private AssignmentSummaryFinderFactory finderFactory;
  @Mock private AssignmentSummaryConverter converter;
  @Mock private AssignmentSummaryReportGenerator reportGenerator;

  @Mock private RoeAssignmentSummaryFinder roeSummaryFinder;
  @Mock private ArmAssignmentSummaryFinder armSummaryFinder;

  private static final AssignmentSummaryFiltersDTO FILTERS =
      AssignmentSummaryFiltersDTO.builder().build();
  private static final Pageable PAGEABLE = mock(Pageable.class);
  private static final Page<AssignmentSummaryDTO> SUMMARY_PAGE =
      new PageImpl<>(List.of(mock(AssignmentSummaryDTO.class)), PageRequest.of(0, 10), 1);
  private static final AssignmentSummaryDTO SUMMARY_TOTALS = mock(AssignmentSummaryDTO.class);

  @Test
  void shouldGetRoeAssignmentSummary() {
    var expected = mock(AssignmentSummaryResponseDTO.class);

    when(finderFactory.getSummaryFinder(AssignmentSummaryType.ROE)).thenReturn(roeSummaryFinder);
    when(roeSummaryFinder.getAssignmentSummaryPage(FILTERS, PAGEABLE)).thenReturn(SUMMARY_PAGE);
    when(roeSummaryFinder.getAssignmentsSummaryTotals(FILTERS)).thenReturn(SUMMARY_TOTALS);
    when(converter.toAssignmentSummaryResponse(SUMMARY_PAGE, SUMMARY_TOTALS)).thenReturn(expected);

    var result = service.getReviewAssignmentSummaryResponse(FILTERS, PAGEABLE);

    assertThat(result).isEqualTo(expected);
  }

  @Test
  void shouldGetArmAssignmentSummary() {
    var expected = mock(AssignmentSummaryResponseDTO.class);

    when(finderFactory.getSummaryFinder(AssignmentSummaryType.ARM)).thenReturn(armSummaryFinder);
    when(armSummaryFinder.getAssignmentSummaryPage(FILTERS, PAGEABLE)).thenReturn(SUMMARY_PAGE);
    when(armSummaryFinder.getAssignmentsSummaryTotals(FILTERS)).thenReturn(SUMMARY_TOTALS);
    when(converter.toAssignmentSummaryResponse(SUMMARY_PAGE, SUMMARY_TOTALS)).thenReturn(expected);

    var result = service.getArmAssignmentSummaryResponse(FILTERS, PAGEABLE);

    assertThat(result).isEqualTo(expected);
  }

  @Test
  void shouldGenerateReviewReport() {
    var expected = mock(AssignmentSummaryReport.class);

    when(finderFactory.getSummaryFinder(AssignmentSummaryType.ROE)).thenReturn(roeSummaryFinder);
    when(reportGenerator.generate(FILTERS, PAGEABLE, roeSummaryFinder)).thenReturn(expected);

    var result = service.generateReviewReport(FILTERS, PAGEABLE);

    assertThat(result).isEqualTo(expected);
  }

  @Test
  void shouldGenerateArmReport() {
    var expected = mock(AssignmentSummaryReport.class);

    when(finderFactory.getSummaryFinder(AssignmentSummaryType.ARM)).thenReturn(armSummaryFinder);
    when(reportGenerator.generate(FILTERS, PAGEABLE, armSummaryFinder)).thenReturn(expected);

    var result = service.generateArmReport(FILTERS, PAGEABLE);

    assertThat(result).isEqualTo(expected);
  }
}

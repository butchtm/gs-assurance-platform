package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.goldstandard.assurance.api.converter.AssignmentSummaryConverter;
import org.goldstandard.assurance.api.dto.AssignmentSummaryDTO;
import org.goldstandard.assurance.api.dto.AssignmentSummaryFiltersDTO;
import org.goldstandard.assurance.api.entity.assignmentsummary.RoeAssignmentSummary;
import org.goldstandard.assurance.api.entity.assignmentsummary.RoeAssignmentSummaryTotals;
import org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class RoeAssignmentSummaryFinderTest {

  @InjectMocks RoeAssignmentSummaryFinder summaryFinder;

  @Mock AssignmentSummaryRepository repository;

  @Spy AssignmentSummaryConverter converter;

  @Test
  void getAssignmentSummaryPage() {
    Pageable pageable = Pageable.ofSize(1);
    AssignmentSummaryFiltersDTO filters = AssignmentSummaryFiltersDTO.builder().build();

    var summary1 =
        RoeAssignmentSummary.builder()
            .accountName("Account A")
            .openCount(4)
            .closedCount(10)
            .firstRoundCount(2)
            .inProgressCount(1)
            .build();
    var summary2 =
        RoeAssignmentSummary.builder()
            .accountName("Account B")
            .openCount(3)
            .closedCount(3)
            .firstRoundCount(1)
            .inProgressCount(1)
            .build();
    var summary3 =
        RoeAssignmentSummary.builder()
            .accountName("Account C")
            .openCount(3)
            .closedCount(7)
            .firstRoundCount(1)
            .inProgressCount(1)
            .build();
    var expectedList = List.of(summary1, summary2, summary3);
    Page<RoeAssignmentSummary> expectedPage = new PageImpl<>(expectedList, pageable, 1);
    when(repository.getReviewAssignmentSummary(filters, pageable)).thenReturn(expectedPage);

    var page = summaryFinder.getAssignmentSummaryPage(filters, pageable);

    assertThat(page.getTotalPages()).isEqualTo(1);
    assertThat(page.getContent()).hasSize(3);

    AssignmentSummaryDTO result1 = page.getContent().get(0);
    assertThat(result1.getAccountName()).isEqualTo("Account A");
    assertThat(result1.getOpenRequests()).isEqualTo(4);
    assertThat(result1.getClosedRequests()).isEqualTo(10);
    assertThat(result1.getRoundOneRequests()).isEqualTo(2);
    assertThat(result1.getInProgressRequests()).isEqualTo(1);

    AssignmentSummaryDTO result2 = page.getContent().get(1);
    assertThat(result2.getAccountName()).isEqualTo("Account B");
    assertThat(result2.getOpenRequests()).isEqualTo(3);
    assertThat(result2.getClosedRequests()).isEqualTo(3);
    assertThat(result2.getRoundOneRequests()).isEqualTo(1);
    assertThat(result2.getInProgressRequests()).isEqualTo(1);

    AssignmentSummaryDTO result3 = page.getContent().get(2);
    assertThat(result3.getAccountName()).isEqualTo("Account C");
    assertThat(result3.getOpenRequests()).isEqualTo(3);
    assertThat(result3.getClosedRequests()).isEqualTo(7);
    assertThat(result3.getRoundOneRequests()).isEqualTo(1);
    assertThat(result3.getInProgressRequests()).isEqualTo(1);
  }

  @Test
  void getAssignmentSummaryPage_emptyResult() {
    Pageable pageable = Pageable.ofSize(1);
    AssignmentSummaryFiltersDTO filters = AssignmentSummaryFiltersDTO.builder().build();

    Page<RoeAssignmentSummary> expectedPage = new PageImpl<>(List.of(), pageable, 0);
    when(repository.getReviewAssignmentSummary(filters, pageable)).thenReturn(expectedPage);

    var page = summaryFinder.getAssignmentSummaryPage(filters, pageable);

    assertThat(page.getTotalPages()).isZero();
    assertThat(page.getContent()).isEmpty();
  }

  @Test
  void getAssignmentsSummaryTotals() {
    AssignmentSummaryFiltersDTO filters = AssignmentSummaryFiltersDTO.builder().build();

    var totals =
        RoeAssignmentSummaryTotals.builder()
            .openCount(10)
            .closedCount(20)
            .firstRoundCount(4)
            .inProgressCount(3)
            .build();
    when(repository.getReviewAssignmentSummaryTotals(filters)).thenReturn(Optional.of(totals));

    var result = summaryFinder.getAssignmentsSummaryTotals(filters);

    assertThat(result.getOpenRequests()).isEqualTo(10);
    assertThat(result.getClosedRequests()).isEqualTo(20);
    assertThat(result.getRoundOneRequests()).isEqualTo(4);
    assertThat(result.getInProgressRequests()).isEqualTo(3);
  }

  @Test
  void getAssignmentsSummaryTotals_emptyResult() {
    AssignmentSummaryFiltersDTO filters = AssignmentSummaryFiltersDTO.builder().build();

    when(repository.getReviewAssignmentSummaryTotals(filters)).thenReturn(Optional.empty());

    var result = summaryFinder.getAssignmentsSummaryTotals(filters);

    assertThat(result.getOpenRequests()).isZero();
    assertThat(result.getClosedRequests()).isZero();
    assertThat(result.getRoundOneRequests()).isZero();
    assertThat(result.getInProgressRequests()).isZero();
  }
}

package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.goldstandard.assurance.api.converter.AssignmentSummaryConverter;
import org.goldstandard.assurance.api.dto.AssignmentSummaryDTO;
import org.goldstandard.assurance.api.dto.AssignmentSummaryFiltersDTO;
import org.goldstandard.assurance.api.entity.assignmentsummary.ArmAssignmentSummary;
import org.goldstandard.assurance.api.entity.assignmentsummary.ArmAssignmentSummaryTotals;
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
class ArmAssignmentSummaryFinderTest {

  @InjectMocks ArmAssignmentSummaryFinder summaryFinder;

  @Mock AssignmentSummaryRepository repository;

  @Spy AssignmentSummaryConverter converter;

  @Test
  void getAssignmentSummaryPage() {
    Pageable pageable = Pageable.ofSize(1);
    AssignmentSummaryFiltersDTO filters = AssignmentSummaryFiltersDTO.builder().build();

    var summary1 =
        ArmAssignmentSummary.builder()
            .accountName("Account A")
            .upcomingCount(5)
            .dueCount(3)
            .overdueCount(2)
            .build();
    var summary2 =
        ArmAssignmentSummary.builder()
            .accountName("Account B")
            .upcomingCount(7)
            .dueCount(1)
            .overdueCount(0)
            .build();
    var summary3 =
        ArmAssignmentSummary.builder()
            .accountName("Account C")
            .upcomingCount(2)
            .dueCount(4)
            .overdueCount(3)
            .build();
    var expectedList = List.of(summary1, summary2, summary3);
    Page<ArmAssignmentSummary> expectedPage = new PageImpl<>(expectedList, pageable, 1);
    when(repository.getArmAssignmentSummary(filters, pageable)).thenReturn(expectedPage);

    var page = summaryFinder.getAssignmentSummaryPage(filters, pageable);

    assertThat(page.getTotalPages()).isEqualTo(1);
    assertThat(page.getContent()).hasSize(3);

    AssignmentSummaryDTO result1 = page.getContent().get(0);
    assertThat(result1.getAccountName()).isEqualTo("Account A");
    assertThat(result1.getUpcomingRequests()).isEqualTo(5);
    assertThat(result1.getDueRequests()).isEqualTo(3);
    assertThat(result1.getOverdueRequests()).isEqualTo(2);

    AssignmentSummaryDTO result2 = page.getContent().get(1);
    assertThat(result2.getAccountName()).isEqualTo("Account B");
    assertThat(result2.getUpcomingRequests()).isEqualTo(7);
    assertThat(result2.getDueRequests()).isEqualTo(1);
    assertThat(result2.getOverdueRequests()).isZero();

    AssignmentSummaryDTO result3 = page.getContent().get(2);
    assertThat(result3.getAccountName()).isEqualTo("Account C");
    assertThat(result3.getUpcomingRequests()).isEqualTo(2);
    assertThat(result3.getDueRequests()).isEqualTo(4);
    assertThat(result3.getOverdueRequests()).isEqualTo(3);
  }

  @Test
  void getAssignmentSummaryPage_emptyResult() {
    Pageable pageable = Pageable.ofSize(1);
    AssignmentSummaryFiltersDTO filters = AssignmentSummaryFiltersDTO.builder().build();

    Page<ArmAssignmentSummary> expectedPage = new PageImpl<>(List.of(), pageable, 0);
    when(repository.getArmAssignmentSummary(filters, pageable)).thenReturn(expectedPage);

    var page = summaryFinder.getAssignmentSummaryPage(filters, pageable);

    assertThat(page.getTotalPages()).isZero();
    assertThat(page.getContent()).isEmpty();
  }

  @Test
  void getAssignmentsSummaryTotals() {
    AssignmentSummaryFiltersDTO filters = AssignmentSummaryFiltersDTO.builder().build();

    var totals =
        ArmAssignmentSummaryTotals.builder().upcomingCount(14).dueCount(8).overdueCount(5).build();
    when(repository.getArmAssignmentSummaryTotals(filters)).thenReturn(Optional.of(totals));

    var result = summaryFinder.getAssignmentsSummaryTotals(filters);

    assertThat(result.getUpcomingRequests()).isEqualTo(14);
    assertThat(result.getDueRequests()).isEqualTo(8);
    assertThat(result.getOverdueRequests()).isEqualTo(5);
  }

  @Test
  void getAssignmentsSummaryTotals_emptyResult() {
    AssignmentSummaryFiltersDTO filters = AssignmentSummaryFiltersDTO.builder().build();

    when(repository.getArmAssignmentSummaryTotals(filters)).thenReturn(Optional.empty());

    var result = summaryFinder.getAssignmentsSummaryTotals(filters);

    assertThat(result.getOpenRequests()).isZero();
    assertThat(result.getClosedRequests()).isZero();
    assertThat(result.getRoundOneRequests()).isZero();
    assertThat(result.getInProgressRequests()).isZero();
  }
}

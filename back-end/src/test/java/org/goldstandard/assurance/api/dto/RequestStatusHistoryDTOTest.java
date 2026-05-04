package org.goldstandard.assurance.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.goldstandard.assurance.api.entity.RequestStatusHistory;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class RequestStatusHistoryDTOTest {

  @ParameterizedTest
  @EnumSource(MappingTestCase.class)
  void testMapDTOFromEntity(MappingTestCase testCase) {
    final RequestStatusHistory requestStatusHistory =
        RequestStatusHistory.builder()
            .id(testCase.getId())
            .previousStatus(
                testCase.getPriorStatusId() != null
                    ? new RequestStatus(testCase.getPriorStatusId())
                    : null)
            .nextStatus(
                testCase.getNextStatusId() != null
                    ? new RequestStatus(testCase.getNextStatusId())
                    : null)
            .changedAt(testCase.getChangedAt())
            .build();

    final RequestStatusHistoryDTO requestStatusHistoryDTO =
        RequestStatusHistoryDTO.fromRequestStatusHistory(requestStatusHistory);

    assertThat(requestStatusHistoryDTO.getId()).isEqualTo(testCase.getId());
    assertThat(requestStatusHistoryDTO.getPreviousStatusId())
        .isEqualTo(testCase.getPriorStatusId());
    assertThat(requestStatusHistoryDTO.getNextStatusId()).isEqualTo(testCase.getNextStatusId());
    assertThat(requestStatusHistoryDTO.getChangedAt()).isEqualTo(testCase.getChangedAt());
  }

  @Getter
  @AllArgsConstructor
  public enum MappingTestCase {
    NULL_PRIOR_STATUS(1, null, 3, ZonedDateTime.now()),
    NULL_NEXT_STATUS(2, 3, null, ZonedDateTime.now()),
    BOTH_STATUSES_SET(3, 2, 3, ZonedDateTime.now());
    private final Integer id;
    private final Integer priorStatusId;
    private final Integer nextStatusId;
    private final ZonedDateTime changedAt;
  }
}

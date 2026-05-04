package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.goldstandard.assurance.api.service.AuditEventSubject.REQUEST_ROUND_UPDATE;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.time.ZonedDateTime;
import java.util.Map;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestRound;
import org.goldstandard.assurance.api.entity.lookup.RequestRoundStatus;
import org.goldstandard.assurance.api.repository.RequestRoundRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestRoundTransactionTest {

  private static final ZonedDateTime START_DATE = ZonedDateTime.now();
  private static final ZonedDateTime END_DATE = START_DATE.plusDays(10);

  @InjectMocks RequestRoundTransaction transaction;

  @Mock RequestRoundRepository roundRepository;

  @Mock AuditService auditService;

  @Captor ArgumentCaptor<RequestRound> roundCaptor;

  @Captor ArgumentCaptor<Map<String, Object>> auditInfoCaptor;

  @Test
  void shouldUpdateRound() {
    var round = buildRound();

    transaction.updateRequestRound(round);

    verify(roundRepository).save(roundCaptor.capture());
    var savedRound = roundCaptor.getValue();
    assertThat(savedRound).isEqualTo(round);
  }

  @Test
  void shouldAuditRoundUpdated() {
    var round = buildRound();

    transaction.updateRequestRound(round);

    verify(auditService).auditEvent(eq(REQUEST_ROUND_UPDATE), auditInfoCaptor.capture());
    var auditInfo = auditInfoCaptor.getValue();
    assertThat(auditInfo)
        .hasSize(6)
        .contains(
            entry("requestId", round.getRequest().getId()),
            entry("roundId", round.getId()),
            entry("index", round.getRoundIndex()),
            entry("startDate", round.getStartDate().toString()),
            entry("endDate", round.getEndDate().toString()),
            entry("statusId", round.getStatus().getId()));
  }

  private RequestRound buildRound() {
    return RequestRound.builder()
        .id(88)
        .roundIndex(1)
        .status(new RequestRoundStatus(RequestRoundStatus.OPEN))
        .request(Request.builder().id(99).build())
        .startDate(START_DATE)
        .endDate(END_DATE)
        .build();
  }
}

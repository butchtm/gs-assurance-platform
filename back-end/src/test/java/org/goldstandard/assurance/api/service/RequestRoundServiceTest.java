package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.goldstandard.assurance.api.entity.lookup.RequestRoundStatus.CLOSED;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.Optional;
import org.goldstandard.assurance.api.dto.RequestRoundUpdateDTO;
import org.goldstandard.assurance.api.entity.RequestRound;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.repository.RequestRoundRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestRoundServiceTest {

  private static final Integer REQUEST_ID = 54753;
  private static final Integer ROUND_INDEX = 2;
  private static final ZonedDateTime START_DATE = ZonedDateTime.now();
  private static final ZonedDateTime END_DATE = START_DATE.plusDays(10);

  @InjectMocks RequestRoundService service;

  @Mock RequestRoundRepository repository;

  @Mock RequestRoundTransaction transaction;

  @Captor ArgumentCaptor<RequestRound> captor;

  @Test
  void testUpdateRound() {
    when(repository.findByRequestIdAndRoundIndex(REQUEST_ID, ROUND_INDEX))
        .thenReturn(Optional.of(RequestRound.builder().build()));
    var dto = new RequestRoundUpdateDTO(START_DATE, END_DATE, CLOSED);

    service.updateRequestRound(REQUEST_ID, ROUND_INDEX, dto);

    verify(transaction).updateRequestRound(captor.capture());
    var savedRound = captor.getValue();
    assertThat(savedRound.getStatus().getId()).isEqualTo(dto.getStatusId());
    assertThat(savedRound.getStartDate()).isEqualTo(dto.getStartDate());
    assertThat(savedRound.getEndDate()).isEqualTo(dto.getEndDate());
  }

  @Test
  void testUpdateRoundErrorsIfRequestRoundNotFound() {
    when(repository.findByRequestIdAndRoundIndex(REQUEST_ID, ROUND_INDEX))
        .thenReturn(Optional.empty());
    var dto = new RequestRoundUpdateDTO(START_DATE, END_DATE, CLOSED);

    var error = catchThrowable(() -> service.updateRequestRound(REQUEST_ID, ROUND_INDEX, dto));

    assertThat(error)
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Round %d for request %d not found", ROUND_INDEX, REQUEST_ID);
  }
}

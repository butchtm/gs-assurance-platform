package org.goldstandard.assurance.api.service.finance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FinanceObjectPathFactoryTest {

  private final Clock clock = mock(Clock.class);

  private final FinanceObjectPathFactory factory = new FinanceObjectPathFactory(clock);

  @BeforeEach
  void setUp() {
    mockTimeNow();
  }

  @Test
  void shouldReturnSalesOrderPath() {
    var requestId = 999;

    var path = factory.toSalesOrderPath(requestId);

    assertThat(path).isEqualTo("Inprocess/sales-order-999-29-09-2025-11-58-12-494Z.xml");
  }

  @Test
  void shouldReturnProjectContractPath() {
    var gsId = "GS1234";

    var path = factory.toProjectContractPath(gsId);

    assertThat(path).isEqualTo("Inprocess/project-contract-GS1234-29-09-2025-11-58-12-494Z.xml");
  }

  private void mockTimeNow() {
    var now = ZonedDateTime.parse("2025-09-29T11:58:12.4948Z");
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(clock.instant()).thenReturn(now.toInstant());
  }
}

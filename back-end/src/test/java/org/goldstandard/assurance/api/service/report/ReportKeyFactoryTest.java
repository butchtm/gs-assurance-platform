package org.goldstandard.assurance.api.service.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.goldstandard.assurance.api.entity.User;
import org.junit.jupiter.api.Test;

class ReportKeyFactoryTest {

  private final Clock clock = mock(Clock.class);

  private final User user = mock(User.class);

  private final ReportKeyFactory factory = new ReportKeyFactory(clock);

  @Test
  void shouldGenerateFilenameWithCurrentTime() {
    mockTimeNow();
    when(user.getUserName()).thenReturn("mruocco");

    var key = factory.buildCommonDashboardKey(user);

    assertThat(key).isEqualTo("mruocco/common-dashboard-report-14102025-010203444.xlsx");
  }

  private void mockTimeNow() {
    var now = ZonedDateTime.of(2025, 10, 14, 1, 2, 3, 444444444, ZoneId.of("UTC"));
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(clock.instant()).thenReturn(now.toInstant());
  }
}

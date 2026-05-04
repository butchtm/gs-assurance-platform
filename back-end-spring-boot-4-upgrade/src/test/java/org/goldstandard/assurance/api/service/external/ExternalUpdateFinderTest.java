package org.goldstandard.assurance.api.service.external;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import org.goldstandard.assurance.api.entity.ExternalSystemProjectUpdate;
import org.goldstandard.assurance.api.repository.ExternalSystemProjectUpdateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

@ExtendWith(MockitoExtension.class)
class ExternalUpdateFinderTest {

  @InjectMocks ExternalUpdateFinder finder;

  @Mock ExternalUpdateConfig config;

  @Mock Clock clock;

  @Mock ExternalSystemProjectUpdateRepository repository;

  @Test
  void shouldFindPageOfUpdates() {
    var maxRequests = givenMaxRequests();
    var cutoff = givenCutoff();
    var expectedPage = new PageImpl<>(List.of(mock(ExternalSystemProjectUpdate.class)));
    var system = givenExternalSystem();
    when(repository.getPageOfUpdates(maxRequests, cutoff, system)).thenReturn(expectedPage);

    var page = finder.getPageOfUpdates();

    assertThat(page).isEqualTo(expectedPage);
  }

  private int givenMaxRequests() {
    var maxRequests = 5;
    when(config.getMaxRequests()).thenReturn(maxRequests);
    return maxRequests;
  }

  private ZonedDateTime givenCutoff() {
    var now = mockTimeNow();
    var retryInterval = Duration.ofSeconds(30);
    when(config.getRetryInterval()).thenReturn(retryInterval);
    return now.minus(retryInterval);
  }

  private ZonedDateTime mockTimeNow() {
    var now = ZonedDateTime.now();
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(clock.instant()).thenReturn(now.toInstant());
    return now;
  }

  private ExternalSystem givenExternalSystem() {
    var system = ExternalSystem.REGISTRY;
    when(config.getSystem()).thenReturn(system);
    return system;
  }
}

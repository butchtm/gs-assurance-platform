package org.goldstandard.assurance.api.service.pub;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
@RequiredArgsConstructor
public class RateLimitResetter {

  private final Collection<PublicRateLimitService> services;

  public void resetAll() {
    services.forEach(PublicRateLimitService::reset);
  }
}

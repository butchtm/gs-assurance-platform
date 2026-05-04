package org.goldstandard.assurance.api.service.external;

import java.time.Duration;
import java.util.Collection;

public interface ExternalUpdateConfig {

  Duration getInterval();

  Duration getRetryInterval();

  Integer getMaxRequests();

  Integer getMaxRetries();

  Collection<Integer> getAbortCodes();

  ExternalSystem getSystem();
}

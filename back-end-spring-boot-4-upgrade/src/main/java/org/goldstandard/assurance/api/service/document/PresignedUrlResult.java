package org.goldstandard.assurance.api.service.document;

import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PresignedUrlResult {

  private final String key;
  private final ZonedDateTime expiry;
  private final String checksum;
  private final String url;
}

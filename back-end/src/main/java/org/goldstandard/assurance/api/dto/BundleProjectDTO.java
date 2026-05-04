package org.goldstandard.assurance.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Builder
public class BundleProjectDTO {

  private final Integer id;
  private final String gsid;
  private final String name;
  private final Request currentRequest;

  @Getter
  @NoArgsConstructor(force = true)
  @RequiredArgsConstructor
  @Builder
  public static class Request {

    private final Integer id;
    private final String name;
  }
}

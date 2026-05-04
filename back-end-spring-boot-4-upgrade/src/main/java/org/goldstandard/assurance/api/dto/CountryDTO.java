package org.goldstandard.assurance.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public class CountryDTO {
  private final Integer id;
  private final String countryCode;
  private final String name;
}

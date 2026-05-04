package org.goldstandard.assurance.api.dto;

import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import org.goldstandard.assurance.api.util.SheetLocations;

@Builder
@Getter
public class ExtractedText {
  private final Integer pageNumber;
  private final String text;
  private final SheetLocations sheetLocations;

  public SheetLocations getSheetLocations() {
    return Optional.ofNullable(sheetLocations).orElseGet(SheetLocations::new);
  }
}

package org.goldstandard.assurance.api.util;

import java.util.HashMap;
import java.util.Map;

public class SheetLocations extends HashMap<Offsets, String> {

  public SheetLocations() {
    super();
  }

  public SheetLocations(SheetLocations locations) {
    super(locations);
  }

  public String getLocation(int startOffset, int endOffset) {
    return getLocation(new Offsets(startOffset, endOffset));
  }

  private String getLocation(Offsets offsets) {
    return this.entrySet().stream()
        .filter(location -> location.getKey().matches(offsets))
        .map(Map.Entry::getValue)
        .findFirst()
        .orElse(null);
  }
}

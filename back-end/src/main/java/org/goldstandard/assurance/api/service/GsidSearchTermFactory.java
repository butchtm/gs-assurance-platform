package org.goldstandard.assurance.api.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GsidSearchTermFactory {

  public static String toGsidSearchTerm(String input) {
    var lowerInput = input.toLowerCase();
    var prefix = "gs";
    if (!lowerInput.startsWith(prefix)) {
      return String.format("%s%s", prefix, lowerInput);
    }
    return lowerInput;
  }

  public static String toLowerCaseWithWildcards(String input) {
    return StringUtils.wrap(input.toLowerCase(), "%");
  }
}

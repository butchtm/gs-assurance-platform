package org.goldstandard.assurance.api.util;

import static org.apache.commons.lang3.StringUtils.stripEnd;
import static org.apache.commons.lang3.StringUtils.stripStart;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomStringUtils {

  public static String replaceSpecialCharsForUnderscores(String string) {
    return string.replaceAll("([^a-zA-Z0-9]+)", "_");
  }

  public static String trimLeadingOrTrailingUnderscores(String string) {
    var temp = stripEnd(string, "_");
    return stripStart(temp, "_");
  }
}

package org.goldstandard.assurance.api.service.document;

import java.net.URI;
import org.springframework.stereotype.Component;

@Component
public class PathPrefixHandler {

  public String toPrefix(String input) {
    return removeLeadingSlashFromPathIfPresent(input);
  }

  private String removeLeadingSlashFromPathIfPresent(String input) {
    var uri = URI.create(input);
    var path = uri.getPath();
    if (path.startsWith("/")) {
      return path.substring(1);
    }
    return path;
  }
}

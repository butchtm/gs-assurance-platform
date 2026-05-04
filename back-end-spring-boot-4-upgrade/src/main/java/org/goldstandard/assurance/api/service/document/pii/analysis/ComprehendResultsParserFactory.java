package org.goldstandard.assurance.api.service.document.pii.analysis;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ComprehendResultsParserFactory {
  private final List<ComprehendResultsParser> parsers;

  public ComprehendResultsParser getParser(String contentType) {
    return parsers.stream()
        .filter(parser -> parser.supported(contentType))
        .findFirst()
        .orElse(null);
  }
}

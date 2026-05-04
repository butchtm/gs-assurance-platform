package org.goldstandard.assurance.api.service.comprehend;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.goldstandard.assurance.api.service.document.pii.analysis.ComprehendResultsParserFactory;
import org.goldstandard.assurance.api.service.document.pii.analysis.TextractComprehendResultsParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ComprehendResultsParserFactoryTest {

  private ComprehendResultsParserFactory factory;
  private TextractComprehendResultsParser textractParser;

  @BeforeEach()
  void setup() {
    textractParser = new TextractComprehendResultsParser();
    factory = new ComprehendResultsParserFactory(List.of(textractParser));
  }

  @Test
  void shouldReturnParserSupportingContentType() {
    assertThat(factory.getParser(MediaType.APPLICATION_PDF_VALUE)).isEqualTo(textractParser);
  }
}

package org.goldstandard.assurance.api.service.comprehend;

import static org.assertj.core.api.Assertions.assertThat;

import org.goldstandard.assurance.api.fixtures.ComprehendOutputMother;
import org.goldstandard.assurance.api.service.document.pii.analysis.TextractComprehendResultsParser;
import org.goldstandard.assurance.api.util.DocumentUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class TextractComprehendResultsParserTest {

  private TextractComprehendResultsParser parser;

  @BeforeEach
  void setup() {
    parser = new TextractComprehendResultsParser();
  }

  @Test
  void shouldBuildProbablePiiWithPageNumberFromFileName() {
    var ouptutList = ComprehendOutputMother.buildList(3, "ADDRESS");

    var parsedPii = parser.parse(ouptutList).stream().toList();

    assertThat(parsedPii.getFirst().getPageNumber()).isEqualTo(3);
    assertThat(parsedPii.getFirst().getEntity()).isEqualTo("ADDRESS");
  }

  @Test
  void shouldSupportTextractSupportFileTypes() {
    assertThat(parser.supported(MediaType.APPLICATION_PDF_VALUE)).isTrue();
    assertThat(parser.supported(DocumentUtils.IMAGE_TIFF)).isTrue();
    assertThat(parser.supported(MediaType.IMAGE_JPEG_VALUE)).isTrue();
    assertThat(parser.supported(MediaType.IMAGE_PNG_VALUE)).isTrue();
  }
}

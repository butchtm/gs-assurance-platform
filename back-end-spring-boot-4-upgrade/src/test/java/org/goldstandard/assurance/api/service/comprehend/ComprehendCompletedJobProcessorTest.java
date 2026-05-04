package org.goldstandard.assurance.api.service.comprehend;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.goldstandard.assurance.api.fixtures.ComprehendOutputMother;
import org.goldstandard.assurance.api.fixtures.DocumentMother;
import org.goldstandard.assurance.api.fixtures.ProbablePiiMother;
import org.goldstandard.assurance.api.service.document.DocumentService;
import org.goldstandard.assurance.api.service.document.pii.analysis.ComprehendResultsParserFactory;
import org.goldstandard.assurance.api.service.document.pii.analysis.DocumentPiiResultsProcessor;
import org.goldstandard.assurance.api.service.document.pii.analysis.TextractComprehendResultsParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import software.amazon.awssdk.services.comprehend.model.PiiEntitiesDetectionJobProperties;
import software.amazon.awssdk.services.comprehend.model.PiiOutputDataConfig;

@ExtendWith(MockitoExtension.class)
class ComprehendCompletedJobProcessorTest {

  @InjectMocks private ComprehendCompletedJobProcessor completedJobProcessor;
  @Mock private DocumentService documentService;
  @Mock private ComprehendResultsRetriever resultsRetriever;
  @Mock private ComprehendResultsParserFactory parserFactory;
  @Mock private TextractComprehendResultsParser textractParser;
  @Mock private DocumentPiiResultsProcessor resultsProcessor;

  private static final UUID DOCUMENT_ID = UUID.randomUUID();

  @Test
  void shouldRetrieveAndProcessJobResults() {
    var document = DocumentMother.withContentType(DOCUMENT_ID, MediaType.APPLICATION_PDF_VALUE);
    var completedJobOutput = PiiOutputDataConfig.builder().build();
    var completedJob =
        PiiEntitiesDetectionJobProperties.builder().outputDataConfig(completedJobOutput).build();
    var retrievedOutput = ComprehendOutputMother.buildList(3, "ADDRESS");
    var parsedPii = ProbablePiiMother.buildList();

    when(documentService.forceGetDocument(DOCUMENT_ID)).thenReturn(document);
    when(resultsRetriever.retrieve(completedJobOutput)).thenReturn(retrievedOutput);
    when(parserFactory.getParser(document.getContentType())).thenReturn(textractParser);
    when(textractParser.parse(retrievedOutput)).thenReturn(parsedPii);

    completedJobProcessor.processJobOutput(DOCUMENT_ID, completedJob);

    verify(documentService).forceGetDocument(DOCUMENT_ID);
    verify(resultsRetriever).retrieve(completedJobOutput);
    verify(parserFactory).getParser(document.getContentType());
    verify(textractParser).parse(retrievedOutput);
    verify(resultsProcessor).processResults(parsedPii, document);
  }
}

package org.goldstandard.assurance.api.service.comprehend;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.UUID;
import org.goldstandard.assurance.api.fixtures.DocumentMother;
import org.goldstandard.assurance.api.fixtures.ProbablePiiMother;
import org.goldstandard.assurance.api.service.document.pii.analysis.DocumentPiiAnalysisResults;
import org.goldstandard.assurance.api.service.document.pii.analysis.DocumentPiiAnalysisResultsFactory;
import org.goldstandard.assurance.api.service.document.pii.analysis.DocumentPiiDbService;
import org.goldstandard.assurance.api.service.document.pii.analysis.DocumentPiiResultsProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

@ExtendWith(MockitoExtension.class)
class DocumentPiiResultsProcessorTest {
  @InjectMocks private DocumentPiiResultsProcessor resultsProcessor;
  @Mock private DocumentPiiDbService documentPiiDbService;
  @Mock private DocumentPiiAnalysisResultsFactory resultsFactory;

  private static final UUID DOCUMENT_ID = UUID.randomUUID();

  @Test
  void shouldBuildAndSaveResultsDetailsIfPiiPresent() {
    var pii = ProbablePiiMother.buildList();
    var document = DocumentMother.withContentType(DOCUMENT_ID, MediaType.APPLICATION_PDF_VALUE);

    var resultDetails = DocumentPiiAnalysisResults.builder().build();

    when(resultsFactory.build(document, pii)).thenReturn(resultDetails);

    resultsProcessor.processResults(pii, document);

    verify(resultsFactory).build(document, pii);
    verify(documentPiiDbService).saveAnalysisResults(resultDetails);
  }

  @Test
  void shouldUpdateToCompletedNoPiiFoundStatusIfPiiNotPresent() {
    var document = DocumentMother.withContentType(DOCUMENT_ID, MediaType.APPLICATION_PDF_VALUE);

    resultsProcessor.processResults(Collections.emptyList(), document);

    verify(documentPiiDbService).updateToCompletedNoPiiStatus(DOCUMENT_ID);

    verifyNoInteractions(resultsFactory);
  }
}

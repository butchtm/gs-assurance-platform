package org.goldstandard.assurance.api.service.document.pii.analysis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.view.GroupedDocumentPiiView;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.service.document.DocumentFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class DocumentPiiAnalysisServiceTest {
  private static final UUID DOCUMENT_ID = UUID.randomUUID();

  @InjectMocks DocumentPiiAnalysisService service;

  @Mock DocumentFinder documentFinder;
  @Mock DocumentPiiAnalyserFactory analyserFactory;
  @Mock DocumentPiiAnalysisRunner analysisRunner;
  @Mock DocumentPiiDbService documentPiiDbService;
  @Mock TextractDocumentPiiAnalyser textractAnalyser;

  @Test
  void shouldReturnTrueIfAnalyserExistsForCriteria() {
    var criteria = mock(DocumentPiiScanCriteria.class);
    when(analyserFactory.getAnalyser(criteria)).thenReturn(Optional.of(textractAnalyser));

    var scannable = service.isDocumentScannable(criteria);

    assertThat(scannable).isTrue();
  }

  @Test
  void shouldReturnFalseIfAnalyserDoesNotExistsForCriteria() {
    var criteria = mock(DocumentPiiScanCriteria.class);

    var scannable = service.isDocumentScannable(criteria);

    assertThat(scannable).isFalse();
  }

  @Test
  void shouldStartAnalysisIfAnalyserExistsForDocument() {
    var document = mock(Document.class);
    when(documentFinder.getDocument(DOCUMENT_ID)).thenReturn(document);
    when(analyserFactory.getAnalyser(document)).thenReturn(Optional.of(textractAnalyser));

    service.startAnalysis(DOCUMENT_ID);

    verify(analysisRunner).runAnalysis(document, textractAnalyser);
  }

  @Test
  void shouldNotStartAnalysisIfAnalyserDoesNotExistForDocument() {
    when(documentFinder.getDocument(DOCUMENT_ID)).thenReturn(mock(Document.class));

    service.startAnalysis(DOCUMENT_ID);

    verifyNoInteractions(analysisRunner);
  }

  @Test
  void shouldNotStartAnalysisIfDocumentWasNotFound() {
    var expectedError = new NotFoundException("document %s not found", DOCUMENT_ID);
    when(documentFinder.getDocument(DOCUMENT_ID)).thenThrow(expectedError);

    var error = catchThrowable(() -> service.startAnalysis(DOCUMENT_ID));

    assertThat(error).isEqualTo(expectedError);
    verifyNoInteractions(analysisRunner);
  }

  @Test
  void shouldReturnPageOfGroupedProbablePii() {
    var pageable = PageRequest.of(0, 5);
    var expectedPage = toPage(pageable, List.of(mock(GroupedDocumentPiiView.class)));
    when(documentPiiDbService.getProbablePiiPage(DOCUMENT_ID, pageable)).thenReturn(expectedPage);

    var page = service.getPiiResultsPage(DOCUMENT_ID, pageable);

    assertThat(page).isEqualTo(expectedPage);
  }

  private static <T> Page<T> toPage(Pageable pageable, Collection<T> items) {
    var pageItems =
        items.stream().skip(pageable.getOffset()).limit(pageable.getPageSize()).toList();
    return new PageImpl<>(pageItems, pageable, items.size());
  }
}

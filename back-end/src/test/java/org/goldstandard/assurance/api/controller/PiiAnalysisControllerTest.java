package org.goldstandard.assurance.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.goldstandard.assurance.api.converter.PiiAnalysisConverter;
import org.goldstandard.assurance.api.dto.DocumentPiiScanCriteriaDTO;
import org.goldstandard.assurance.api.entity.view.GroupedDocumentPiiView;
import org.goldstandard.assurance.api.service.document.pii.analysis.DocumentPiiAnalysisService;
import org.goldstandard.assurance.api.service.document.pii.analysis.DocumentPiiScanCriteria;
import org.goldstandard.assurance.api.service.document.pii.analysis.PageGroupedPii;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class PiiAnalysisControllerTest {

  @InjectMocks PiiAnalysisController controller;
  @Mock PiiAnalysisConverter converter;
  @Mock DocumentPiiAnalysisService piiAnalysisService;

  private static final UUID DOCUMENT_ID = UUID.fromString("83e31c05-a2f2-49b2-befc-69fe12047efb");

  @Test
  void shouldReturnTrueIfCriteriaSupportedForScan() {
    var scanCriteria = mock(DocumentPiiScanCriteria.class);
    when(converter.toAnalysisCriteria(any(DocumentPiiScanCriteriaDTO.class)))
        .thenReturn(scanCriteria);
    when(piiAnalysisService.isDocumentScannable(scanCriteria)).thenReturn(true);

    assertThat(
            controller.isDocumentSupported(DocumentPiiScanCriteriaDTO.builder().build()).getBody())
        .isTrue();

    verify(piiAnalysisService).isDocumentScannable(scanCriteria);
  }

  @Test
  void shouldReturnFalseIfCriteriaNotSupportedForScan() {
    var scanCriteria = mock(DocumentPiiScanCriteria.class);
    when(converter.toAnalysisCriteria(any(DocumentPiiScanCriteriaDTO.class)))
        .thenReturn(scanCriteria);
    when(piiAnalysisService.isDocumentScannable(scanCriteria)).thenReturn(false);

    assertThat(
            controller.isDocumentSupported(DocumentPiiScanCriteriaDTO.builder().build()).getBody())
        .isFalse();

    verify(piiAnalysisService).isDocumentScannable(scanCriteria);
  }

  @Test
  void shouldReturnAnalysisResults() {
    var page = new PageImpl<>(List.of(mock(GroupedDocumentPiiView.class)), PageRequest.of(0, 1), 1);
    when(piiAnalysisService.getPiiResultsPage(eq(DOCUMENT_ID), any(Pageable.class)))
        .thenReturn(page);
    when(converter.toPageGroupedPii(any(GroupedDocumentPiiView.class)))
        .thenReturn(mock(PageGroupedPii.class));

    assertThat(controller.getAnalysisResults(DOCUMENT_ID, mock(Pageable.class)).getContent())
        .hasSize(1);

    verify(piiAnalysisService).getPiiResultsPage(eq(DOCUMENT_ID), any(Pageable.class));
  }
}

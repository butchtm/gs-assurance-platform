package org.goldstandard.assurance.api.service.document.pii.analysis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.UUID;
import org.goldstandard.assurance.api.dto.ProbablePii;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.fixtures.DocumentMother;
import org.goldstandard.assurance.api.fixtures.ProbablePiiMother;
import org.goldstandard.assurance.api.fixtures.RequestMother;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentPiiAnalysisResultsFactoryTest {

  @InjectMocks private DocumentPiiAnalysisResultsFactory factory;
  @Mock private RequestFinder requestFinder;

  private static final Document DOCUMENT = DocumentMother.document(UUID.randomUUID(), 1);
  private static final Request REQUEST = RequestMother.build();
  private static final Collection<ProbablePii> PROBABLE_PII = ProbablePiiMother.buildList();

  @Test
  void shouldBuildResultsWithRequestAndProject() {
    when(requestFinder.getRequestAssociatedToDocument(DOCUMENT.getId())).thenReturn(REQUEST);

    var results = factory.build(DOCUMENT, PROBABLE_PII);

    assertThat(results.getDocument()).isEqualTo(DOCUMENT);
    assertThat(results.getRequest()).isEqualTo(REQUEST);
    assertThat(results.getProject()).isEqualTo(REQUEST.getProject());
    assertThat(results.getProbablePii()).isEqualTo(PROBABLE_PII);
  }
}

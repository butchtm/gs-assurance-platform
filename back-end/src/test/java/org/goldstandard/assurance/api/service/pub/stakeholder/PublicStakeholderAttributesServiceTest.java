package org.goldstandard.assurance.api.service.pub.stakeholder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.service.pub.stakeholder.PublicStakeholderAttributesService.ANONYMOUS_USERNAME;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.goldstandard.assurance.api.converter.ProjectCreditingPeriodConverter;
import org.goldstandard.assurance.api.dto.CreditingPeriodDTO;
import org.goldstandard.assurance.api.dto.pub.stakeholder.project.PublicStakeholderProjectDetailDTO;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.goldstandard.assurance.api.entity.view.stakeholder.attributes.RequestCommentView;
import org.goldstandard.assurance.api.entity.view.stakeholder.attributes.RequestDocumentView;
import org.goldstandard.assurance.api.repository.ProjectCreditingPeriodRepository;
import org.goldstandard.assurance.api.repository.pub.stakeholder.PublicStakeholderCommentViewRepository;
import org.goldstandard.assurance.api.repository.pub.stakeholder.PublicStakeholderDocumentsViewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublicStakeholderAttributesServiceTest {

  private static final Integer REQUEST_ID = 100;
  private static final Integer PROJECT_ID = 200;
  private static final UUID DOCUMENT_ID = UUID.randomUUID();
  private static final RequestCommentView COMMENT_VIEW =
      RequestCommentView.builder()
          .id(1)
          .requestId(REQUEST_ID)
          .submittedComment("Best comment ever")
          .build();
  private static final RequestDocumentView DOCUMENT_VIEW =
      RequestDocumentView.builder()
          .documentId(DOCUMENT_ID)
          .requestId(REQUEST_ID)
          .filename("test_file.txt")
          .build();

  @InjectMocks PublicStakeholderAttributesService service;
  @Mock PublicStakeholderDocumentsViewRepository documentsViewRepository;
  @Mock PublicStakeholderCommentViewRepository commentViewRepository;
  @Mock ProjectCreditingPeriodRepository creditingPeriodRepository;
  @Mock ProjectCreditingPeriodConverter creditingPeriodConverter;

  @Test
  void projectCreditingPeriodsSetCorrectly() {
    var expectedCreditingPeriods = mockProjectCreditingPeriods();

    var project =
        service.setProjectCreditingPeriods(new PublicStakeholderProjectDetailDTO(), PROJECT_ID);

    assertThat(project.getProjectCreditingPeriods()).isEqualTo(expectedCreditingPeriods);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void shouldReturnAssociatedComments(boolean anonymous) {
    COMMENT_VIEW.setAnonymous(anonymous);
    var comments = List.of(COMMENT_VIEW);
    mockComments();
    when(commentViewRepository.findByRequestId(REQUEST_ID)).thenReturn(comments);

    var result = service.getRequestComments(REQUEST_ID);

    assertThat(result).hasSize(comments.size());
    assertThat(result.getFirst().getSubmittedComment())
        .isEqualTo(COMMENT_VIEW.getSubmittedComment());
    assertThat(result.getFirst().getUploadedDate()).isEqualTo(COMMENT_VIEW.getUploadedDate());
    if (anonymous) {
      assertThat(result.getFirst().getUploadedByName()).isEqualTo(ANONYMOUS_USERNAME);
    } else {
      assertThat(result.getFirst().getUploadedByName()).isEqualTo(COMMENT_VIEW.getUploadedByName());
    }
  }

  @Test
  void shouldReturnAssociatedDocuments() {
    var documents = List.of(DOCUMENT_VIEW);
    mockDocuments();
    when(documentsViewRepository.findByRequestId(REQUEST_ID)).thenReturn(documents);

    var result = service.getRequestDocuments(REQUEST_ID);

    assertThat(result).hasSize(documents.size());
    assertThat(result.getFirst().getId()).isEqualTo(DOCUMENT_ID);
    assertThat(result.getFirst().getFilename()).isEqualTo(DOCUMENT_VIEW.getFilename());
  }

  private void mockComments() {
    when(commentViewRepository.findByRequestId(REQUEST_ID)).thenReturn(List.of(COMMENT_VIEW));
  }

  private void mockDocuments() {
    when(documentsViewRepository.findByRequestId(REQUEST_ID)).thenReturn(List.of(DOCUMENT_VIEW));
  }

  private Collection<CreditingPeriodDTO> mockProjectCreditingPeriods() {
    var creditingPeriod = mock(ProjectCreditingPeriod.class);
    var creditingPeriods = List.of(creditingPeriod);
    when(creditingPeriodRepository.findAllByProjectId(PROJECT_ID)).thenReturn(creditingPeriods);
    var dtos = List.of(mock(CreditingPeriodDTO.class));
    when(creditingPeriodConverter.toDtos(creditingPeriods)).thenReturn(dtos);
    return dtos;
  }
}

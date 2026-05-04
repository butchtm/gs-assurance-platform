package org.goldstandard.assurance.api.service.pub;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.goldstandard.assurance.api.dto.pub.PublicDataMapper;
import org.goldstandard.assurance.api.dto.pub.PublicRequest;
import org.goldstandard.assurance.api.entity.Comment;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.view.PublicDocumentView;
import org.goldstandard.assurance.api.entity.view.PublicRequestsView;
import org.goldstandard.assurance.api.repository.pub.PublicDocumentViewRepository;
import org.goldstandard.assurance.api.repository.pub.PublicRequestsViewRepository;
import org.goldstandard.assurance.api.service.CommentFinder;
import org.goldstandard.assurance.api.service.ProjectFinder;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublicServiceTest {
  @Mock private ProjectFinder projectFinder;
  @Mock private RequestFinder requestFinder;
  @Mock private PublicRequestsViewRepository publicRequestsRepository;
  @Mock private PublicDocumentViewRepository publicDocumentRepository;
  @Mock private CommentFinder commentFinder;
  @Mock private PublicDataMapper publicMapper;
  @Mock private PublicRequestVisibilityEvaluator visibilityEvaluator;
  @InjectMocks private PublicService publicService;

  private static final String GSID = "GS1";
  private static final String PROJECT_NAME = "Project1";
  private static final int REQUEST_ID = 1;

  @Test
  void shouldGetProjectAndRequestInformationThatShouldBeVisibleOnPublicDocumentsPage() {
    var project = Project.builder().gsid(GSID).name(PROJECT_NAME).build();
    var request = PublicRequestsView.builder().gsid(GSID).requestId(REQUEST_ID).build();
    var expectedRequest = PublicRequest.builder().build();

    when(projectFinder.forceGetProjectByGsId(GSID)).thenReturn(project);
    when(publicRequestsRepository.findByGsidOrderByRequestIndexAscRequestIdAsc(GSID))
        .thenReturn(List.of(request));
    when(requestFinder.getParentRequestIdOrRequestId(REQUEST_ID)).thenReturn(REQUEST_ID);
    when(publicDocumentRepository.findByGsidAndRequestId(GSID, REQUEST_ID))
        .thenReturn(List.of(new PublicDocumentView()));
    when(commentFinder.getCommentsByRequestId(REQUEST_ID)).thenReturn(List.of(new Comment()));
    when(visibilityEvaluator.shouldShowRequestOnPublicPage(eq(request), anyList(), anyList()))
        .thenReturn(true);
    when(publicMapper.toPublicRequest(eq(request), anyList(), anyList()))
        .thenReturn(expectedRequest);

    var result = publicService.getPublicProjectRequestDocumentsForGSID(GSID);

    assertThat(result.getGsid()).isEqualTo(GSID);
    assertThat(result.getName()).isEqualTo(PROJECT_NAME);
    assertThat(result.getRequests()).contains(expectedRequest);

    verify(visibilityEvaluator).shouldShowRequestOnPublicPage(eq(request), anyList(), anyList());
    verify(publicMapper).toPublicRequest(eq(request), anyList(), anyList());
  }

  @Test
  void shouldExcludeRequestsThatShouldNotBeVisibleOnPublicDocumentsPage() {
    var project = Project.builder().gsid(GSID).name(PROJECT_NAME).build();
    var request = PublicRequestsView.builder().gsid(GSID).requestId(REQUEST_ID).build();

    when(projectFinder.forceGetProjectByGsId(GSID)).thenReturn(project);
    when(publicRequestsRepository.findByGsidOrderByRequestIndexAscRequestIdAsc(GSID))
        .thenReturn(List.of(request));
    when(requestFinder.getParentRequestIdOrRequestId(REQUEST_ID)).thenReturn(REQUEST_ID);
    when(publicDocumentRepository.findByGsidAndRequestId(GSID, REQUEST_ID))
        .thenReturn(List.of(new PublicDocumentView()));
    when(commentFinder.getCommentsByRequestId(REQUEST_ID)).thenReturn(List.of(new Comment()));
    when(visibilityEvaluator.shouldShowRequestOnPublicPage(eq(request), anyList(), anyList()))
        .thenReturn(false);

    var result = publicService.getPublicProjectRequestDocumentsForGSID(GSID);

    assertThat(result.getGsid()).isEqualTo(GSID);
    assertThat(result.getName()).isEqualTo(PROJECT_NAME);
    assertThat(result.getRequests()).isEmpty();

    verify(visibilityEvaluator).shouldShowRequestOnPublicPage(eq(request), anyList(), anyList());
    verifyNoInteractions(publicMapper);
  }

  @Test
  void shouldUseParentRequestIdForFetchingDocumentsAndCommentsForChildOfBatchRequest() {
    var project = Project.builder().gsid(GSID).name(PROJECT_NAME).build();
    var childRequest = PublicRequestsView.builder().gsid(GSID).requestId(REQUEST_ID).build();
    var parentRequestId = 2;

    when(projectFinder.forceGetProjectByGsId(GSID)).thenReturn(project);
    when(publicRequestsRepository.findByGsidOrderByRequestIndexAscRequestIdAsc(GSID))
        .thenReturn(List.of(childRequest));
    when(requestFinder.getParentRequestIdOrRequestId(REQUEST_ID)).thenReturn(parentRequestId);
    when(publicDocumentRepository.findByGsidAndRequestId(GSID, parentRequestId))
        .thenReturn(List.of(new PublicDocumentView()));
    when(commentFinder.getCommentsByRequestId(parentRequestId)).thenReturn(List.of(new Comment()));
    when(visibilityEvaluator.shouldShowRequestOnPublicPage(eq(childRequest), anyList(), anyList()))
        .thenReturn(false);

    var result = publicService.getPublicProjectRequestDocumentsForGSID(GSID);

    assertThat(result.getGsid()).isEqualTo(GSID);
    assertThat(result.getName()).isEqualTo(PROJECT_NAME);
    assertThat(result.getRequests()).isEmpty();

    verify(requestFinder).getParentRequestIdOrRequestId(REQUEST_ID);
    verify(visibilityEvaluator)
        .shouldShowRequestOnPublicPage(eq(childRequest), anyList(), anyList());
    verifyNoInteractions(publicMapper);
  }
}

package org.goldstandard.assurance.api.service.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.util.UUID;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.exception.BadRequestException;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.goldstandard.assurance.api.exception.RequestDoesNotBelongToProjectException;
import org.goldstandard.assurance.api.repository.RequestDocumentRepository;
import org.goldstandard.assurance.api.service.ProjectFinder;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.goldstandard.assurance.api.service.document.pii.analysis.DocumentPiiAnalysisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {
  private static final int PROJECT_ID = 1;
  private static final int REQUEST_ID = 2;
  private static final int DOCUMENT_TYPE_ID = 3;

  private static final UUID DOCUMENT_ID = UUID.fromString("83e31c05-a2f2-49b2-befc-69fe12047efb");

  @InjectMocks DocumentService documentService;

  @Mock DocumentUploader documentUploader;

  @Mock ProjectFinder projectFinder;

  @Mock RequestFinder requestFinder;

  @Mock DocumentAccessPolicy accessPolicy;

  @Mock DocumentFinder documentFinder;

  @Mock RequestDocumentRepository requestDocumentRepository;

  @Mock FileExtensionValidator extensionValidator;

  @Mock DocumentPiiAnalysisService piiAnalysisService;

  @Mock MockMultipartFile file;

  @Mock Project project;

  @Mock Request request;

  @Test
  void uploadFileShouldThrowExceptionIfFileExtensionIsNotValid() {
    var expectedError = new BadRequestException("bad extension");
    doThrow(expectedError).when(extensionValidator).validateFileExtension(file);

    var error =
        catchThrowable(
            () -> documentService.uploadDocument(PROJECT_ID, REQUEST_ID, DOCUMENT_TYPE_ID, file));

    assertThat(error).isEqualTo(expectedError);
  }

  @Test
  void shouldThrowExceptionIfDocumentIsUploadedOnBatchChildRequest() {
    when(requestFinder.getRequestById(REQUEST_ID)).thenReturn(request);
    when(request.isBatchChild()).thenReturn(true);

    var error =
        catchThrowable(
            () -> documentService.uploadDocument(PROJECT_ID, REQUEST_ID, DOCUMENT_TYPE_ID, file));

    assertThat(error).isInstanceOf(BadRequestException.class);
  }

  @Test
  void shouldThrowExceptionIfUserCannotManageDocumentsForProjectAndRequest() {
    when(requestFinder.getRequestById(REQUEST_ID)).thenReturn(request);
    when(projectFinder.forceGetProjectById(PROJECT_ID)).thenReturn(project);

    doThrow(PermissionException.class)
        .when(accessPolicy)
        .validateUserCanManageDocuments(project, request, DOCUMENT_TYPE_ID);

    var error =
        catchThrowable(
            () -> documentService.uploadDocument(PROJECT_ID, REQUEST_ID, DOCUMENT_TYPE_ID, file));

    assertThat(error).isInstanceOf(PermissionException.class);
  }

  @Test
  void shouldThrowExceptionIfRequestDoesNotBelongToProject() {
    doThrow(RequestDoesNotBelongToProjectException.class)
        .when(request)
        .validateBelongsToProject(anyInt());
    when(requestFinder.getRequestById(REQUEST_ID)).thenReturn(request);

    var error =
        catchThrowable(
            () -> documentService.uploadDocument(PROJECT_ID, REQUEST_ID, DOCUMENT_TYPE_ID, file));

    assertThat(error).isInstanceOf(RequestDoesNotBelongToProjectException.class);
  }

  @Test
  void shouldStartPiiAnalysisOnDocumentUpload() {
    when(requestFinder.getRequestById(REQUEST_ID)).thenReturn(request);
    when(documentUploader.upload(request, DOCUMENT_TYPE_ID, file)).thenReturn(DOCUMENT_ID);

    documentService.uploadDocument(PROJECT_ID, REQUEST_ID, DOCUMENT_TYPE_ID, file);

    verify(piiAnalysisService).startAnalysis(DOCUMENT_ID);
  }

  @Test
  void shouldCallServiceToDeleteDocumentFromRequest() {
    documentService.deleteRequestDocument(REQUEST_ID, DOCUMENT_ID);

    verify(requestDocumentRepository).deleteByRequestIdAndDocumentId(REQUEST_ID, DOCUMENT_ID);
  }

  @Test
  void shouldCallFinderToGetDocument() {
    var document = mock(Document.class);
    when(documentFinder.getDocument(DOCUMENT_ID)).thenReturn(document);

    var result = documentService.forceGetDocument(DOCUMENT_ID);

    assertThat(result).isEqualTo(document);
  }
}

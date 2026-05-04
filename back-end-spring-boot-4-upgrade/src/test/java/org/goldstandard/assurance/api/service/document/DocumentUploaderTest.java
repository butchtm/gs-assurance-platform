package org.goldstandard.assurance.api.service.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class DocumentUploaderTest {
  private static final int DOCUMENT_TYPE_ID = 3;

  @InjectMocks DocumentUploader uploader;

  @Mock DocumentFactory documentFactory;

  @Mock DocumentStoreService documentStoreService;

  @Mock DocumentUploadTransaction transaction;

  @Mock Request request;

  @Mock Project project;

  @Mock Document document;

  @Test
  void shouldUploadAndSaveDocument() {
    var file = givenFile();
    var hash = "file-hash";
    when(documentStoreService.uploadFile(project.getGsid(), file)).thenReturn(hash);
    when(documentFactory.createDocument(file, request, DOCUMENT_TYPE_ID, hash))
        .thenReturn(document);
    when(request.getProject()).thenReturn(project);

    var savedDocument = givenSavedDocumentWithId(request, document);

    var id = uploader.upload(request, DOCUMENT_TYPE_ID, file);

    assertThat(id).isEqualTo(savedDocument.getId());
  }

  private Document givenSavedDocumentWithId(Request request, Document document) {
    var savedDocument =
        Document.builder().id(UUID.fromString("52488e9c-c3b4-4878-90c6-05998e96c31d")).build();
    when(transaction.perform(document, request)).thenReturn(savedDocument);
    return savedDocument;
  }

  private static MultipartFile givenFile() {
    return new MockMultipartFile("test-file.txt", "some content".getBytes());
  }
}

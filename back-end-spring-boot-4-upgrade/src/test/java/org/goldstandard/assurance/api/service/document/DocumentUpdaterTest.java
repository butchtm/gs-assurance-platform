package org.goldstandard.assurance.api.service.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import org.goldstandard.assurance.api.dto.DocumentUpdate;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.exception.DocumentNotAssociatedWithRequestException;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.goldstandard.assurance.api.repository.DocumentRepository;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentUpdaterTest {

  private static final UUID DOCUMENT_ID = UUID.fromString("137e0d0e-c7b5-41b8-bd81-9f57b25c7a5b");
  private static final int REQUEST_ID = 99;

  @InjectMocks DocumentUpdater updater;

  @Mock DocumentRepository repository;

  @Mock RequestFinder requestFinder;

  @Mock DocumentAccessPolicy accessPolicy;

  @Mock DocumentUpdateValidator validator;

  @Mock DisplayCategoryIdFinder categoryIdFinder;

  @Mock DocumentUpdateApplier applier;

  @Mock DocumentUpdateTransaction transaction;

  @Mock DocumentUpdateCalculator updateCalculator;

  @Test
  void shouldThrowExceptionIfDocumentNotFound() {
    var expectedError = new NotFoundException("document not found");
    when(repository.forceFindById(DOCUMENT_ID)).thenThrow(expectedError);

    Throwable error =
        catchThrowable(() -> updater.updateDocument(DOCUMENT_ID, givenDocumentUpdate()));

    assertThat(error).isEqualTo(expectedError);
  }

  @Test
  void shouldThrowExceptionIfDocumentNotAssociatedToAnyRequest() {
    var document = givenDocumentFound();
    var expectedError = new DocumentNotAssociatedWithRequestException(document.getId());
    when(requestFinder.getRequestIdAssociatedToDocument(document.getId())).thenThrow(expectedError);

    Throwable error =
        catchThrowable(() -> updater.updateDocument(document.getId(), givenDocumentUpdate()));

    assertThat(error).isEqualTo(expectedError);
  }

  @Test
  void shouldThrowExceptionIfProjectNotFoundForAssociatedRequest() {
    var document = givenDocumentFound();
    givenRequestAssociatedToDocument(document);
    var expectedError = new NotFoundException("project not found");
    when(requestFinder.getRequestById(REQUEST_ID)).thenThrow(expectedError);

    Throwable error =
        catchThrowable(() -> updater.updateDocument(document.getId(), givenDocumentUpdate()));

    assertThat(error).isEqualTo(expectedError);
  }

  @Test
  void shouldThrowExceptionIfUserCannotManageDocumentsForProjectOrRequest() {
    var document = givenDocumentFound();
    givenRequestAssociatedToDocument(document);
    var request = new Request();
    var project = givenProjectFoundForRequest(request);
    var expectedError = new PermissionException("user cannot manage documents");
    var update = givenDocumentUpdate();
    doThrow(expectedError)
        .when(accessPolicy)
        .validateUserCanManageDocuments(project, request, update.documentTypeId());

    Throwable error = catchThrowable(() -> updater.updateDocument(document.getId(), update));

    assertThat(error).isEqualTo(expectedError);
  }

  @Test
  void shouldThrowExceptionIfUpdateAccessIsNotAllowed() {
    var document = givenDocumentFound();
    givenRequestAssociatedToDocument(document);
    givenProjectFoundForRequest();
    var update = givenDocumentUpdate();
    var expectedError = new PermissionException("update not allowed");
    doThrow(expectedError).when(validator).validateUpdateAllowed(document, update);

    Throwable error = catchThrowable(() -> updater.updateDocument(document.getId(), update));

    assertThat(error).isEqualTo(expectedError);
  }

  @Test
  void shouldThrowExceptionIfCannotFindDocumentDisplayCategory() {
    var document = givenDocumentFound();
    givenRequestAssociatedToDocument(document);
    givenProjectFoundForRequest();
    var update = givenDocumentUpdate();
    var expectedError = new DocumentNotAssociatedWithRequestException(document.getId(), REQUEST_ID);
    when(categoryIdFinder.getDisplayCategoryId(document.getId(), REQUEST_ID))
        .thenThrow(expectedError);

    Throwable error = catchThrowable(() -> updater.updateDocument(document.getId(), update));

    assertThat(error).isEqualTo(expectedError);
  }

  @Test
  void shouldUpdateDocumentIfAllowed() {
    var document = givenDocumentFound();
    givenRequestAssociatedToDocument(document);
    var project = givenProjectFoundForRequest();
    var update = givenDocumentUpdate();
    var categoryId = givenDocumentCategoryIdFound(document);
    var updatedDocument = givenUpdatedDocument(document, update, categoryId);
    var otherUpdates = givenDocumentsToMakePrivate(document, updatedDocument);
    var savedDocument = givenSavedDocument(updatedDocument, otherUpdates, project);

    var result = updater.updateDocument(document.getId(), update);

    assertThat(result).isEqualTo(savedDocument);
  }

  private Document givenDocumentFound() {
    var document = givenDocumentWithId();
    when(repository.forceFindById(DOCUMENT_ID)).thenReturn(document);
    return document;
  }

  private void givenRequestAssociatedToDocument(Document document) {
    when(requestFinder.getRequestIdAssociatedToDocument(document.getId())).thenReturn(REQUEST_ID);
  }

  private Project givenProjectFoundForRequest() {
    return givenProjectFoundForRequest(new Request());
  }

  private Project givenProjectFoundForRequest(Request request) {
    var project = givenProjectWithId();
    request.setProject(project);
    when(requestFinder.getRequestById(REQUEST_ID)).thenReturn(request);
    return project;
  }

  private int givenDocumentCategoryIdFound(Document document) {
    var categoryId = 5;
    when(categoryIdFinder.getDisplayCategoryId(document.getId(), REQUEST_ID))
        .thenReturn(categoryId);
    return categoryId;
  }

  private Document givenUpdatedDocument(Document document, DocumentUpdate update, int categoryId) {
    var updatedDocument = mock(Document.class);
    when(applier.applyUpdates(document, update, categoryId)).thenReturn(updatedDocument);
    return updatedDocument;
  }

  private Collection<Document> givenDocumentsToMakePrivate(Document original, Document updated) {
    Collection<Document> otherUpdates = Collections.emptyList();
    when(updateCalculator.calculateDocumentsToMakePrivate(original, updated, REQUEST_ID))
        .thenReturn(otherUpdates);
    return otherUpdates;
  }

  private Document givenSavedDocument(
      Document document, Collection<Document> otherUpdates, Project project) {
    var savedDocument = mock(Document.class);
    when(transaction.performUpdate(document, REQUEST_ID, project.getId(), otherUpdates))
        .thenReturn(savedDocument);
    return savedDocument;
  }

  private static DocumentUpdate givenDocumentUpdate() {
    return new DocumentUpdate(true, true, 75, true);
  }

  private static Document givenDocumentWithId() {
    return Document.builder().id(DOCUMENT_ID).build();
  }

  private static Project givenProjectWithId() {
    var project = new Project();
    project.setId(88);
    return project;
  }
}

package org.goldstandard.assurance.api.service.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.view.CategorisedRequestDocumentView;
import org.goldstandard.assurance.api.exception.DocumentNotAssociatedWithRequestException;
import org.goldstandard.assurance.api.repository.CategorisedRequestDocumentViewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DisplayCategoryIdFinderTest {

  private static final int REQUEST_ID = 99;

  @InjectMocks DisplayCategoryIdFinder finder;

  @Mock CategorisedRequestDocumentViewRepository repository;

  @Test
  void shouldThrowExceptionIfCategoryNotFound() {
    UUID documentId = UUID.randomUUID();
    when(repository.findAllByRequestId(REQUEST_ID)).thenReturn(Collections.emptyList());

    Throwable error = catchThrowable(() -> finder.getDisplayCategoryId(documentId, REQUEST_ID));

    assertThat(error)
        .isInstanceOf(DocumentNotAssociatedWithRequestException.class)
        .hasMessage("Document %s not associated with request %d", documentId, REQUEST_ID);
  }

  @Test
  void shouldThrowExceptionIfCategoriesFoundButNoneMatchDocumentId() {
    Document document = buildDocument();
    List<CategorisedRequestDocumentView> views =
        List.of(
            categorisedRequestDocumentView(buildDocument(), 1),
            categorisedRequestDocumentView(buildDocument(), 2));
    when(repository.findAllByRequestId(REQUEST_ID)).thenReturn(views);

    Throwable error =
        catchThrowable(() -> finder.getDisplayCategoryId(document.getId(), REQUEST_ID));

    assertThat(error)
        .isInstanceOf(DocumentNotAssociatedWithRequestException.class)
        .hasMessage("Document %s not associated with request %d", document.getId(), REQUEST_ID);
  }

  @Test
  void shouldReturnCategoryIdFromFirstCategorisedRequestDocumentViewWithMatchingDocumentId() {
    UUID documentId = UUID.randomUUID();
    Document document = buildDocument(documentId);
    List<CategorisedRequestDocumentView> views =
        List.of(
            categorisedRequestDocumentView(buildDocument(documentId), 3),
            categorisedRequestDocumentView(buildDocument(documentId), 4));
    when(repository.findAllByRequestId(REQUEST_ID)).thenReturn(views);

    int categoryId = finder.getDisplayCategoryId(document.getId(), REQUEST_ID);

    assertThat(categoryId).isEqualTo(3);
  }

  private static Document buildDocument() {
    return buildDocument(UUID.randomUUID());
  }

  private static Document buildDocument(UUID id) {
    return Document.builder().id(id).build();
  }

  private static CategorisedRequestDocumentView categorisedRequestDocumentView(
      Document document, int categoryId) {
    var view = new CategorisedRequestDocumentView();
    view.setDocument(document);
    view.setDocumentDisplayCategoryId(categoryId);
    return view;
  }
}

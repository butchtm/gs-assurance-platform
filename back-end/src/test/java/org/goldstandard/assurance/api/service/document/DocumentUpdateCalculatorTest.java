package org.goldstandard.assurance.api.service.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.repository.CategorisedRequestDocumentViewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentUpdateCalculatorTest {

  private static final int REQUEST_ID = 1;

  @InjectMocks DocumentUpdateCalculator calculator;

  @Mock CategorisedRequestDocumentViewRepository repository;

  @Mock Document original;

  @Mock Document updated;

  @Test
  void shouldNotReturnDocumentsToMakePrivateIfUpdatedDocumentTypeIsOther() {
    when(updated.hasOtherType()).thenReturn(true);

    var otherUpdates = calculator.calculateDocumentsToMakePrivate(original, updated, REQUEST_ID);

    assertThat(otherUpdates).isEmpty();
  }

  @Test
  void shouldNotReturnDocumentsToMakePrivateIfDocumentWasAlreadyPublic() {
    when(updated.hasOtherType()).thenReturn(false);
    when(original.isPrivate()).thenReturn(false);

    var otherUpdates = calculator.calculateDocumentsToMakePrivate(original, updated, REQUEST_ID);

    assertThat(otherUpdates).isEmpty();
  }

  @Test
  void shouldNotReturnDocumentsToMakePrivateIfDocumentHasNotBeenMadePublic() {
    when(updated.hasOtherType()).thenReturn(false);
    when(original.isPrivate()).thenReturn(true);
    when(updated.isPublic()).thenReturn(false);

    var otherUpdates = calculator.calculateDocumentsToMakePrivate(original, updated, REQUEST_ID);

    assertThat(otherUpdates).isEmpty();
  }

  @Test
  void
      shouldReturnDocumentsToMakePrivateIfUpdateDocumentDoesNotHaveTypeOtherAndHasBeenMadePublic() {
    when(updated.hasOtherType()).thenReturn(false);
    when(original.isPrivate()).thenReturn(true);
    when(updated.isPublic()).thenReturn(true);
    var typeId = 2;
    when(updated.getDocumentTypeId()).thenReturn(typeId);
    var public1 = mock(Document.class);
    var private1 = givenMadePrivate(public1);
    var public2 = mock(Document.class);
    var private2 = givenMadePrivate(public2);
    when(repository.findAllPublicRequestDocumentsWithType(REQUEST_ID, typeId))
        .thenReturn(List.of(public1, public2));

    var otherUpdates = calculator.calculateDocumentsToMakePrivate(original, updated, REQUEST_ID);

    assertThat(otherUpdates).containsExactly(private1, private2);
  }

  private Document givenMadePrivate(Document document) {
    var privateDocument = mock(Document.class);
    when(document.makePrivate()).thenReturn(privateDocument);
    return privateDocument;
  }
}

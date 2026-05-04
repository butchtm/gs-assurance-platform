package org.goldstandard.assurance.api.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.goldstandard.assurance.api.entity.lookup.DocumentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DocumentTest {

  @Test
  void shouldReturnTrueIfPublic() {
    var document = Document.builder().access(true).build();

    var isPublic = document.isPublic();

    assertThat(isPublic).isTrue();
  }

  @Test
  void shouldReturnFalseIfNotPublic() {
    var document = Document.builder().access(false).build();

    var isPublic = document.isPublic();

    assertThat(isPublic).isFalse();
  }

  @Test
  void shouldReturnTrueIfPrivate() {
    var document = Document.builder().access(false).build();

    var isPrivate = document.isPrivate();

    assertThat(isPrivate).isTrue();
  }

  @Test
  void shouldReturnFalseIfNotPrivate() {
    var document = Document.builder().access(true).build();

    var isPrivate = document.isPrivate();

    assertThat(isPrivate).isFalse();
  }

  @Test
  void shouldMakeDocumentPrivate() {
    var original = Document.builder().access(true).build();

    var updated = original.makePrivate();

    assertThat(updated.isPrivate()).isTrue();
  }

  @Test
  void shouldReturnNullDocumentTypeIdIfDocumentTypeIsNull() {
    var document = Document.builder().documentType(null).build();

    var typeId = document.getDocumentTypeId();

    assertThat(typeId).isNull();
  }

  @Test
  void shouldReturnNullDocumentTypeIdIfDocumentTypeSet() {
    var expectedId = 1;
    var document = Document.builder().documentType(new DocumentType(expectedId)).build();

    var typeId = document.getDocumentTypeId();

    assertThat(typeId).isEqualTo(expectedId);
  }

  @Test
  void shouldReturnHasOtherTypeFalseIfDocumentTypeIsNull() {
    var document = Document.builder().documentType(null).build();

    var hasOtherType = document.hasOtherType();

    assertThat(hasOtherType).isFalse();
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void shouldReturnHasOtherType(boolean isOther) {
    var type = mock(DocumentType.class);
    when(type.isOther()).thenReturn(isOther);
    var document = Document.builder().documentType(type).build();

    var hasOtherType = document.hasOtherType();

    assertThat(hasOtherType).isEqualTo(isOther);
  }
}

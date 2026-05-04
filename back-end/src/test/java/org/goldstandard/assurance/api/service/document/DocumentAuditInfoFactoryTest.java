package org.goldstandard.assurance.api.service.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DocumentAuditInfoFactoryTest {

  private static final int PROJECT_ID = 1;
  private static final int REQUEST_ID = 2;
  private static final UUID DOCUMENT_ID = UUID.fromString("14088719-c5e5-4516-919e-e1b189a095c8");
  private static final Collection<UUID> OTHER_DOCUMENT_IDS =
      List.of(
          UUID.fromString("3ab2f871-8653-4ca2-a4f2-92a9ffe13651"),
          UUID.fromString("7762d22b-d6a3-4dce-9742-751373e81635"));

  private final DocumentAuditInfoFactory factory = new DocumentAuditInfoFactory();

  @Test
  void shouldPopulateAllSuppliedValues() {
    var info = factory.build(PROJECT_ID, REQUEST_ID, DOCUMENT_ID);

    assertThat(info).hasSize(3);
  }

  @Test
  void shouldPopulateAllValuesIfSupplied() {
    var info = factory.build(PROJECT_ID, REQUEST_ID, DOCUMENT_ID, OTHER_DOCUMENT_IDS);

    assertThat(info).hasSize(4);
  }

  @ParameterizedTest
  @MethodSource("expectedKeysAndValues")
  void shouldPopulateKeysAndValues(String key, Object value) {
    var info = factory.build(PROJECT_ID, REQUEST_ID, DOCUMENT_ID, OTHER_DOCUMENT_IDS);

    assertThat(info).contains(entry(key, value));
  }

  private static Stream<Arguments> expectedKeysAndValues() {
    return Stream.of(
        Arguments.of("projectId", PROJECT_ID),
        Arguments.of("requestId", REQUEST_ID),
        Arguments.of("documentId", DOCUMENT_ID.toString()),
        Arguments.of(
            "otherDocumentIds",
            "3ab2f871-8653-4ca2-a4f2-92a9ffe13651,7762d22b-d6a3-4dce-9742-751373e81635"));
  }
}

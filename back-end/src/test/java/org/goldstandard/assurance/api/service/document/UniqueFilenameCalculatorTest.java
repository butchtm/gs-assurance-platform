package org.goldstandard.assurance.api.service.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.List;
import org.goldstandard.assurance.api.entity.Document;
import org.junit.jupiter.api.Test;

class UniqueFilenameCalculatorTest {

  private final UniqueFilenameCalculator calculator = new UniqueFilenameCalculator();

  @Test
  void shouldReturnUniqueFilenamesUsingUpCasedContainerNamePrefixIfFileNamesAreTheSame() {
    var document1 =
        Document.builder().containerName("gs1111").fileName("duplicate-file.txt").build();
    var document2 =
        Document.builder().containerName("gs2222").fileName("duplicate-file.txt").build();

    var uniqueFilenames = calculator.toSortedUniqueFilenames(List.of(document2, document1));

    assertThat(uniqueFilenames)
        .containsExactly(
            entry("GS1111-duplicate-file.txt", document1),
            entry("GS2222-duplicate-file.txt", document2));
  }

  @Test
  void shouldReturnSuppliedFilenameIfAllFilenamesAreUnique() {
    var document1 = Document.builder().containerName("gs1111").fileName("file-1.txt").build();
    var document2 = Document.builder().containerName("gs2222").fileName("file-2.txt").build();

    var uniqueFilenames = calculator.toSortedUniqueFilenames(List.of(document2, document1));

    assertThat(uniqueFilenames)
        .containsExactly(entry("file-1.txt", document1), entry("file-2.txt", document2));
  }

  @Test
  void shouldReturnFilenamesInAlphabeticalOrder() {
    var document1 = Document.builder().containerName("gs1111").fileName("bbb.txt").build();
    var document2 = Document.builder().containerName("gs2222").fileName("aaa.txt").build();

    var uniqueFilenames = calculator.toSortedUniqueFilenames(List.of(document2, document1));

    assertThat(uniqueFilenames)
        .containsExactly(entry("aaa.txt", document2), entry("bbb.txt", document1));
  }
}

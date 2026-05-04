package org.goldstandard.assurance.api.service.document.pii.analysis;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PlainTextDocumentChunkerTest {

  @InjectMocks private PlainTextDocumentChunker chunker;

  @Test
  void shouldReturnSingleChunkForTextUnderLimit() {
    ReflectionTestUtils.setField(chunker, "comprehendLimit", 100000);

    var textUnderLimit = "a".repeat(99999); // 99,999 characters - under the 100,000 limit

    var result = chunker.chunkTextIntoPages(textUnderLimit);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getPageNumber()).isNull();
    assertThat(result.getFirst().getText()).isEqualTo(textUnderLimit);
    assertThat(result.getFirst().getText().length()).isEqualTo(99999);
  }

  @Test
  void shouldSplitTextExceedingLimitIntoMultipleChunks() {
    ReflectionTestUtils.setField(chunker, "comprehendLimit", 100000);

    var textOverLimit = "b".repeat(250000); // 250,000 characters, should create 3 chunks

    var result = chunker.chunkTextIntoPages(textOverLimit);

    assertThat(result).hasSize(3);

    assertThat(result.getFirst().getPageNumber()).isNull();
    assertThat(result.get(0).getText()).isEqualTo("b".repeat(100000));
    assertThat(result.get(0).getText().length()).isEqualTo(100000);

    assertThat(result.get(1).getPageNumber()).isNull();
    assertThat(result.get(1).getText()).isEqualTo("b".repeat(100000));
    assertThat(result.get(1).getText().length()).isEqualTo(100000);

    assertThat(result.get(2).getPageNumber()).isNull();
    assertThat(result.get(2).getText()).isEqualTo("b".repeat(50000));
    assertThat(result.get(2).getText().length()).isEqualTo(50000);
  }

  @Test
  void shouldHandleEmptyString() {
    ReflectionTestUtils.setField(chunker, "comprehendLimit", 100000);

    var emptyText = "";

    var result = chunker.chunkTextIntoPages(emptyText);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getPageNumber()).isNull();
    assertThat(result.getFirst().getText()).isEmpty();
  }
}

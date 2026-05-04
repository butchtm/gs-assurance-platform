package org.goldstandard.assurance.api.service.document.pii.analysis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.goldstandard.assurance.api.dto.ExtractedText;
import org.goldstandard.assurance.api.service.document.FileStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExtractedTextS3WriterTest {

  @InjectMocks private ExtractedTextS3Writer extractedTextS3Writer;
  @Mock private FileStore fileStore;
  @Captor private ArgumentCaptor<InputStream> inputStreamCaptor;

  private static final String S3_INPUT_FOLDER = "s3://my-bucket/my-document";

  @Test
  void shouldWritePageToS3ForEachUniquePageInExtractedTextList() throws IOException {
    var text1 = "text1First";
    var text2 = "text2Another";
    var extractedText1 = ExtractedText.builder().text(text1).pageNumber(1).build();
    var extractedText2 = ExtractedText.builder().text(text2).pageNumber(2).build();
    var extractedTexts = List.of(extractedText1, extractedText2);

    extractedTextS3Writer.writePagesToS3(extractedTexts, S3_INPUT_FOLDER);

    verify(fileStore)
        .uploadStream(
            eq(S3_INPUT_FOLDER + "/1"), inputStreamCaptor.capture(), eq((long) text1.length()));
    assertThat(getStreamCaptorString()).isEqualTo(text1);
    verify(fileStore)
        .uploadStream(
            eq(S3_INPUT_FOLDER + "/2"), inputStreamCaptor.capture(), eq((long) text2.length()));
    assertThat(getStreamCaptorString()).isEqualTo(text2);
  }

  @Test
  void shouldAppendToExistingFileIfFileExistsOnS3() throws IOException {
    var existingFile1Text = "textOnS3";
    var text1 = "text1First";
    var extractedText1 = ExtractedText.builder().text(text1).pageNumber(1).build();
    var filePath = S3_INPUT_FOLDER + "/1";
    var fullFileText = existingFile1Text + " " + text1;

    when(fileStore.getObjectAsStream(filePath))
        .thenReturn(new ByteArrayInputStream(existingFile1Text.getBytes()));

    extractedTextS3Writer.writePagesToS3(List.of(extractedText1), S3_INPUT_FOLDER);

    verify(fileStore).getObjectAsStream(filePath);
    verify(fileStore)
        .uploadStream(eq(filePath), inputStreamCaptor.capture(), eq((long) fullFileText.length()));
    assertThat(getStreamCaptorString()).isEqualTo(fullFileText);
  }

  private String getStreamCaptorString() throws IOException {
    return new String(inputStreamCaptor.getValue().readAllBytes(), StandardCharsets.UTF_8);
  }
}

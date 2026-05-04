package org.goldstandard.assurance.api.service.textract;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.goldstandard.assurance.api.fixtures.TextractObjectMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.GetDocumentTextDetectionRequest;

@ExtendWith(MockitoExtension.class)
class TextractResultProcessorTest {

  @InjectMocks private TextractResultProcessor resultProcessor;
  @Mock private TextractClient textractClient;
  @Mock private TextractServiceRequestFactory requestFactory;

  @Test
  void shouldCollectTextFromResponse() {
    when(requestFactory.getDetectionRequestWithToken(anyString(), eq(null)))
        .thenReturn(mock(GetDocumentTextDetectionRequest.class));
    when(textractClient.getDocumentTextDetection(any(GetDocumentTextDetectionRequest.class)))
        .thenReturn(TextractObjectMother.detectionResponseWithBlocks());

    assertThat(resultProcessor.streamByResponseToken("job1").flatMap(List::stream).toList())
        .extracting("text")
        .containsExactlyInAnyOrder("block1", "block2");

    verify(textractClient).getDocumentTextDetection(any(GetDocumentTextDetectionRequest.class));
  }

  @Test
  void shouldUseNextTokenToCollectTextFromNextBlocks() {
    when(requestFactory.getDetectionRequestWithToken(anyString(), eq(null)))
        .thenReturn(mock(GetDocumentTextDetectionRequest.class));
    when(requestFactory.getDetectionRequestWithToken(anyString(), anyString()))
        .thenReturn(mock(GetDocumentTextDetectionRequest.class));
    when(textractClient.getDocumentTextDetection(any(GetDocumentTextDetectionRequest.class)))
        .thenReturn(
            TextractObjectMother.detectionResponseWithBlocksAndNextToken(),
            TextractObjectMother.detectionResponseWithBlocks());

    assertThat(resultProcessor.streamByResponseToken("job1").flatMap(List::stream).toList())
        .extracting("text")
        .containsExactlyInAnyOrder("block1", "block2", "block1", "block2");

    verify(textractClient, times(2))
        .getDocumentTextDetection(any(GetDocumentTextDetectionRequest.class));
  }
}

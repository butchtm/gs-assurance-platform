package org.goldstandard.assurance.api.service.textract;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Stream;
import org.goldstandard.assurance.api.exception.DocumentPiiScanException;
import org.goldstandard.assurance.api.fixtures.TextractObjectMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.GetDocumentTextDetectionRequest;
import software.amazon.awssdk.services.textract.model.GetDocumentTextDetectionResponse;
import software.amazon.awssdk.services.textract.model.StartDocumentTextDetectionRequest;

@ExtendWith(MockitoExtension.class)
class TextractServiceDefaultTest {

  @InjectMocks private TextractServiceDefault service;
  @Mock private TextractClient textractClient;
  @Mock private TextractServiceRequestFactory requestFactory;
  @Mock private TextractDelayHandler delayHandler;
  @Mock private TextractResultProcessor resultProcessor;

  private static final String BUCKET = "bucket";
  private static final String DOC_NAME = "document1.pdf";
  private static final String JOB_ID = "job1";
  private static final GetDocumentTextDetectionResponse SUCCESS_RESPONSE =
      TextractObjectMother.successfulDetectionResponse();

  @Test
  void shouldProcessResultWhenJobComplete() {
    stubTextDetectionRequests();
    when(textractClient.startDocumentTextDetection(any(StartDocumentTextDetectionRequest.class)))
        .thenReturn(TextractObjectMother.startDetectionRequest(JOB_ID));
    when(textractClient.getDocumentTextDetection(any(GetDocumentTextDetectionRequest.class)))
        .thenReturn(SUCCESS_RESPONSE);

    assertThat(service.doDetectTextJob(BUCKET, DOC_NAME)).isEqualTo(JOB_ID);

    verify(textractClient).startDocumentTextDetection(any(StartDocumentTextDetectionRequest.class));
    verify(textractClient).getDocumentTextDetection(any(GetDocumentTextDetectionRequest.class));
  }

  @Test
  void shouldRepeatRequestWhenJobNotComplete() {
    stubTextDetectionRequests();
    when(textractClient.startDocumentTextDetection(any(StartDocumentTextDetectionRequest.class)))
        .thenReturn(TextractObjectMother.startDetectionRequest(JOB_ID));
    when(textractClient.getDocumentTextDetection(any(GetDocumentTextDetectionRequest.class)))
        .thenReturn(TextractObjectMother.inProgressDetectionResponse(), SUCCESS_RESPONSE);

    assertThat(service.doDetectTextJob(BUCKET, DOC_NAME)).isEqualTo(JOB_ID);

    verify(textractClient).startDocumentTextDetection(any(StartDocumentTextDetectionRequest.class));
    verify(textractClient, times(2))
        .getDocumentTextDetection(any(GetDocumentTextDetectionRequest.class));
  }

  @Test
  void shouldThrowPiiScanExceptionWhenThreadInterrupted() {
    stubTextDetectionRequests();
    when(textractClient.startDocumentTextDetection(any(StartDocumentTextDetectionRequest.class)))
        .thenReturn(TextractObjectMother.startDetectionRequest(JOB_ID));
    doThrow(DocumentPiiScanException.class).when(delayHandler).delayBeforeNextPoll();

    assertThat(catchThrowable(() -> service.doDetectTextJob(BUCKET, DOC_NAME)))
        .isInstanceOf(DocumentPiiScanException.class);

    verify(textractClient).startDocumentTextDetection(any(StartDocumentTextDetectionRequest.class));
    verifyNoMoreInteractions(textractClient);
  }

  @Test
  void shouldThrowPiiScanExceptionWhenResponseStatusIsFailure() {
    stubTextDetectionRequests();
    when(textractClient.startDocumentTextDetection(any(StartDocumentTextDetectionRequest.class)))
        .thenReturn(TextractObjectMother.startDetectionRequest(JOB_ID));
    when(textractClient.getDocumentTextDetection(any(GetDocumentTextDetectionRequest.class)))
        .thenReturn(TextractObjectMother.failedDetectionResponse());

    assertThat(catchThrowable(() -> service.doDetectTextJob(BUCKET, DOC_NAME)))
        .isInstanceOf(DocumentPiiScanException.class);

    verify(textractClient).startDocumentTextDetection(any(StartDocumentTextDetectionRequest.class));
    verify(textractClient).getDocumentTextDetection(any(GetDocumentTextDetectionRequest.class));
  }

  private void stubTextDetectionRequests() {
    when(requestFactory.startDetectionRequest(anyString(), anyString()))
        .thenReturn(mock(StartDocumentTextDetectionRequest.class));
    when(requestFactory.getDetectionRequest(anyString()))
        .thenReturn(mock(GetDocumentTextDetectionRequest.class));
  }

  @Test
  void shouldReturnStreamOfResultsForJobId() {
    var testResults = TextractObjectMother.resultTexts();
    when(resultProcessor.streamByResponseToken(JOB_ID)).thenReturn(Stream.of(testResults));

    assertThat(service.extractWords(JOB_ID).flatMap(List::stream).toList()).isEqualTo(testResults);

    verify(resultProcessor).streamByResponseToken(JOB_ID);
  }
}

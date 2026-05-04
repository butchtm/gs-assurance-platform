package org.goldstandard.assurance.api.service.comprehend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.goldstandard.assurance.api.dto.ExtractedText;
import org.goldstandard.assurance.api.dto.FieldTextAnalysisDTO;
import org.goldstandard.assurance.api.enumeration.PiiProbability;
import org.goldstandard.assurance.api.fixtures.PiiEntityMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.comprehend.model.DescribePiiEntitiesDetectionJobRequest;
import software.amazon.awssdk.services.comprehend.model.DescribePiiEntitiesDetectionJobResponse;
import software.amazon.awssdk.services.comprehend.model.DetectPiiEntitiesRequest;
import software.amazon.awssdk.services.comprehend.model.DetectPiiEntitiesResponse;
import software.amazon.awssdk.services.comprehend.model.PiiEntitiesDetectionJobProperties;
import software.amazon.awssdk.services.comprehend.model.PiiEntity;
import software.amazon.awssdk.services.comprehend.model.PiiEntityType;
import software.amazon.awssdk.services.comprehend.model.StartPiiEntitiesDetectionJobRequest;
import software.amazon.awssdk.services.comprehend.model.StartPiiEntitiesDetectionJobResponse;

@ExtendWith(MockitoExtension.class)
class ComprehendServiceDefaultTest {

  @InjectMocks ComprehendServiceDefault service;
  @Mock ComprehendClient comprehendClient;
  @Mock ComprehendPiiConfigFilter filter;
  @Mock ComprehendRequestFactory requestFactory;
  @Captor ArgumentCaptor<DetectPiiEntitiesRequest> requestCaptor;

  private static final String TEST_JOB_ID = "job123";

  @Test
  void shouldReturnProbablePiiWhenTextContainsEntities() {
    var fieldText = FieldTextAnalysisDTO.builder().field("notes").text("joe blogg").build();
    var entities = List.of(buildPiiEntity());
    when(comprehendClient.detectPiiEntities(any(DetectPiiEntitiesRequest.class)))
        .thenReturn(DetectPiiEntitiesResponse.builder().entities(entities).build());
    when(filter.filterByConfigProperties(entities)).thenReturn(entities);

    var result = service.detectEntities(List.of(fieldText));

    verify(comprehendClient).detectPiiEntities(requestCaptor.capture());
    var request = requestCaptor.getValue();

    assertThat(request.text()).isEqualTo(fieldText.getText());
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getEntity()).isEqualTo(PiiEntityType.NAME.toString());
    assertThat(result.getFirst().getProbability()).isEqualTo(PiiProbability.HIGH);
    assertThat(result.getFirst().getIdentifiedString()).isEqualTo(fieldText.getText());
    assertThat(result.getFirst().getField()).isEqualTo(fieldText.getField());
  }

  @Test
  void shouldNotReturnProbablePiiWhenTestNotIdentifiedAsPii() {
    var fieldText = FieldTextAnalysisDTO.builder().field("notes").text("joe blogg").build();
    when(comprehendClient.detectPiiEntities(any(DetectPiiEntitiesRequest.class)))
        .thenReturn(DetectPiiEntitiesResponse.builder().entities(Collections.emptyList()).build());

    var result = service.detectEntities(List.of(fieldText));

    verify(comprehendClient).detectPiiEntities(requestCaptor.capture());
    var request = requestCaptor.getValue();

    assertThat(request.text()).isEqualTo(fieldText.getText());
    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnProbablePiiWithPageNumberWhenDocumentTextContainsEntities() {
    var extractedText = ExtractedText.builder().pageNumber(2).text("joe blogg").build();
    var entities = List.of(buildPiiEntity());
    when(comprehendClient.detectPiiEntities(any(DetectPiiEntitiesRequest.class)))
        .thenReturn(DetectPiiEntitiesResponse.builder().entities(entities).build());
    when(filter.filterByConfigProperties(entities)).thenReturn(entities);

    var result = service.detectDocumentEntities(List.of(extractedText));

    verify(comprehendClient).detectPiiEntities(requestCaptor.capture());
    var request = requestCaptor.getValue();

    assertThat(request.text()).isEqualTo(extractedText.getText());
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getEntity()).isEqualTo(PiiEntityType.NAME.toString());
    assertThat(result.getFirst().getProbability()).isEqualTo(PiiProbability.HIGH);
    assertThat(result.getFirst().getPageNumber()).isEqualTo(extractedText.getPageNumber());
  }

  private static PiiEntity buildPiiEntity() {
    return PiiEntityMother.build((float) 0.99);
  }

  @Test
  void shouldStartDetectionJobAndReturnJobIdAndStatus() {
    var jobStatus = "SUBMITTED";
    var request = StartPiiEntitiesDetectionJobRequest.builder().build();
    var response =
        StartPiiEntitiesDetectionJobResponse.builder()
            .jobId(TEST_JOB_ID)
            .jobStatus(jobStatus)
            .build();

    when(requestFactory.startDetectPiiEntitiesRequest(anyString())).thenReturn(request);
    when(comprehendClient.startPiiEntitiesDetectionJob(request)).thenReturn(response);

    var result = service.detectDocumentEntitiesAsync("input-folder");

    assertThat(result.getComprehendJobId()).isEqualTo(TEST_JOB_ID);
    assertThat(result.getComprehendJobStatus()).isEqualTo(jobStatus);
  }

  @Test
  void shouldReturnJobDetailsForJobId() {
    var request = DescribePiiEntitiesDetectionJobRequest.builder().build();
    var expectedResult = PiiEntitiesDetectionJobProperties.builder().build();
    var response =
        DescribePiiEntitiesDetectionJobResponse.builder()
            .piiEntitiesDetectionJobProperties(expectedResult)
            .build();

    when(requestFactory.describeDetectEntitiesJobRequest(TEST_JOB_ID)).thenReturn(request);
    when(comprehendClient.describePiiEntitiesDetectionJob(request)).thenReturn(response);

    var result = service.getDetectPiiJob(TEST_JOB_ID);

    assertThat(result).isEqualTo(expectedResult);
  }
}

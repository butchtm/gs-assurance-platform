package org.goldstandard.assurance.api.service.comprehend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.util.List;
import org.goldstandard.assurance.api.dto.comprehend.ComprehendFileOutput;
import org.goldstandard.assurance.api.dto.comprehend.ComprehendFileRawOutput;
import org.goldstandard.assurance.api.service.document.FileStore;
import org.goldstandard.assurance.api.util.JsonConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.comprehend.model.PiiEntity;
import software.amazon.awssdk.services.comprehend.model.PiiOutputDataConfig;

@ExtendWith(MockitoExtension.class)
class ComprehendResultsRetrieverTest {

  @InjectMocks private ComprehendResultsRetriever retriever;
  @Mock private FileStore fileStore;
  @Mock private JsonConverter jsonConverter;
  @Mock private ComprehendOutputMapper outputMapper;
  @Mock private ComprehendPiiConfigFilter filter;

  private static final String TEST_S3_URI = "s3://my-bucket/myfolder";

  @Test
  void shouldMapJsonToObjectFromEveryFileInSpecifiedS3Folder() {
    var outputConfig = PiiOutputDataConfig.builder().s3Uri(TEST_S3_URI).build();
    var s3Keys = List.of("key1");
    var file1Stream = new ByteArrayInputStream("file1".getBytes());
    var rawOutput = mock(ComprehendFileRawOutput.class);
    var piiEntities = List.of(PiiEntity.builder().build());
    var expectedOutput = ComprehendFileOutput.builder().entities(piiEntities).build();

    when(fileStore.getKeysInFolder(TEST_S3_URI)).thenReturn(s3Keys);
    when(fileStore.getObjectAsStream(s3Keys.getFirst())).thenReturn(file1Stream);
    when(jsonConverter.toObject(file1Stream, ComprehendFileRawOutput.class)).thenReturn(rawOutput);
    when(outputMapper.mapFromRawOutput(rawOutput)).thenReturn(expectedOutput);
    when(filter.filterByConfigProperties(piiEntities)).thenReturn(piiEntities);

    var result = retriever.retrieve(outputConfig).stream().toList();

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getEntities()).isEqualTo(piiEntities);

    verify(fileStore).getKeysInFolder(TEST_S3_URI);
    verify(fileStore).getObjectAsStream(s3Keys.getFirst());
    verify(jsonConverter).toObject(file1Stream, ComprehendFileRawOutput.class);
    verify(outputMapper).mapFromRawOutput(rawOutput);
    verify(filter).filterByConfigProperties(piiEntities);
  }
}

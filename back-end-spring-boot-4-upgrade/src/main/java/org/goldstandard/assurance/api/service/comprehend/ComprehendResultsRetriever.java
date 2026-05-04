package org.goldstandard.assurance.api.service.comprehend;

import java.io.InputStream;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.comprehend.ComprehendFileOutput;
import org.goldstandard.assurance.api.dto.comprehend.ComprehendFileRawOutput;
import org.goldstandard.assurance.api.service.document.FileStore;
import org.goldstandard.assurance.api.util.JsonConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.comprehend.model.PiiOutputDataConfig;

@Slf4j
@Component
public class ComprehendResultsRetriever {

  private final FileStore fileStore;
  private final JsonConverter jsonConverter;
  private final ComprehendOutputMapper outputMapper;
  private final ComprehendPiiConfigFilter filter;

  public ComprehendResultsRetriever(
      @Qualifier("piiScanningFileStore") final FileStore fileStore,
      JsonConverter jsonConverter,
      ComprehendOutputMapper outputMapper,
      ComprehendPiiConfigFilter filter) {
    this.fileStore = fileStore;
    this.jsonConverter = jsonConverter;
    this.outputMapper = outputMapper;
    this.filter = filter;
  }

  public Collection<ComprehendFileOutput> retrieve(PiiOutputDataConfig outputDataConfig) {
    return fileStore.getKeysInFolder(outputDataConfig.s3Uri()).stream()
        .map(fileStore::getObjectAsStream)
        .map(this::mapStreamToObject)
        .map(outputMapper::mapFromRawOutput)
        .map(this::filterPiiByConfig)
        .toList();
  }

  private ComprehendFileRawOutput mapStreamToObject(InputStream inputStream) {
    return jsonConverter.toObject(inputStream, ComprehendFileRawOutput.class);
  }

  private ComprehendFileOutput filterPiiByConfig(ComprehendFileOutput output) {
    return output.toBuilder()
        .entities(filter.filterByConfigProperties(output.getEntities()))
        .build();
  }
}

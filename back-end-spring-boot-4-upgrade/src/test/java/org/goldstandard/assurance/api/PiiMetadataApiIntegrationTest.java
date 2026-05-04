package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.PiiAction;
import org.goldstandard.assurance.api.enumeration.PiiType;
import org.goldstandard.assurance.api.populator.PiiMetadataPopulator;
import org.goldstandard.assurance.api.repository.PiiMetadataRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor
public class PiiMetadataApiIntegrationTest extends AbstractApiTest {
  @Autowired ApiClient client;
  @Autowired PiiMetadataPopulator piiMetadataPopulator;
  @Autowired PiiMetadataRepository piiMetadataRepository;

  @Test
  void shouldUpdatePiiMetadataToNotFound() {
    piiMetadataPopulator.populate(
        1, List.of("title", "description"), new PiiAction(PiiAction.NEW_STATUS_ID));
    var response = client.updatePiiToNotFound(1, List.of("title"));
    var nonActionedPii =
        piiMetadataRepository.findByProjectIdAndTypeAndActionedFalse(1, PiiType.PROJECT);
    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(nonActionedPii.size()).isEqualTo(1);
    piiMetadataPopulator.delete();
  }
}

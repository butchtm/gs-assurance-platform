package org.goldstandard.assurance.api.populator;

import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.lookup.PiiAction;
import org.goldstandard.assurance.api.entity.pii.PiiMetadata;
import org.goldstandard.assurance.api.enumeration.PiiType;
import org.goldstandard.assurance.api.repository.PiiMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PiiMetadataPopulator {
  @Autowired private PiiMetadataRepository piiMetadataRepository;

  public void populate(Integer projectId, Collection<String> projectFields, PiiAction piiAction) {
    projectFields.forEach(
        field -> {
          var metadata =
              PiiMetadata.builder()
                  .type(PiiType.PROJECT)
                  .typeDetail(field)
                  .actioned(piiAction.isActioned())
                  .action(piiAction)
                  .projectId(projectId)
                  .build();
          piiMetadataRepository.save(metadata);
        });
  }

  public void delete() {
    piiMetadataRepository.deleteAll();
  }
}

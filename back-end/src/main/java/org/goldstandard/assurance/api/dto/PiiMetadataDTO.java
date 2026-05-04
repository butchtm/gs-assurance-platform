package org.goldstandard.assurance.api.dto;

import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.PiiAction;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PiiMetadataDTO {
  private final boolean piiPresent;
  private final PiiAction action;
  private final Collection<String> piiDetail;
}

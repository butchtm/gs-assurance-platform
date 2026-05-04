package org.goldstandard.assurance.api.dto.pub;

import java.util.Collection;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PublicProject {
  private final String gsid;
  private final String name;
  private final Collection<PublicRequest> requests;
}

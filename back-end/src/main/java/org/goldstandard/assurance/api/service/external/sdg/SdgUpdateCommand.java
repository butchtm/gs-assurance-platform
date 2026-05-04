package org.goldstandard.assurance.api.service.external.sdg;

import lombok.Builder;
import org.goldstandard.assurance.api.service.external.ExternalUpdateCommand;

@Builder
public class SdgUpdateCommand implements ExternalUpdateCommand {

  private final SdgProjectConverter converter;
  private final SdgApiClient client;

  @Override
  public void execute(Integer projectId) {
    var sdgProject = converter.toSdgProject(projectId);
    client.updateProject(sdgProject);
  }
}

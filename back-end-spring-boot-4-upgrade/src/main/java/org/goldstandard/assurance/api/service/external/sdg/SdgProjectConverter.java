package org.goldstandard.assurance.api.service.external.sdg;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.service.ProjectFinder;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SdgProjectConverter {

  private final ProjectFinder projectFinder;
  private final AccountFinder accountFinder;
  private final SdgProjectFactory sdgProjectFactory;

  public SdgProject toSdgProject(Integer projectId) {
    var project = projectFinder.forceGetProjectById(projectId);
    var account = accountFinder.forceGetAccountById(project.getAccountId());
    return sdgProjectFactory.build(project, account);
  }
}

package org.goldstandard.assurance.api.service.external.registry;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.service.ProjectFinder;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistryProjectConverter {

  private final ProjectFinder projectFinder;
  private final AccountFinder accountFinder;
  private final RegistryProjectFactory registryProjectFactory;

  public RegistryProject toRegistryProject(Integer projectId) {
    var project = projectFinder.forceGetProjectById(projectId);
    var account = accountFinder.forceGetAccountById(project.getAccountId());
    return registryProjectFactory.build(project, account);
  }
}

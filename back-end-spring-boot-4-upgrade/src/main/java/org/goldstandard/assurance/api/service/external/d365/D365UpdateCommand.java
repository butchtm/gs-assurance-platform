package org.goldstandard.assurance.api.service.external.d365;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.service.external.ExternalUpdateCommand;
import org.goldstandard.assurance.api.service.finance.FinanceService;

@RequiredArgsConstructor
public class D365UpdateCommand implements ExternalUpdateCommand {

  private final FinanceService financeService;

  @Override
  public void execute(Integer projectId) {
    financeService.triggerProjectContractIfPossible(projectId);
  }
}

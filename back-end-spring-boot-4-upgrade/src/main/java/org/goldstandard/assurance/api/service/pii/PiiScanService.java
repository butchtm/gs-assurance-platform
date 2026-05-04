package org.goldstandard.assurance.api.service.pii;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.ProjectIdsDTO;
import org.goldstandard.assurance.api.entity.pii.PiiScanRun;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PiiScanService {

  private final PiiScanAsyncProcessor asyncProcessor;
  private final PiiScanTransactionService transactionService;

  public PiiScanRun initiatePiiScan(ProjectIdsDTO projectIdsDTO) {
    log.info("Initiating PII scan for {} projects", projectIdsDTO.getProjectIds().size());
    var savedRun = transactionService.saveScanRunWithProjectIds(projectIdsDTO.getProjectIds());
    asyncProcessor.performPiiScanAsync(savedRun, projectIdsDTO);
    log.info("Created PII scan run with ID: {}", savedRun.getId());
    return savedRun;
  }
}

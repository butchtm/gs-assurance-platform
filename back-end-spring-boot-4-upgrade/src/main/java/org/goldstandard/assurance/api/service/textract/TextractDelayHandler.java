package org.goldstandard.assurance.api.service.textract;

import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.exception.DocumentPiiScanException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TextractDelayHandler {

  @Value("${textract.pollingRate:5000}")
  private Long pollingRateMs;

  public void delayBeforeNextPoll() {
    try {
      Thread.sleep(pollingRateMs);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("Error in pollForCompletion: {}", e.getMessage(), e);
      throw new DocumentPiiScanException(e.getMessage());
    }
  }
}

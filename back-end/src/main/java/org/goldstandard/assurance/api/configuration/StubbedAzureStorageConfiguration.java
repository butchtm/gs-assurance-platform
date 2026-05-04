package org.goldstandard.assurance.api.configuration;

import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.service.document.FileStore;
import org.goldstandard.assurance.api.service.document.PathPrefixHandler;
import org.goldstandard.assurance.api.service.document.StubbedFileStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnExpression(
    "T(org.apache.commons.lang3.StringUtils).isEmpty('${azure.storage.finance.connection-string:}')")
public class StubbedAzureStorageConfiguration {

  @Bean
  public FileStore salesOrderFileStore(PathPrefixHandler prefixHandler) {
    log.warn("configuring stubbed sales order file store");
    return new StubbedFileStore(prefixHandler);
  }

  @Bean
  public FileStore projectContractFileStore(PathPrefixHandler prefixHandler) {
    log.warn("configuring stubbed project contract file store");
    return new StubbedFileStore(prefixHandler);
  }
}

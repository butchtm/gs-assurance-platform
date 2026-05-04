package org.goldstandard.assurance.api.configuration;

import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.service.document.FileStore;
import org.goldstandard.assurance.api.service.document.StubbedFileStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
@Profile("local")
public class StubbedS3Configuration {

  @Bean
  public FileStore documentFileStore() {
    log.warn("configuring stubbed document file store");
    return new StubbedFileStore();
  }

  @Bean
  public FileStore downloadFileStore() {
    log.warn("configuring stubbed download file store");
    return new StubbedFileStore();
  }

  @Bean
  public FileStore piiScanningFileStore() {
    log.warn("configuring stubbed scanning file store");
    return new StubbedFileStore();
  }
}

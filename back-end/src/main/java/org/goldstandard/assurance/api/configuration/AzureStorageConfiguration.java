package org.goldstandard.assurance.api.configuration;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.service.document.WritableFileStore;
import org.goldstandard.assurance.api.service.finance.AzureFileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnExpression(
    "T(org.apache.commons.lang3.StringUtils).isNotEmpty('${azure.storage.finance.connection-string:}')")
public class AzureStorageConfiguration {

  @Autowired private AzureStorageConfigurationProperties properties;

  @Bean
  public BlobServiceClient blobServiceClient() {
    return new BlobServiceClientBuilder()
        .connectionString(properties.getConnectionString())
        .buildClient();
  }

  @Bean
  public WritableFileStore salesOrderFileStore(BlobServiceClient client) {
    var containerName = properties.getSalesOrderContainerName();
    log.info("configuring sales order file store with container name {}", containerName);
    return new AzureFileStore(client.getBlobContainerClient(containerName));
  }

  @Bean
  public WritableFileStore projectContractFileStore(BlobServiceClient client) {
    var containerName = properties.getProjectContractContainerName();
    log.info("configuring project contract file store with container name {}", containerName);
    return new AzureFileStore(client.getBlobContainerClient(containerName));
  }
}

package org.goldstandard.assurance.api.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "azure.storage.finance")
public class AzureStorageConfigurationProperties {

  private String connectionString;
  private ContainerName containerName;

  public String getSalesOrderContainerName() {
    return containerName.getSalesOrder();
  }

  public String getProjectContractContainerName() {
    return containerName.getProjectContract();
  }

  @Getter
  @Setter
  private static class ContainerName {
    private String salesOrder;
    private String projectContract;
  }
}

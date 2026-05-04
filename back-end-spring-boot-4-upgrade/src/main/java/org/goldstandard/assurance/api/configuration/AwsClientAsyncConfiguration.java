package org.goldstandard.assurance.api.configuration;

import static software.amazon.awssdk.core.client.config.SdkAdvancedAsyncClientOption.FUTURE_COMPLETION_EXECUTOR;

import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.core.client.config.ClientAsyncConfiguration;

@Configuration
public class AwsClientAsyncConfiguration {

  @Bean
  public ClientAsyncConfiguration clientAsyncConfiguration(ThreadPoolTaskExecutorBuilder builder) {
    builder.taskDecorator(new ContextCopyingTaskDecorator());
    return ClientAsyncConfiguration.builder()
        .advancedOption(FUTURE_COMPLETION_EXECUTOR, builder.build())
        .build();
  }
}

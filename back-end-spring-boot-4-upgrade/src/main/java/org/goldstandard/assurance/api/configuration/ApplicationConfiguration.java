package org.goldstandard.assurance.api.configuration;

import java.time.Clock;
import java.util.UUID;
import java.util.function.Supplier;
import org.goldstandard.assurance.api.repository.UserRepository;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.goldstandard.assurance.api.service.permission.CachingAuthenticatedUserSupplier;
import org.goldstandard.assurance.api.service.permission.DefaultAuthenticatedUserSupplier;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class ApplicationConfiguration {

  @Bean
  public Clock clock() {
    return Clock.systemDefaultZone();
  }

  @Bean
  public Supplier<UUID> randomUuidSupplier() {
    return UUID::randomUUID;
  }

  @Bean
  @RequestScope
  public AuthenticatedUserSupplier cachingAuthenticatedUserSupplier(UserRepository userRepository) {
    return new CachingAuthenticatedUserSupplier(
        new DefaultAuthenticatedUserSupplier(userRepository));
  }

  @Bean
  public ThreadPoolTaskExecutor taskExecutor() {
    return new ThreadPoolTaskExecutorBuilder()
        .corePoolSize(5)
        .maxPoolSize(10)
        .queueCapacity(40)
        .taskDecorator(new ContextCopyingTaskDecorator())
        .build();
  }
}

package org.goldstandard.assurance.api.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SystemUserConfiguration {

  private final SystemUserProperties systemUserProperties;

  @Bean
  public User systemUser() {
    return User.builder()
        .userName(systemUserProperties.getUsername())
        .idpUserId(systemUserProperties.getUuid())
        .build();
  }
}

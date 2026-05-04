package org.goldstandard.assurance.api.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AccountConfiguration {
  @Value("${accounts.armTeam.id}")
  private Integer armTeamAccountId;

  @Value("${accounts.registryTeam.id}")
  private Integer registryTeamAccountId;

  @Value("${accounts.financeTeam.id}")
  private Integer financeTeamAccountId;
}

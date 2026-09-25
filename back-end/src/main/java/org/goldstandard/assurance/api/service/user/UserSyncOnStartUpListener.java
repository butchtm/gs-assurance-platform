package org.goldstandard.assurance.api.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Profile("!test & (!local-auth | !local)")
public class UserSyncOnStartUpListener implements ApplicationListener<ContextRefreshedEvent> {

  private final UserSyncService service;

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    service.syncUsers();
  }
}

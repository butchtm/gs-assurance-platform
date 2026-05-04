package org.goldstandard.assurance.api.service.conversation;

import java.time.Clock;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.ConversationStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConversationUpdater {

  private final ConversationFinder finder;
  private final ConversationTransaction transaction;
  private final Clock clock;

  public void updateConversationStatus(Integer id, ConversationStatus newStatus) {
    var conversation = finder.forceFindById(id);
    transaction.update(
        conversation.toBuilder()
            .status(newStatus)
            .statusAppliedAt(ZonedDateTime.now(clock))
            .build());
  }
}

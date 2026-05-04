package org.goldstandard.assurance.api.projection;

import org.goldstandard.assurance.api.entity.ConversationMessage;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = {ConversationMessage.class})
public interface ConversationMessageId {
  Integer getId();
}

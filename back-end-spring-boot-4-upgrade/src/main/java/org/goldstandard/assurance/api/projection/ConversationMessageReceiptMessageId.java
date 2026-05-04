package org.goldstandard.assurance.api.projection;

import org.goldstandard.assurance.api.entity.ConversationMessageReceipt;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = {ConversationMessageReceipt.class})
public interface ConversationMessageReceiptMessageId {
  ConversationMessageId getMessage();
}

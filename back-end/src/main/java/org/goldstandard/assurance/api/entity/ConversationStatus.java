package org.goldstandard.assurance.api.entity;

import jakarta.persistence.Entity;
import java.util.Objects;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;

@Entity
public class ConversationStatus extends ReferenceDataType {

  public static final int CONVERSATION_OPEN_STATUS_ID = 0;
  public static final int CONVERSATION_CLOSED_STATUS_ID = 1;

  public ConversationStatus() {}

  public ConversationStatus(Integer id) {
    super(id);
  }

  public static ConversationStatus buildClosed() {
    return new ConversationStatus(CONVERSATION_CLOSED_STATUS_ID);
  }

  public static ConversationStatus buildOpen() {
    return new ConversationStatus(CONVERSATION_OPEN_STATUS_ID);
  }

  public boolean isClosed() {
    return Objects.equals(this.id, CONVERSATION_CLOSED_STATUS_ID);
  }

  public boolean isOpen() {
    return Objects.equals(this.id, CONVERSATION_OPEN_STATUS_ID);
  }
}

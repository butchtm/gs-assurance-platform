package org.goldstandard.assurance.api.entity.system;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.ZonedDateTime;
import java.util.StringJoiner;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Slf4j
@Getter
@Setter
public class Process {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Enumerated(EnumType.STRING)
  private ProcessCode processCode;

  @Enumerated(EnumType.STRING)
  private ProcessStatus processStatus;

  private ZonedDateTime startTimestamp;
  private ZonedDateTime statusTimestamp;
  private String processMessage;

  public boolean updatedBefore(ZonedDateTime dateTime) {
    boolean updatedBefore = statusTimestamp.isBefore(dateTime);
    log.debug(
        "Process {} last updated at {} before {} is {}",
        processCode,
        statusTimestamp,
        dateTime,
        updatedBefore);
    return updatedBefore;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Process.class.getSimpleName() + "[", "]")
        .add("id=" + id)
        .add("processCode='" + processCode + "'")
        .add("processStatus='" + processStatus + "'")
        .add("startTimestamp=" + startTimestamp)
        .add("statusTimestamp=" + statusTimestamp)
        .add("processMessage='" + processMessage + "'")
        .toString();
  }

  public enum ProcessStatus {
    STARTED,
    COMPLETE,
    FAILED
  }

  public enum ProcessCode {
    USER_SYNC,
    REGISTRY_UPDATE,
    SDG_UPDATE,
    PII_SCANNING,
    CONSULTATION_NOTIFICATION,
    D365_UPDATE,
    CONVERSATION_NOTIFICATION
  }
}

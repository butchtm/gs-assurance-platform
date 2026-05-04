package org.goldstandard.assurance.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Data
public class AuditEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private UUID idpUserId;
  private ZonedDateTime eventTimestamp;
  private String eventSubject;

  @JdbcTypeCode(SqlTypes.JSON)
  private String additionalInformation;

  private Boolean anonymous;

  public boolean isAnonymous() {
    if (Objects.isNull(anonymous)) {
      return Objects.nonNull(idpUserId);
    }
    return anonymous;
  }
}

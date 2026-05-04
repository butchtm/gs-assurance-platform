package org.goldstandard.assurance.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Builder(toBuilder = true)
@Data
public class UserBatch {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private final Integer id;

  private final Integer count;

  private final Integer successCount;

  @JdbcTypeCode(SqlTypes.JSON)
  private final Collection<UserBatchError> errors;

  private final ZonedDateTime createdAt;
  private final ZonedDateTime updatedAt;

  public boolean isComplete() {
    return count == (successCount + errors.size());
  }

  public UserBatch addSuccessfulUser(ZonedDateTime now) {
    return toBuilder().successCount(successCount + 1).updatedAt(now).build();
  }

  public UserBatch addError(UserBatchError error, ZonedDateTime now) {
    return toBuilder()
        .errors(Stream.concat(errors.stream(), Stream.of(error)).toList())
        .updatedAt(now)
        .build();
  }
}

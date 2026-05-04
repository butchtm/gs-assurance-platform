package org.goldstandard.assurance.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Entity
@Builder(toBuilder = true)
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class RequestForm {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private final Integer id;

  private final Integer requestId;
  @ManyToOne private final FormDefinition definition;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "form_data", columnDefinition = "jsonb")
  private final JsonNode data;

  @ManyToOne private final RequestFormStatus status;
  private final ZonedDateTime createdAt;
  private final ZonedDateTime updatedAt;
  private final ZonedDateTime submittedAt;
  @OneToOne private final User submittedBy;

  @JsonIgnore
  public boolean isSubmitted() {
    return Optional.ofNullable(status).map(RequestFormStatus::isSubmitted).orElse(false);
  }

  public RequestForm withData(JsonNode data, ZonedDateTime now) {
    return toBuilder().data(data).updatedAt(now).build();
  }

  public double getVersion() {
    return definition.getVersion();
  }

  public DigitalFormType getType() {
    return definition.getType();
  }

  public JsonNode getSchema() {
    return definition.getSchema();
  }
}

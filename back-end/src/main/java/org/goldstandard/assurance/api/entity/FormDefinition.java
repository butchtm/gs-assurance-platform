package org.goldstandard.assurance.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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
public class FormDefinition {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private final Integer id;

  private final Double version;
  @ManyToOne private final DigitalFormType type;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "form_schema", columnDefinition = "jsonb")
  private final JsonNode schema;

  @ManyToOne private final FormDefinitionStatus status;
  private final ZonedDateTime createdAt;
  private final ZonedDateTime updatedAt;
  private final ZonedDateTime publishedAt;

  @JsonIgnore
  public boolean isPublished() {
    return Optional.ofNullable(status).map(FormDefinitionStatus::isPublished).orElse(false);
  }

  public FormDefinition withSchema(JsonNode schema, ZonedDateTime now) {
    return toBuilder().schema(schema).updatedAt(now).build();
  }
}

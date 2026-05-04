package org.goldstandard.assurance.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
import org.hibernate.annotations.ColumnTransformer;
import tools.jackson.databind.JsonNode;

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

  @Convert(converter = JsonNodeConverter.class)
  @ColumnTransformer(write = "?::jsonb")
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

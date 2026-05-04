package org.goldstandard.assurance.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import java.util.Objects;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;

@Entity
@NoArgsConstructor
public class FormDefinitionStatus extends ReferenceDataType {

  public static final int DRAFT_STATUS_ID = 0;
  public static final int PUBLISHED_STATUS_ID = 1;

  public static FormDefinitionStatus draft() {
    return new FormDefinitionStatus(DRAFT_STATUS_ID);
  }

  public static FormDefinitionStatus published() {
    return new FormDefinitionStatus(PUBLISHED_STATUS_ID);
  }

  public FormDefinitionStatus(Integer id) {
    super(id);
  }

  @JsonIgnore
  public boolean isPublished() {
    return Objects.equals(this.id, PUBLISHED_STATUS_ID);
  }
}

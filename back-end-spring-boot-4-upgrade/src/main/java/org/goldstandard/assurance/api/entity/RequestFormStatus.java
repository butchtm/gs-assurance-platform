package org.goldstandard.assurance.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import java.util.Objects;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;

@Entity
@NoArgsConstructor
public class RequestFormStatus extends ReferenceDataType {

  public static final int DRAFT_STATUS_ID = 0;
  public static final int SUBMITTED_STATUS_ID = 1;

  public static RequestFormStatus draft() {
    return new RequestFormStatus(DRAFT_STATUS_ID);
  }

  public static RequestFormStatus submitted() {
    return new RequestFormStatus(SUBMITTED_STATUS_ID);
  }

  public RequestFormStatus(Integer id) {
    super(id);
  }

  @JsonIgnore
  public boolean isSubmitted() {
    return Objects.equals(this.id, SUBMITTED_STATUS_ID);
  }
}

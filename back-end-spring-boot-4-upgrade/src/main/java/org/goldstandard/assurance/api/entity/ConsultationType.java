package org.goldstandard.assurance.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ConsultationType {

  public static final Integer TAC_CONSULTATION = 0;
  public static final Integer PUBLIC_CONSULTATION = 1;

  @Id private Integer id;
  private String userType;
  private Integer workingDays;

  public ConsultationType(Integer id) {
    this.id = id;
  }

  public boolean isTac() {
    return TAC_CONSULTATION.equals(id);
  }
}

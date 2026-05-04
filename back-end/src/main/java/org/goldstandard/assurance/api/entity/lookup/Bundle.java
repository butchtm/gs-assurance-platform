package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Builder
@Entity
public class Bundle {

  @Id private final Integer id;
  private final String name;
  private final Integer masterProjectCategoryId;
  private final Integer masterProjectId;
}

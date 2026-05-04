package org.goldstandard.assurance.api.entity.view;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Builder
@Entity(name = "v_searchable_project")
public class SearchableProjectView {

  @Id private final Integer id;
  private final String gsid;
  private final Integer gsidNum;
  private final String name;
  private final Integer accountId;
  private final Integer masterProjectId;
  private final Integer parentProjectId;
  private final Integer projectStatusId;
  private final Integer bundleId;

  public Integer getRootProjectId() {
    return Optional.ofNullable(masterProjectId).orElse(id);
  }
}

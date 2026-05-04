package org.goldstandard.assurance.api.dto.pub.stakeholder.project;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.goldstandard.assurance.api.dto.pub.stakeholder.request.PublicStakeholderRequestBasicDTO;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.entity.lookup.ProjectLocation;
import org.goldstandard.assurance.api.entity.view.stakeholder.project.ProjectBasicView;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicStakeholderProjectBasicDTO {
  private String gsid;
  private String name;
  private List<String> projectLocations;
  private List<String> projectMethodologies;
  private List<PublicStakeholderRequestBasicDTO> requests;

  public PublicStakeholderProjectBasicDTO(ProjectBasicView project) {
    this.gsid = project.getGsid();
    this.name = project.getProjectName();
    this.projectLocations =
        emptyIfNull(project.getProjectLocations()).stream()
            .map(ProjectLocation::getDescription)
            .toList();
    this.projectMethodologies =
        emptyIfNull(project.getProjectMethodologies()).stream()
            .map(Methodology::getDescription)
            .toList();
  }
}

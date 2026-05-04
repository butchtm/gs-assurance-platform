package org.goldstandard.assurance.api.fixtures;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMother {

  public static Request build() {
    return builder().build();
  }

  public static Request withIdAndName(Integer id, String name) {
    return builder().id(id).requestName(name).build();
  }

  public static Request withIdNameAndProject(Integer id, String name, Project project) {
    return builder().id(id).requestName(name).project(project).build();
  }

  public static Request withId(Integer id) {
    return builder().id(id).build();
  }

  public static Request.RequestBuilder builder() {
    return Request.builder()
        .id(1)
        .currentRequestStatus(RequestStatus.inPreparation())
        .requestType(RequestTypeMother.preliminaryReview())
        .project(ProjectMother.build());
  }
}

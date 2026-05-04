package org.goldstandard.assurance.api.projection;

import org.goldstandard.assurance.api.entity.Request;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = {Request.class})
public interface RequestProject {
  Integer getProjectId();

  String getProjectGsid();

  String getProjectName();
}

package org.goldstandard.assurance.api.projection;

import org.goldstandard.assurance.api.entity.Project;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = {Project.class})
public interface ProjectMinimal {
  Integer getId();

  String getGsid();

  String getName();
}

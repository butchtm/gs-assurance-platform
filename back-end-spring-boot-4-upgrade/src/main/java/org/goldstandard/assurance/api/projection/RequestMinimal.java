package org.goldstandard.assurance.api.projection;

import org.goldstandard.assurance.api.entity.Request;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = {Request.class})
public interface RequestMinimal {
  Integer getId();

  String getRequestName();
}

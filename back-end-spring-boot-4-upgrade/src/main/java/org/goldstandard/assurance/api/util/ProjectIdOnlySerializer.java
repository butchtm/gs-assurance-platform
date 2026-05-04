package org.goldstandard.assurance.api.util;

import java.util.Objects;
import org.goldstandard.assurance.api.entity.Project;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class ProjectIdOnlySerializer extends ValueSerializer<Project> {

  @Override
  public void serialize(Project project, JsonGenerator generator, SerializationContext context)
      throws JacksonException {
    if (Objects.isNull(project) || Objects.isNull(project.getId())) {
      generator.writeNull();
      return;
    }
    generator.writeNumber(project.getId());
  }
}

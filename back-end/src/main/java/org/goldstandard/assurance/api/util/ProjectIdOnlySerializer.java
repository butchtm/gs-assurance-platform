package org.goldstandard.assurance.api.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.goldstandard.assurance.api.entity.Project;

public class ProjectIdOnlySerializer extends JsonSerializer<Project> {

  @Override
  public void serialize(
      Project project, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
      throws IOException {
    if (project != null && project.getId() != null) {
      jsonGenerator.writeNumber(project.getId());
    } else {
      jsonGenerator.writeNull();
    }
  }
}

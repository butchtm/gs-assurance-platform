package org.goldstandard.assurance.api.service.form;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FormFieldsFactory {

  private final RequestFormFieldProjectDataPopulator projectFieldPopulator;
  private final RequestFormFieldOldProjectDataPopulator oldProjectFieldPopulator;

  public Collection<FormFields> buildAllFormFields() {
    return List.of(buildProjectsFormFields(), buildOldProjectFormFields());
  }

  private FormFields buildProjectsFormFields() {
    return FormFields.builder()
        .parent("projects")
        .children(Set.of("gsid", "title", "registryLink"))
        .dataPopulator(projectFieldPopulator)
        .build();
  }

  private FormFields buildOldProjectFormFields() {
    return FormFields.builder()
        .parent("")
        .children(Set.of("projectGsids", "projectTitles", "projectRegistryLinks"))
        .dataPopulator(oldProjectFieldPopulator)
        .build();
  }
}

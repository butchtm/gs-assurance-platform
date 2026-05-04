package org.goldstandard.assurance.api.service.form;

import java.util.Collection;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;

@Builder
public record FormFields(
    String parent, Collection<String> children, RequestFormFieldDataPopulator dataPopulator) {

  public FormFields(Collection<String> fields) {
    this("", fields, new RequestFormFieldNoopDataPopulator());
  }

  public FormFields(String parent, Collection<String> children) {
    this(parent, children, new RequestFormFieldNoopDataPopulator());
  }

  public boolean hasParent() {
    return StringUtils.isNotEmpty(parent);
  }
}

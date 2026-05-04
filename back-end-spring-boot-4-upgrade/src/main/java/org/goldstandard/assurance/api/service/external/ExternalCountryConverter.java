package org.goldstandard.assurance.api.service.external;

import java.util.Collection;
import java.util.Collections;
import org.apache.commons.collections4.CollectionUtils;
import org.goldstandard.assurance.api.entity.lookup.ProjectLocation;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;
import org.springframework.stereotype.Component;

@Component
public class ExternalCountryConverter {

  public Collection<String> toExternalCountries(Collection<ProjectLocation> locations) {
    if (CollectionUtils.isEmpty(locations)) {
      return Collections.emptyList();
    }
    return locations.stream().map(ReferenceDataType::getCode).map(this::toExternalCountry).toList();
  }

  public String toExternalCountry(String country) {
    return switch (country) {
      case "INTERNATIONAL" -> "XZ";
      case "XC" -> "GB";
      case "XE" -> "SC";
      case "PS" -> "XG";
      default -> country;
    };
  }
}

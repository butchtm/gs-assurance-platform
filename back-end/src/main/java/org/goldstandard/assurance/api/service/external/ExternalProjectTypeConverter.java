package org.goldstandard.assurance.api.service.external;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.lookup.ProjectType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExternalProjectTypeConverter {

  private static final String OTHER = "OTHER";

  private final Set<String> unchangedCodes;

  public ExternalProjectTypeConverter() {
    this(buildUnchangedCodes());
  }

  public Collection<String> toExternalProjectTypes(Project project) {
    var types = project.getProjectTypes();
    if (org.apache.commons.collections4.CollectionUtils.isEmpty(types)) {
      return Collections.emptyList();
    }
    return types.stream().map(this::toExternalProjectType).toList();
  }

  public String toExternalProjectType(Project project) {
    var types = project.getProjectTypes();
    if (CollectionUtils.isEmpty(types)) {
      return OTHER;
    }
    return toExternalProjectType(types.getFirst());
  }

  private String toExternalProjectType(ProjectType type) {
    var code = type.getCode();
    if (StringUtils.isEmpty(code)) {
      return OTHER;
    }
    if (shouldReturnUnchangedCode(code)) {
      return code;
    }
    return toMappedCode(code);
  }

  private boolean shouldReturnUnchangedCode(String code) {
    return unchangedCodes.contains(code);
  }

  private String toMappedCode(String code) {
    return switch (code) {
      case "AFFORESTATION_REFORESTATION" -> "A_R";
      case "BIOGAS_AND_ELECTRICITY", "COMMUNITY_BIOGAS_ELECTRICITY" -> "BIOGAS_ELECTRICITY";
      case "COMMUNITY_BIOGAS_HEAT" -> "BIOGAS_HEAT";
      case "COMMUNITY_ENERGY_EFFICIENCY_DOMESTIC" -> "ENERGY_EFFICIENCY_DOMESTIC";
      case "ENERGY_EFFICIENCY_AGRICULTURE" -> "ENERGY_EFFICIENCY_AGRICULTURE_SECTOR";
      case "ENERGY_EFFICIENCY_INDUSTRIAL_SECTOR" -> "ENERGY_EFFICIENCY_INDUSTRIAL";
      case "RENEWABLE_ENERGY_BIOGAS_CONGENERATION" -> "BIOMASS_OR_LIQUID_BIOFUEL_COGENERATION";
      case "RENEWABLE_ENERGY_BIOMASS_OR_LIQUID_BIOFUEL_ELECTRICITY" ->
          "BIOMASS_OR_LIQUID_BIOFUEL_ELECTRICITY";
      case "RENEWABLE_ENERGY_BIOMASS_OR_LIQUID_BIOFUEL_HEAT" -> "BIOMASS_OR_LIQUID_BIOFUEL_HEAT";
      default -> OTHER;
    };
  }

  private static Set<String> buildUnchangedCodes() {
    return Set.of(
        "BIOGAS_COGENERATION",
        "BIOGAS_HEAT",
        "ENERGY_EFFICIENCY_COMMERCIAL_SECTOR",
        "ENERGY_EFFICIENCY_PUBLIC_SECTOR",
        "ENERGY_EFFICIENCY_TRANSPORT_SECTOR",
        "GEOTHERMAL",
        "PV",
        "SMALL_LOW_IMPACT_HYDRO",
        "SOLAR_THERMAL_ELECTRICITY",
        "SOLAR_THERMAL_HEAT",
        "WIND");
  }
}

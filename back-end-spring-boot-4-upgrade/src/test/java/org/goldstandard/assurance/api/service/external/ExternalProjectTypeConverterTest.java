package org.goldstandard.assurance.api.service.external;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.lookup.ProjectType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class ExternalProjectTypeConverterTest {

  private static final String OTHER = "OTHER";

  private final ExternalProjectTypeConverter converter = new ExternalProjectTypeConverter();

  @ParameterizedTest
  @MethodSource("assuranceTypeCodeAndExpectedMappedRegistryTypeCode")
  void shouldMapAssuranceTypeCodesToRegistryTypeCodes(
      String assuranceTypeCode, String expectedRegistryTypeCode) {
    var project = Project.builder().projectTypes(List.of(toProjectType(assuranceTypeCode))).build();

    var registryTypeCode = converter.toExternalProjectType(project);

    assertThat(registryTypeCode).isEqualTo(expectedRegistryTypeCode);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
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
        "WIND"
      })
  void shouldReturnUnchangedAssuranceTypeCode(String assuranceTypeCode) {
    var project = Project.builder().projectTypes(List.of(toProjectType(assuranceTypeCode))).build();

    var registryTypeCode = converter.toExternalProjectType(project);

    assertThat(registryTypeCode).isEqualTo(assuranceTypeCode);
  }

  @Test
  void shouldReturnOtherIfProjectTypesIsNull() {
    var project = Project.builder().build();

    var registryTypeCode = converter.toExternalProjectType(project);

    assertThat(registryTypeCode).isEqualTo(OTHER);
  }

  @Test
  void shouldReturnOtherIfProjectTypesIsEmpty() {
    var project = Project.builder().projectTypes(Collections.emptyList()).build();

    var registryTypeCode = converter.toExternalProjectType(project);

    assertThat(registryTypeCode).isEqualTo(OTHER);
  }

  @Test
  void shouldReturnOtherIfFirstProjectTypeHasNullCode() {
    var project = Project.builder().projectTypes(List.of(toProjectType(null))).build();

    var registryTypeCode = converter.toExternalProjectType(project);

    assertThat(registryTypeCode).isEqualTo(OTHER);
  }

  private static Stream<Arguments> assuranceTypeCodeAndExpectedMappedRegistryTypeCode() {
    return Stream.of(
        Arguments.of("AFFORESTATION_REFORESTATION", "A_R"),
        Arguments.of("AGRICULTURE", "OTHER"),
        Arguments.of("COMMUNITY_ENERGY_EFFICIENCY_HEAT", OTHER),
        Arguments.of("COMPOSTING", OTHER),
        Arguments.of("ELECTRICIFICATION", OTHER),
        Arguments.of("LANDFILL", "OTHER"),
        Arguments.of("RENEWABLE_ENERGY_COMBINE", OTHER),
        Arguments.of("SOIL_ORGANIC_CARBON", OTHER),
        Arguments.of("SOLAR", OTHER),
        Arguments.of("SOLID_WASTE_TREATMENT", OTHER),
        Arguments.of("VCI", OTHER),
        Arguments.of("WASTEWATER_TREATMENT", OTHER),
        Arguments.of("BIOGAS_AND_ELECTRICITY", "BIOGAS_ELECTRICITY"),
        Arguments.of("COMMUNITY_BIOGAS_ELECTRICITY", "BIOGAS_ELECTRICITY"),
        Arguments.of("COMMUNITY_BIOGAS_HEAT", "BIOGAS_HEAT"),
        Arguments.of("COMMUNITY_ENERGY_EFFICIENCY_DOMESTIC", "ENERGY_EFFICIENCY_DOMESTIC"),
        Arguments.of("ENERGY_EFFICIENCY_AGRICULTURE", "ENERGY_EFFICIENCY_AGRICULTURE_SECTOR"),
        Arguments.of("ENERGY_EFFICIENCY_INDUSTRIAL_SECTOR", "ENERGY_EFFICIENCY_INDUSTRIAL"),
        Arguments.of(
            "RENEWABLE_ENERGY_BIOGAS_CONGENERATION", "BIOMASS_OR_LIQUID_BIOFUEL_COGENERATION"),
        Arguments.of(
            "RENEWABLE_ENERGY_BIOMASS_OR_LIQUID_BIOFUEL_ELECTRICITY",
            "BIOMASS_OR_LIQUID_BIOFUEL_ELECTRICITY"),
        Arguments.of(
            "RENEWABLE_ENERGY_BIOMASS_OR_LIQUID_BIOFUEL_HEAT", "BIOMASS_OR_LIQUID_BIOFUEL_HEAT"),
        Arguments.of("ANY_OTHER_CODE", OTHER));
  }

  private static ProjectType toProjectType(String code) {
    var type = new ProjectType();
    type.setCode(code);
    return type;
  }
}

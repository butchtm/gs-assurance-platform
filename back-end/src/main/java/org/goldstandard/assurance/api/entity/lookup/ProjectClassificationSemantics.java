package org.goldstandard.assurance.api.entity.lookup;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class ProjectClassificationSemantics {

  private ProjectClassificationSemantics() {}

  public static ProjectClassification toDisplayClassification(
      ProjectClassification classification) {
    if (Objects.isNull(classification)) {
      return null;
    }
    var displayClassification = new ProjectClassification();
    displayClassification.setId(toDisplayId(classification.getId()));
    displayClassification.setCode(toDisplayCode(classification.getCode()));
    displayClassification.setDescription(
        toDisplayDescription(classification.getCode(), classification.getDescription()));
    displayClassification.setActive(toDisplayActive(classification));
    return displayClassification;
  }

  public static Integer toDisplayId(Integer id) {
    return switch (Objects.requireNonNullElse(id, -1)) {
      case ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_ID ->
          ProjectClassification.REAL_CASE_VPA_ID;
      case ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_ID ->
          ProjectClassification.REGULAR_CASE_VPA_ID;
      default -> id;
    };
  }

  public static String toDisplayCode(String code) {
    return switch (Objects.requireNonNullElse(code, "")) {
      case ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_CODE ->
          ProjectClassification.REAL_CASE_VPA_CODE;
      case ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_CODE ->
          ProjectClassification.REGULAR_VPA_CODE;
      default -> code;
    };
  }

  public static String toDisplayDescription(ProjectClassification classification) {
    if (Objects.isNull(classification)) {
      return "";
    }
    return toDisplayDescription(classification.getCode(), classification.getDescription());
  }

  public static String toDisplayDescription(String code, String description) {
    return switch (Objects.requireNonNullElse(code, "")) {
      case ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_CODE ->
          ProjectClassification.REAL_CASE_VPA_DESCRIPTION;
      case ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_CODE ->
          ProjectClassification.REGULAR_VPA_DESCRIPTION;
      default -> description;
    };
  }

  public static String toDisplayDescription(String codeOrDescription) {
    return switch (Objects.requireNonNullElse(codeOrDescription, "")) {
      case ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_CODE ->
          ProjectClassification.REAL_CASE_VPA_DESCRIPTION;
      case "Real Case VPA (cross linkage)", "Real Case VPA - Cross Linking" ->
          ProjectClassification.REAL_CASE_VPA_DESCRIPTION;
      case ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_CODE ->
          ProjectClassification.REGULAR_VPA_DESCRIPTION;
      case "Regular Case VPA (cross linkage)", "Regular VPA - Cross Linking" ->
          ProjectClassification.REGULAR_VPA_DESCRIPTION;
      default -> codeOrDescription;
    };
  }

  private static Boolean toDisplayActive(ProjectClassification classification) {
    return switch (Objects.requireNonNullElse(classification.getCode(), "")) {
      case ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_CODE,
          ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_CODE ->
          true;
      default -> classification.getActive();
    };
  }

  public static Collection<Integer> expandDisplayFilterIds(Collection<Integer> ids) {
    var expandedIds = new LinkedHashSet<Integer>();
    if (Objects.isNull(ids)) {
      return Set.of();
    }
    for (Integer id : ids) {
      if (Objects.isNull(id)) {
        continue;
      }
      expandedIds.add(id);
      if (Objects.equals(id, ProjectClassification.REAL_CASE_VPA_ID)) {
        expandedIds.add(ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_ID);
      }
      if (Objects.equals(id, ProjectClassification.REGULAR_CASE_VPA_ID)) {
        expandedIds.add(ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_ID);
      }
    }
    return Set.copyOf(expandedIds);
  }
}

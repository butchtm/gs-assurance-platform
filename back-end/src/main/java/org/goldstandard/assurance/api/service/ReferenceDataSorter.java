package org.goldstandard.assurance.api.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.ExtendedReferenceDataType;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReferenceDataSorter {

  private final Comparator<ReferenceDataType> defaultComparator;
  private final Map<String, Comparator<ReferenceDataType>> specificComparators;

  public ReferenceDataSorter() {
    this(Comparator.comparing(ReferenceDataType::getDescription), buildSpecificComparators());
  }

  public Map<String, List<ExtendedReferenceDataType>> sort(
      Map<String, List<ExtendedReferenceDataType>> map) {
    var sortedMap = new HashMap<String, List<ExtendedReferenceDataType>>();
    map.forEach((key, types) -> sortedMap.put(key, sort(key, types)));
    return Collections.unmodifiableMap(sortedMap);
  }

  private List<ExtendedReferenceDataType> sort(String key, List<ExtendedReferenceDataType> types) {
    var comparator = specificComparators.getOrDefault(key, defaultComparator);
    var sortedTypes = new ArrayList<>(types);
    sortedTypes.sort(comparator);
    return sortedTypes;
  }

  private static Map<String, Comparator<ReferenceDataType>> buildSpecificComparators() {
    return Map.of(
        "CarbonStream", new CodeOrderedComparator(List.of("GS_VER", "GS_CER", "REPORTING_ONLY")),
        "ProjectSize",
            new CodeOrderedComparator(List.of("MICROSCALE", "SMALL_SCALE", "LARGE_SCALE")),
        "ProjectType", new OrderedLastCodesComparator(List.of("OTHER")),
        "DocumentType", new OrderedLastCodesComparator(List.of("OTHER")),
        "RequestType",
            new OrderedLastCodesComparator(
                List.of("ISSUANCE", "MANAGE_PROJECT_INFORMATION", "GENERIC")));
  }

  @RequiredArgsConstructor
  private static class CodeOrderedComparator implements Comparator<ReferenceDataType> {

    private final List<String> orderedCodes;

    @Override
    public int compare(ReferenceDataType t1, ReferenceDataType t2) {
      return toScore(t1).compareTo(toScore(t2));
    }

    private Integer toScore(ReferenceDataType type) {
      return orderedCodes.indexOf(type.getCode());
    }
  }

  @RequiredArgsConstructor
  private static class OrderedLastCodesComparator implements Comparator<ReferenceDataType> {

    private final List<String> orderedLastCodes;
    private final Comparator<ReferenceDataType> defaultComparator;

    public OrderedLastCodesComparator(List<String> orderedLastCodes) {
      this(orderedLastCodes, Comparator.comparing(ReferenceDataType::getDescription));
    }

    @Override
    public int compare(ReferenceDataType t1, ReferenceDataType t2) {
      String code1 = t1.getCode();
      String code2 = t2.getCode();

      boolean isLastCode1 = orderedLastCodes.contains(code1);
      boolean isLastCode2 = orderedLastCodes.contains(code2);

      if (isLastCode1 && !isLastCode2) {
        return 1;
      } else if (!isLastCode1 && isLastCode2) {
        return -1;
      } else if (isLastCode1) {
        return Integer.compare(orderedLastCodes.indexOf(code1), orderedLastCodes.indexOf(code2));
      } else {
        return defaultComparator.compare(t1, t2);
      }
    }
  }
}

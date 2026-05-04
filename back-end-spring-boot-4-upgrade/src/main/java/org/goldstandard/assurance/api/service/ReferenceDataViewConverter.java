package org.goldstandard.assurance.api.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.goldstandard.assurance.api.entity.lookup.ExtendedReferenceDataType;
import org.goldstandard.assurance.api.entity.view.ReferenceDataView;
import org.springframework.stereotype.Component;

@Component
public class ReferenceDataViewConverter {

  public Map<String, List<ExtendedReferenceDataType>> mapByEnumeration(
      List<ReferenceDataView> views) {
    return views.stream()
        .collect(
            Collectors.groupingBy(
                dataRecord -> dataRecord.getId().getEnumeration(),
                Collectors.mapping(this::toExtendedRefDataType, Collectors.toList())));
  }

  public ExtendedReferenceDataType toExtendedRefDataType(ReferenceDataView view) {
    var type = new ExtendedReferenceDataType();
    type.setId(view.getId().getId());
    type.setCode(view.getCode());
    type.setDescription(view.getDescription());
    type.setActive(view.isActive());
    return type;
  }
}

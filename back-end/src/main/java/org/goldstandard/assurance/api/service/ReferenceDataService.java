package org.goldstandard.assurance.api.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.ExtendedReferenceDataType;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;
import org.goldstandard.assurance.api.entity.view.ReferenceDataView;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.repository.ReferenceDataViewRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReferenceDataService {

  private final ReferenceDataViewRepository repository;
  private final ReferenceDataViewConverter converter;
  private final ReferenceDataSorter sorter;

  public ReferenceDataType getSpecificReferenceType(String referenceTypeName, Integer id) {
    if (id == null) {
      return null;
    }
    return getItems(referenceTypeName, converter::toExtendedRefDataType).stream()
        .filter(refType -> refType.getId().equals(id))
        .findFirst()
        .orElseThrow(() -> new NotFoundException("%s not found for id %d", referenceTypeName, id));
  }

  public List<ExtendedReferenceDataType> getRequestStatuses(Set<Integer> ids) {
    return getRequestStatuses().stream().filter(status -> ids.contains(status.getId())).toList();
  }

  public List<ExtendedReferenceDataType> getRequestStatuses() {
    return getItems("RequestStatus", converter::toExtendedRefDataType);
  }

  public ReferenceDataType getDocumentDisplayCategory(Integer id) {
    return getItems("DocumentDisplayCategory", converter::toExtendedRefDataType).stream()
        .filter(category -> Objects.equals(category.getId(), id))
        .findFirst()
        .orElseThrow(
            () -> new NotFoundException("document display category not found for id %d", id));
  }

  public List<ExtendedReferenceDataType> getInvoiceStatuses(Set<Integer> ids) {
    return getInvoiceStatuses().stream().filter(status -> ids.contains(status.getId())).toList();
  }

  public List<ExtendedReferenceDataType> getInvoiceStatuses() {
    return getItems("InvoiceStatus", converter::toExtendedRefDataType);
  }

  public List<ExtendedReferenceDataType> getRequestTypes(Set<Integer> ids) {
    return getRequestTypes().stream().filter(status -> ids.contains(status.getId())).toList();
  }

  public List<ExtendedReferenceDataType> getRequestTypes() {
    return getItems("RequestType", converter::toExtendedRefDataType);
  }

  public Map<String, List<ExtendedReferenceDataType>> getAllItems(Boolean active) {
    if (Objects.isNull(active)) {
      return getAllItems(v -> true);
    }
    return getAllItems(v -> v.isActive() == active);
  }

  public ReferenceDataType getAssignmentType(Integer id) {
    return getSpecificReferenceType("AssignmentType", id);
  }

  private Map<String, List<ExtendedReferenceDataType>> getAllItems(
      Predicate<ReferenceDataView> filter) {
    var map = converter.mapByEnumeration(repository.findAll().stream().filter(filter).toList());
    return sorter.sort(map);
  }

  private <T> List<T> getItems(String key, Function<ReferenceDataView, T> conversion) {
    return repository.findAll().stream()
        .filter(view -> Objects.equals(view.getId().getEnumeration(), key))
        .map(conversion)
        .toList();
  }
}

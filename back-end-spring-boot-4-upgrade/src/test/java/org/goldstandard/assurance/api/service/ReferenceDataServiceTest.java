package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.goldstandard.assurance.api.entity.lookup.ExtendedReferenceDataType;
import org.goldstandard.assurance.api.entity.view.ReferenceDataView;
import org.goldstandard.assurance.api.repository.ReferenceDataViewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReferenceDataServiceTest {

  @InjectMocks ReferenceDataService service;

  @Mock ReferenceDataViewRepository repository;

  @Mock ReferenceDataViewConverter converter;

  @Mock ReferenceDataSorter sorter;

  @Test
  void shouldReturnAllRefDataItemsIfActiveFlagIsNull() {
    var view1 = mock(ReferenceDataView.class);
    var view2 = mock(ReferenceDataView.class);
    givenViewsPresent(view1, view2);
    var expectedTypes = givenViewsConvertToTypes(view1, view2);
    when(sorter.sort(expectedTypes)).thenReturn(expectedTypes);

    var result = service.getAllItems(null);

    assertThat(result).isEqualTo(expectedTypes);
  }

  @Test
  void shouldReturnActiveRefDataItemsIfActiveFlagIsTrue() {
    var activeView = givenViewWithActive(true);
    var inactiveView = givenViewWithActive(false);
    givenViewsPresent(activeView, inactiveView);
    var expectedTypes = givenViewsConvertToTypes(activeView);
    when(sorter.sort(expectedTypes)).thenReturn(expectedTypes);

    var result = service.getAllItems(true);

    assertThat(result).isEqualTo(expectedTypes);
  }

  @Test
  void shouldReturnInactiveRefDataItemsIfActiveFlagIsFalse() {
    var activeView = givenViewWithActive(true);
    var inactiveView = givenViewWithActive(false);
    givenViewsPresent(activeView, inactiveView);
    var expectedTypes = givenViewsConvertToTypes(inactiveView);
    when(sorter.sort(expectedTypes)).thenReturn(expectedTypes);

    var result = service.getAllItems(false);

    assertThat(result).isEqualTo(expectedTypes);
  }

  private void givenViewsPresent(ReferenceDataView... views) {
    when(repository.findAll()).thenReturn(List.of(views));
  }

  private Map<String, List<ExtendedReferenceDataType>> givenViewsConvertToTypes(
      ReferenceDataView... views) {
    var types = new HashMap<String, List<ExtendedReferenceDataType>>();
    when(converter.mapByEnumeration(List.of(views))).thenReturn(types);
    return types;
  }

  private static ReferenceDataView givenViewWithActive(boolean active) {
    var view = mock(ReferenceDataView.class);
    when(view.isActive()).thenReturn(active);
    return view;
  }
}

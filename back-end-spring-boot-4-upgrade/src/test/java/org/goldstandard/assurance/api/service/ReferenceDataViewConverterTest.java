package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.goldstandard.assurance.api.entity.lookup.ExtendedReferenceDataType;
import org.goldstandard.assurance.api.entity.view.ReferenceDataView;
import org.goldstandard.assurance.api.entity.view.ReferenceDataViewId;
import org.junit.jupiter.api.Test;

class ReferenceDataViewConverterTest {

  private final ReferenceDataViewConverter converter = new ReferenceDataViewConverter();

  @Test
  void shouldConvertViewsAndMapByEnumeration() {
    var enumeration1 = "enumeration-1";
    var view1 = buildView(buildKey(enumeration1, 1), "code-1", true);
    var enumeration2 = "enumeration-2";
    var view2 = buildView(buildKey(enumeration2, 2), "code-2", false);

    var map = converter.mapByEnumeration(List.of(view1, view2));

    assertThat(map).containsKeys(enumeration1, enumeration2);
    var types1 = map.get(enumeration1);
    assertThat(types1).hasSize(1);
    assertSameValues(types1.getFirst(), view1);
    var types2 = map.get(enumeration2);
    assertThat(types2).hasSize(1);
    assertSameValues(types2.getFirst(), view2);
  }

  private static void assertSameValues(ExtendedReferenceDataType type, ReferenceDataView view) {
    assertThat(type.getId()).isEqualTo(view.getId().getId());
    assertThat(type.getCode()).isEqualTo(view.getCode());
    assertThat(type.getDescription()).isEqualTo(view.getDescription());
    assertThat(type.getActive()).isEqualTo(view.isActive());
  }

  private static ReferenceDataViewId buildKey(String enumeration, Integer id) {
    var key = new ReferenceDataViewId();
    key.setEnumeration(enumeration);
    key.setId(id);
    return key;
  }

  private static ReferenceDataView buildView(ReferenceDataViewId key, String code, boolean active) {
    var view = new ReferenceDataView();
    view.setId(key);
    view.setCode(code);
    view.setDescription(String.format("%s-desc", code));
    view.setActive(active);
    return view;
  }
}

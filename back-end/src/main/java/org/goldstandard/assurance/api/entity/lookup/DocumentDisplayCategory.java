package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class DocumentDisplayCategory extends ReferenceDataType {

  public static final int REQUIRED_DISPLAY_CATEGORY_ID = 1;
  public static final int OTHER_DISPLAY_CATEGORY_ID = 2;
  public static final int SUPPORTING_DISPLAY_CATEGORY_ID = 3;

  public DocumentDisplayCategory(Integer id, String code) {
    this.id = id;
    this.code = code;
  }
}

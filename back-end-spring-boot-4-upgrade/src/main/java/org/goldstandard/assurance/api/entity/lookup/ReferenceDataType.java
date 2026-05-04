package org.goldstandard.assurance.api.entity.lookup;

import static org.goldstandard.assurance.api.util.CustomStringUtils.replaceSpecialCharsForUnderscores;
import static org.goldstandard.assurance.api.util.CustomStringUtils.trimLeadingOrTrailingUnderscores;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder(toBuilder = true)
@MappedSuperclass
@NoArgsConstructor(force = true)
@AllArgsConstructor
@ToString
public class ReferenceDataType implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Integer id;

  protected String code;
  protected String description;

  public ReferenceDataType(Integer id) {
    this.id = id;
  }

  public static ReferenceDataType fromId(Integer id) {
    return ReferenceDataType.builder().id(id).build();
  }

  public static String buildCode(String description) {
    if (Objects.isNull(description)) {
      return "";
    }
    var code = replaceSpecialCharsForUnderscores(description);
    code = trimLeadingOrTrailingUnderscores(code);
    return code.toUpperCase();
  }
}

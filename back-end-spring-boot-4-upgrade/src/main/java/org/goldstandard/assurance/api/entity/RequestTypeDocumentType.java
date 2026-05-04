package org.goldstandard.assurance.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.DocumentDisplayCategory;
import org.goldstandard.assurance.api.entity.lookup.DocumentType;

@Builder
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@Entity
@IdClass(RequestTypeDocumentTypeId.class)
public class RequestTypeDocumentType {

  @Id private final Integer requestTypeId;

  @Id private final Integer documentTypeId;

  private final Integer formTypeId;

  private final Integer documentDisplayCategoryId;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "documentDisplayCategoryId", insertable = false, updatable = false)
  private final DocumentDisplayCategory documentDisplayCategory;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "documentTypeId", insertable = false, updatable = false)
  private final DocumentType documentType;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "formTypeId", insertable = false, updatable = false)
  private final DigitalFormType formType;

  public String getDocumentDisplayCategoryCode() {
    return documentDisplayCategory.getCode();
  }

  public Optional<DigitalFormType> getFormType() {
    return Optional.ofNullable(formType);
  }
}

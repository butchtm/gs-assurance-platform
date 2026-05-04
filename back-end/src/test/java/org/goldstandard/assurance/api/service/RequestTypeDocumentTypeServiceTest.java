package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.entity.lookup.DocumentDisplayCategory.REQUIRED_DISPLAY_CATEGORY_ID;
import static org.goldstandard.assurance.api.entity.lookup.DocumentDisplayCategory.SUPPORTING_DISPLAY_CATEGORY_ID;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.goldstandard.assurance.api.entity.RequestTypeDocumentType;
import org.goldstandard.assurance.api.entity.lookup.DocumentDisplayCategory;
import org.goldstandard.assurance.api.entity.lookup.DocumentType;
import org.goldstandard.assurance.api.repository.RequestTypeDocumentTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestTypeDocumentTypeServiceTest {

  private static final DocumentDisplayCategory REQUIRED_CATEGORY =
      new DocumentDisplayCategory(REQUIRED_DISPLAY_CATEGORY_ID, "REQUIRED");

  @InjectMocks RequestTypeDocumentTypeService service;

  @Mock RequestTypeDocumentTypeRepository requestDocumentTypeRepository;

  @Test
  void shouldGetDocumentTypesForRequestTypeGroupedByDisplayCategoryCode() {
    int requestTypeId = 1;
    var requiredType1 = toDocumentType(11);
    var requiredType2 = toDocumentType(12);
    when(requestDocumentTypeRepository.findAllByRequestTypeIdAndDocumentDisplayCategoryId(
            requestTypeId, REQUIRED_CATEGORY.getId()))
        .thenReturn(
            List.of(toRequiredDocumentType(requiredType1), toRequiredDocumentType(requiredType2)));

    var documentTypes = service.getDocumentTypesForRequestType(requestTypeId);

    assertThat(documentTypes).hasSize(1);
    assertThat(documentTypes.get(REQUIRED_CATEGORY.getCode()))
        .map(RequestTypeDocumentType::getDocumentTypeId)
        .containsExactly(requiredType1.getId(), requiredType2.getId());
  }

  @Test
  void shouldGetDocumentDisplayCategoryTypeIdIfFound() {
    int requestType = 2;
    int documentType = 3;
    var type = toRequestTypeDocumentType(REQUIRED_CATEGORY);
    when(requestDocumentTypeRepository.findByRequestTypeIdAndDocumentTypeId(
            requestType, documentType))
        .thenReturn(Optional.of(type));

    var categoryTypeId = service.findDocumentDisplayCategoryId(requestType, documentType);

    assertThat(categoryTypeId).isEqualTo(type.getDocumentDisplayCategoryId());
  }

  @Test
  void shouldGetDocumentDisplayCategoryTypeSupportingIfNotFoundAndNotOther() {
    int requestType = 5;
    int documentType = 6;
    when(requestDocumentTypeRepository.findByRequestTypeIdAndDocumentTypeId(
            requestType, documentType))
        .thenReturn(Optional.empty());

    var categoryTypeId = service.findDocumentDisplayCategoryId(requestType, documentType);

    assertThat(categoryTypeId).isEqualTo(SUPPORTING_DISPLAY_CATEGORY_ID);
  }

  private static RequestTypeDocumentType toRequiredDocumentType(DocumentType documentType) {
    return toRequestTypeDocumentType(REQUIRED_CATEGORY, documentType);
  }

  private static RequestTypeDocumentType toRequestTypeDocumentType(
      DocumentDisplayCategory category) {
    return toRequestTypeDocumentType(category, toDocumentType(99));
  }

  private static DocumentType toDocumentType(Integer id) {
    return new DocumentType(id);
  }

  private static RequestTypeDocumentType toRequestTypeDocumentType(
      DocumentDisplayCategory category, DocumentType documentType) {
    return RequestTypeDocumentType.builder()
        .documentDisplayCategoryId(category.getId())
        .documentDisplayCategory(category)
        .documentTypeId(documentType.getId())
        .documentType(documentType)
        .build();
  }
}

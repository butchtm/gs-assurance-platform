package org.goldstandard.assurance.api.service.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;
import org.goldstandard.assurance.api.service.ReferenceDataService;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentsZipFilenameFactoryTest {

  @InjectMocks DocumentsZipFilenameFactory filenameFactory;

  @Mock RequestFinder requestFinder;

  @Mock ReferenceDataService referenceDataService;

  @Test
  void shouldBuildDocumentsZipFilename() {
    var requestId = 1;
    givenRequestFoundForId(requestId);
    var displayCategoryId = 11;
    givenDisplayCategoryFoundForId(displayCategoryId);

    var filename = filenameFactory.toFilename(requestId, displayCategoryId);

    assertThat(filename)
        .isEqualTo("GS1234-Design-Certification-Renewal-Project-no-1-REQUIRED-documents.zip");
  }

  private void givenRequestFoundForId(int id) {
    when(requestFinder.getRequestById(id)).thenReturn(buildRequest(id));
  }

  private void givenDisplayCategoryFoundForId(int id) {
    when(referenceDataService.getDocumentDisplayCategory(id)).thenReturn(buildDisplayCategory(id));
  }

  private static Request buildRequest(int id) {
    return Request.builder()
        .id(id)
        .requestName("Design Certification Renewal | Project no. 1")
        .project(buildProject())
        .build();
  }

  private static Project buildProject() {
    return Project.builder().gsid("GS1234").build();
  }

  private static ReferenceDataType buildDisplayCategory(int id) {
    var category = new ReferenceDataType();
    category.setId(id);
    category.setDescription("REQUIRED");
    return category;
  }
}

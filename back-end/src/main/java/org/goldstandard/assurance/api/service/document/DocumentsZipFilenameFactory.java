package org.goldstandard.assurance.api.service.document;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;
import org.goldstandard.assurance.api.service.ReferenceDataService;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DocumentsZipFilenameFactory {

  private final RequestFinder finder;
  private final ReferenceDataService referenceDataService;

  public String toFilename(Integer requestId, Integer displayCategoryId) {
    var request = finder.getRequestById(requestId);
    var displayCategory = referenceDataService.getDocumentDisplayCategory(displayCategoryId);
    return toFilename(request, displayCategory);
  }

  private static String toFilename(Request request, ReferenceDataType displayCategory) {
    var project = request.getProject();
    var requestName = removeFullStops(request.getRequestName());
    var categoryName = displayCategory.getDescription();
    var name =
        String.format("%s-%s-%s-documents.zip", project.getGsid(), requestName, categoryName);
    return replaceSpaces(removePipes(name));
  }

  private static String removePipes(String s) {
    return s.replace(" | ", "-");
  }

  private static String replaceSpaces(String s) {
    return s.replace(" ", "-");
  }

  private static String removeFullStops(String s) {
    return s.replace(".", "");
  }
}

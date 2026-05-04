package org.goldstandard.assurance.api.service.document.pii.analysis;

import java.util.Collection;
import org.goldstandard.assurance.api.dto.ProbablePii;
import org.goldstandard.assurance.api.dto.comprehend.ComprehendFileOutput;
import org.goldstandard.assurance.api.service.pii.ProbablePiiFactory;
import org.goldstandard.assurance.api.util.DocumentUtils;
import org.springframework.stereotype.Component;

@Component
public class TextractComprehendResultsParser implements ComprehendResultsParser {

  @Override
  public Collection<ProbablePii> parse(Collection<ComprehendFileOutput> comprehendOutput) {
    return comprehendOutput.stream().map(this::parseOutput).flatMap(Collection::stream).toList();
  }

  private Collection<ProbablePii> parseOutput(ComprehendFileOutput comprehendOutput) {
    var pageNumber = getPageNumberFromOutputFile(comprehendOutput.getFileName());
    return comprehendOutput.getEntities().stream()
        .map(entity -> ProbablePiiFactory.buildDetectedPii(pageNumber, entity))
        .toList();
  }

  private Integer getPageNumberFromOutputFile(String fileName) {
    return Integer.parseInt(fileName.split("\\.")[0]);
  }

  @Override
  public boolean supported(String contentType) {
    return DocumentUtils.isTextractScannable(contentType) || DocumentUtils.isWordDoc(contentType);
  }
}

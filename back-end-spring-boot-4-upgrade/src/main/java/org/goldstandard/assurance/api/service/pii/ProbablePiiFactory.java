package org.goldstandard.assurance.api.service.pii;

import static org.goldstandard.assurance.api.util.PiiProbabilityCalculator.toProbability;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.dto.ExtractedText;
import org.goldstandard.assurance.api.dto.FieldTextAnalysisDTO;
import org.goldstandard.assurance.api.dto.ProbablePii;
import software.amazon.awssdk.services.comprehend.model.PiiEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProbablePiiFactory {

  public static ProbablePii buildDetectedPii(FieldTextAnalysisDTO fieldText, PiiEntity piiEntity) {
    return buildDetectedPii(piiEntity)
        .identifiedString(toText(fieldText.getText(), piiEntity))
        .field(fieldText.getField())
        .build();
  }

  public static ProbablePii buildDetectedPii(ExtractedText extractedText, PiiEntity piiEntity) {
    return buildDetectedPii(piiEntity)
        .pageNumber(extractedText.getPageNumber())
        .location(getLocationDescription(extractedText, piiEntity))
        .build();
  }

  public static ProbablePii buildDetectedPii(Integer pageNumber, PiiEntity piiEntity) {
    return buildDetectedPii(piiEntity).pageNumber(pageNumber).build();
  }

  private static ProbablePii.ProbablePiiBuilder buildDetectedPii(PiiEntity piiEntity) {
    return ProbablePii.builder()
        .entity(piiEntity.typeAsString())
        .probability(toProbability(piiEntity.score()));
  }

  private static String toText(String text, PiiEntity piiEntity) {
    return text.substring(piiEntity.beginOffset(), piiEntity.endOffset());
  }

  private static String getLocationDescription(ExtractedText extractedText, PiiEntity piiEntity) {
    return extractedText
        .getSheetLocations()
        .getLocation(piiEntity.beginOffset(), piiEntity.endOffset());
  }
}

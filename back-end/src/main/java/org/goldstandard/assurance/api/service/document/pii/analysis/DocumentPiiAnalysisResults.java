package org.goldstandard.assurance.api.service.document.pii.analysis;

import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import org.goldstandard.assurance.api.dto.ProbablePii;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.User;

@Getter
@Builder
public class DocumentPiiAnalysisResults {
  private final Document document;
  private final Request request;
  private final Project project;
  private final User user;
  private final Collection<ProbablePii> probablePii;
}

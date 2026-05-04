package org.goldstandard.assurance.api.service.document.pii.analysis;

import java.util.Collection;
import org.goldstandard.assurance.api.dto.ProbablePii;

public interface DocumentPiiAnalyser {

  Collection<ProbablePii> analyse(DocumentPiiAnalysisParams params);

  AnalysisStartResult startComprehendAnalysis(DocumentPiiAnalysisParams params);

  boolean supported(DocumentPiiScanCriteria criteria);

  boolean supportsAsyncComprehend();
}

package org.goldstandard.assurance.api.service.document.pii.analysis;

import java.util.Collection;
import org.goldstandard.assurance.api.dto.ProbablePii;
import org.goldstandard.assurance.api.dto.comprehend.ComprehendFileOutput;

public interface ComprehendResultsParser {

  Collection<ProbablePii> parse(Collection<ComprehendFileOutput> comprehendOutput);

  boolean supported(String contentType);
}

package org.goldstandard.assurance.api.service.document.conversion;

import java.io.InputStream;
import org.goldstandard.assurance.api.service.document.ConvertibleOutputStream;
import org.jodconverter.core.office.OfficeException;

public interface DocumentConversionService {
  ConvertibleOutputStream wordToPdf(InputStream wordInput, String contentType)
      throws OfficeException;
}

package org.goldstandard.assurance.api.service.document.conversion;

import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.service.document.ConvertibleOutputStream;
import org.goldstandard.assurance.api.util.DocumentUtils;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.document.DocumentFormat;
import org.jodconverter.core.office.OfficeException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Profile("!local && !test")
public class DocumentConversionServiceDefault implements DocumentConversionService {

  private final DocumentConverter converter;

  public ConvertibleOutputStream wordToPdf(InputStream wordInput, String contentType)
      throws OfficeException {
    var pdfOutput = new ConvertibleOutputStream();

    converter
        .convert(wordInput)
        .as(getInputFormatByContentType(contentType))
        .to(pdfOutput)
        .as(DefaultDocumentFormatRegistry.PDF)
        .execute();

    return pdfOutput;
  }

  private static DocumentFormat getInputFormatByContentType(String contentType) {
    return DocumentUtils.WORD_DOCX.equals(contentType)
        ? DefaultDocumentFormatRegistry.DOCX
        : DefaultDocumentFormatRegistry.DOC;
  }
}

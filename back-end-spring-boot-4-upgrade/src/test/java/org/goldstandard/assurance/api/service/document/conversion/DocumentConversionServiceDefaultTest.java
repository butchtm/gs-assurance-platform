package org.goldstandard.assurance.api.service.document.conversion;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import org.goldstandard.assurance.api.service.document.ConvertibleOutputStream;
import org.goldstandard.assurance.api.util.DocumentUtils;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.job.ConversionJob;
import org.jodconverter.core.job.ConversionJobWithOptionalSourceFormatUnspecified;
import org.jodconverter.core.job.ConversionJobWithRequiredTargetFormatUnspecified;
import org.jodconverter.core.job.ConversionJobWithSourceSpecified;
import org.jodconverter.core.office.OfficeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentConversionServiceDefaultTest {

  @Mock DocumentConverter converter;
  @InjectMocks private DocumentConversionServiceDefault conversionService;

  @Test
  void shouldConvertWordToPdf() throws OfficeException {
    var wordInput = mock(ByteArrayInputStream.class);
    var conversionJobWithWordInput = mock(ConversionJobWithOptionalSourceFormatUnspecified.class);
    var conversionJobWithWordFormatSpecified = mock(ConversionJobWithSourceSpecified.class);
    var conversionJobWithPdfOutput = mock(ConversionJobWithRequiredTargetFormatUnspecified.class);
    var fullConversionJob = mock(ConversionJob.class);

    when(converter.convert(wordInput)).thenReturn(conversionJobWithWordInput);
    when(conversionJobWithWordInput.as(DefaultDocumentFormatRegistry.DOCX))
        .thenReturn(conversionJobWithWordFormatSpecified);
    when(conversionJobWithWordFormatSpecified.to(any(ConvertibleOutputStream.class)))
        .thenReturn(conversionJobWithPdfOutput);
    when(conversionJobWithPdfOutput.as(DefaultDocumentFormatRegistry.PDF))
        .thenReturn(fullConversionJob);

    conversionService.wordToPdf(wordInput, DocumentUtils.WORD_DOCX);

    verify(converter).convert(wordInput);
    verify(conversionJobWithWordInput).as(DefaultDocumentFormatRegistry.DOCX);
    verify(conversionJobWithWordFormatSpecified).to(any(ConvertibleOutputStream.class));
    verify(conversionJobWithPdfOutput).as(DefaultDocumentFormatRegistry.PDF);
    verify(fullConversionJob).execute();
  }

  @Test
  void shouldConvertLegacyWordToPdf() throws OfficeException {
    var wordInput = mock(ByteArrayInputStream.class);
    var conversionJobWithWordInput = mock(ConversionJobWithOptionalSourceFormatUnspecified.class);
    var conversionJobWithWordFormatSpecified = mock(ConversionJobWithSourceSpecified.class);
    var conversionJobWithPdfOutput = mock(ConversionJobWithRequiredTargetFormatUnspecified.class);
    var fullConversionJob = mock(ConversionJob.class);

    when(converter.convert(wordInput)).thenReturn(conversionJobWithWordInput);
    when(conversionJobWithWordInput.as(DefaultDocumentFormatRegistry.DOC))
        .thenReturn(conversionJobWithWordFormatSpecified);
    when(conversionJobWithWordFormatSpecified.to(any(ConvertibleOutputStream.class)))
        .thenReturn(conversionJobWithPdfOutput);
    when(conversionJobWithPdfOutput.as(DefaultDocumentFormatRegistry.PDF))
        .thenReturn(fullConversionJob);

    conversionService.wordToPdf(wordInput, DocumentUtils.WORD_DOC);

    verify(converter).convert(wordInput);
    verify(conversionJobWithWordInput).as(DefaultDocumentFormatRegistry.DOC);
    verify(conversionJobWithWordFormatSpecified).to(any(ConvertibleOutputStream.class));
    verify(conversionJobWithPdfOutput).as(DefaultDocumentFormatRegistry.PDF);
    verify(fullConversionJob).execute();
  }
}

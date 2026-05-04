package org.goldstandard.assurance.api.service.document.conversion;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.service.document.ConvertibleOutputStream;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("local || test")
public class DocumentConversionServiceStubbed implements DocumentConversionService {

  @Override
  public ConvertibleOutputStream wordToPdf(InputStream wordInput, String contentType) {
    log.info("Stubbed wordToPdf conversion.");
    var output = new ConvertibleOutputStream();
    try {
      output.write("Stubbed wordToPdf conversion.".getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      log.error("Error stubbing wordToPdf conversion", e);
    }
    return output;
  }
}

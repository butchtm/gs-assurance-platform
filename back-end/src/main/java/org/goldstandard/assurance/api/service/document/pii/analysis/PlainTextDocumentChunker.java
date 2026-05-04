package org.goldstandard.assurance.api.service.document.pii.analysis;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.ExtractedText;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PlainTextDocumentChunker {

  @Value("${comprehend.detectEntitiesCharLimit:90000}")
  private int comprehendLimit;

  public List<ExtractedText> chunkTextIntoPages(String textContent) {
    if (textContent.length() <= comprehendLimit) {
      return List.of(ExtractedText.builder().pageNumber(null).text(textContent).build());
    }

    var chunks = new java.util.ArrayList<ExtractedText>();

    for (int start = 0; start < textContent.length(); start += comprehendLimit) {
      var end = Math.min(start + comprehendLimit, textContent.length());
      var chunk = textContent.substring(start, end);

      chunks.add(ExtractedText.builder().pageNumber(null).text(chunk).build());
    }

    log.info("Text document chunked into {} parts for Comprehend analysis", chunks.size());
    return chunks;
  }
}

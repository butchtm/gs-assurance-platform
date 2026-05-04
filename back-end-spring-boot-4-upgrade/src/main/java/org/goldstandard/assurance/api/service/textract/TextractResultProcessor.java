package org.goldstandard.assurance.api.service.textract;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.ExtractedText;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.Block;
import software.amazon.awssdk.services.textract.model.BlockType;
import software.amazon.awssdk.services.textract.model.GetDocumentTextDetectionResponse;

@RequiredArgsConstructor
@Component
@Profile("!local && !test")
public class TextractResultProcessor {

  private final TextractClient textractClient;
  private final TextractServiceRequestFactory requestFactory;

  public Stream<List<ExtractedText>> streamByResponseToken(String jobId) {
    Iterator<List<ExtractedText>> iterator =
        new Iterator<>() {
          private String nextToken = null;
          private boolean hasMore = true;

          @Override
          public boolean hasNext() {
            return hasMore;
          }

          @Override
          public List<ExtractedText> next() {
            if (!hasMore) {
              throw new NoSuchElementException("No more pages from Textract");
            }
            var response = doGetTextRequest(jobId, nextToken);
            nextToken = response.nextToken();
            hasMore = nextToken != null;
            return extractFromResponse(response);
          }
        };
    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
  }

  private GetDocumentTextDetectionResponse doGetTextRequest(String jobId, String nextToken) {
    return textractClient.getDocumentTextDetection(
        requestFactory.getDetectionRequestWithToken(jobId, nextToken));
  }

  private List<ExtractedText> extractFromResponse(GetDocumentTextDetectionResponse response) {
    return response.blocks().stream()
        .filter(block -> block.blockType() == BlockType.LINE)
        .map(this::toExtractedText)
        .toList();
  }

  private ExtractedText toExtractedText(Block block) {
    return ExtractedText.builder().text(block.text()).pageNumber(block.page()).build();
  }
}

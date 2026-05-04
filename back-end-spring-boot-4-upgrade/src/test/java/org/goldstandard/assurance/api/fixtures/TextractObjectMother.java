package org.goldstandard.assurance.api.fixtures;

import java.util.Collections;
import java.util.List;
import org.goldstandard.assurance.api.dto.ExtractedText;
import software.amazon.awssdk.services.textract.model.Block;
import software.amazon.awssdk.services.textract.model.BlockType;
import software.amazon.awssdk.services.textract.model.GetDocumentTextDetectionResponse;
import software.amazon.awssdk.services.textract.model.JobStatus;
import software.amazon.awssdk.services.textract.model.StartDocumentTextDetectionResponse;

public class TextractObjectMother {

  public static StartDocumentTextDetectionResponse startDetectionRequest(String jobId) {
    return StartDocumentTextDetectionResponse.builder().jobId(jobId).build();
  }

  public static GetDocumentTextDetectionResponse successfulDetectionResponse() {
    return GetDocumentTextDetectionResponse.builder().jobStatus(JobStatus.SUCCEEDED).build();
  }

  public static GetDocumentTextDetectionResponse inProgressDetectionResponse() {
    return GetDocumentTextDetectionResponse.builder().jobStatus(JobStatus.IN_PROGRESS).build();
  }

  public static GetDocumentTextDetectionResponse failedDetectionResponse() {
    return GetDocumentTextDetectionResponse.builder().jobStatus(JobStatus.FAILED).build();
  }

  public static GetDocumentTextDetectionResponse detectionResponseWithBlocks() {
    return GetDocumentTextDetectionResponse.builder().blocks(blocks()).build();
  }

  public static List<ExtractedText> resultTexts() {
    return Collections.singletonList(ExtractedText.builder().text("test").pageNumber(1).build());
  }

  public static GetDocumentTextDetectionResponse detectionResponseWithBlocksAndNextToken() {
    return GetDocumentTextDetectionResponse.builder().blocks(blocks()).nextToken("token").build();
  }

  private static List<Block> blocks() {
    return List.of(block("block1"), block("block2"));
  }

  private static Block block(String text) {
    return Block.builder().blockType(BlockType.LINE).text(text).build();
  }
}

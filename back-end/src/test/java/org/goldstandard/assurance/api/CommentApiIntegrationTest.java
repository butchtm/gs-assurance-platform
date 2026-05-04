package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import org.goldstandard.assurance.api.dto.CommentCreateDTO;
import org.goldstandard.assurance.api.dto.ProbablePii;
import org.goldstandard.assurance.api.dto.TextAnalysisResultsDTO;
import org.goldstandard.assurance.api.enumeration.PiiProbability;
import org.goldstandard.assurance.api.repository.CommentRepository;
import org.goldstandard.assurance.api.service.notification.RenderedMessage;
import org.goldstandard.assurance.api.service.pub.RateLimitResetter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class CommentApiIntegrationTest extends AbstractApiTest {

  @Autowired CommentRepository commentRepository;

  @Autowired ApiClient client;

  @Autowired RateLimitResetter rateLimitResetter;

  @BeforeEach
  void setUp() {
    rateLimitResetter.resetAll();
  }

  @AfterEach
  void tearDown() {
    rateLimitResetter.resetAll();
  }

  @Test
  void shouldReturnPiiMetadataAgainstComment() {
    when(emailSender.sendEmailAsync(anyString(), any(RenderedMessage.class)))
        .thenReturn(CompletableFuture.completedFuture("mocked-message-reference"));
    var requestId = 1;
    var commentId = givenCommentWithPiiExistsId(requestId);

    try {
      var response = client.getRequestComments(requestId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      var body = response.getBody();
      assertThat(body).isNotNull().hasSize(2);
      var comment =
          body.stream().filter(c -> c.getId().equals(commentId)).findFirst().orElseThrow();
      assertThat(comment).isNotNull();
      var metadata = comment.getPiiMetadata();
      assertThat(metadata).isNotNull();
      assertThat(metadata.getAction().isNew()).isTrue();
    } finally {
      commentRepository.deleteById(commentId);
    }
  }

  private Integer givenCommentWithPiiExistsId(Integer requestId) {
    var dto = buildCommentWithPii(requestId);
    var createResponse = client.publicCreateComment(dto);
    assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    return Objects.requireNonNull(createResponse.getBody());
  }

  private static CommentCreateDTO buildCommentWithPii(Integer requestId) {
    return CommentCreateDTO.builder()
        .comment("test comment with stubbed-pii")
        .usersEmail("testEmail")
        .usersName("testName")
        .requestId(requestId)
        .anonymous(false)
        .textAnalysisResults(buildTextAnalysisResults())
        .build();
  }

  private static TextAnalysisResultsDTO buildTextAnalysisResults() {
    return TextAnalysisResultsDTO.builder().probablePii(buildProbablePii()).build();
  }

  private static Collection<ProbablePii> buildProbablePii() {
    return List.of(
        ProbablePii.builder()
            .entity("STUBBED_ENTITY")
            .probability(PiiProbability.HIGH)
            .identifiedString("stubbed-pii")
            .field("comment")
            .build());
  }
}

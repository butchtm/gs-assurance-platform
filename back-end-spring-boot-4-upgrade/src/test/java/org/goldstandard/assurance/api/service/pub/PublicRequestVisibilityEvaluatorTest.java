package org.goldstandard.assurance.api.service.pub;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import org.goldstandard.assurance.api.entity.Comment;
import org.goldstandard.assurance.api.entity.view.PublicDocumentView;
import org.goldstandard.assurance.api.entity.view.PublicRequestsView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublicRequestVisibilityEvaluatorTest {
  private PublicRequestVisibilityEvaluator visibilityEvaluator;

  @BeforeEach
  void setup() {
    visibilityEvaluator = new PublicRequestVisibilityEvaluator();
  }

  @Test
  void shouldAllowRequestsThatHaveStakeholderConsultationDates() {
    var consultationStart = ZonedDateTime.of(2025, 10, 20, 0, 0, 0, 0, ZoneId.of("Europe/London"));
    var consultationEnd = consultationStart.plusDays(5);
    var request =
        PublicRequestsView.builder()
            .consultationStartDate(consultationStart)
            .consultationEndDate(consultationEnd)
            .build();

    var result =
        visibilityEvaluator.shouldShowRequestOnPublicPage(
            request, Collections.emptyList(), Collections.emptyList());

    assertThat(result).isTrue();
  }

  @Test
  void shouldAllowRequestsThatHavePublicDocuments() {
    var request = PublicRequestsView.builder().build();
    var documents = List.of(new PublicDocumentView());

    var result =
        visibilityEvaluator.shouldShowRequestOnPublicPage(
            request, documents, Collections.emptyList());

    assertThat(result).isTrue();
  }

  @Test
  void shouldAllowRequestsThatHavePublicComments() {
    var request = PublicRequestsView.builder().build();
    var comments = List.of(new Comment());

    var result =
        visibilityEvaluator.shouldShowRequestOnPublicPage(
            request, Collections.emptyList(), comments);

    assertThat(result).isTrue();
  }

  @Test
  void shouldNotAllowRequestsThatHaveNoStakeholderConsultationOrDocumentsOrComments() {
    var request = PublicRequestsView.builder().build();

    var result =
        visibilityEvaluator.shouldShowRequestOnPublicPage(
            request, Collections.emptyList(), Collections.emptyList());

    assertThat(result).isFalse();
  }
}

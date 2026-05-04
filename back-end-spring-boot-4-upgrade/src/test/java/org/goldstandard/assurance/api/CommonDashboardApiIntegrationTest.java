package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.FINANCE_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REGISTRY_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REVIEWER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.TAC_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.VVB_ROLE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.awaitility.Awaitility;
import org.goldstandard.assurance.api.dto.CommonDashboardRequestDTO;
import org.goldstandard.assurance.api.service.document.ConvertibleOutputStream;
import org.goldstandard.assurance.api.service.document.PresignedUrlResult;
import org.goldstandard.assurance.api.service.notification.EmailSender;
import org.goldstandard.assurance.api.service.notification.RenderedMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class CommonDashboardApiIntegrationTest extends AbstractApiTest {

  @Autowired ApiClient client;

  @ParameterizedTest
  @ValueSource(
      strings = {
        PROJECT_DEVELOPER_ROLE,
        TAC_ROLE,
        GS_REVIEWER_ROLE,
        VVB_ROLE,
        FINANCE_ROLE,
        GS_REGISTRY_ROLE
      })
  void shouldReturnForbiddenForNonPermittedUsers(String role) {
    var response = client.getCommonDashboardRequests(role);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(response.getBody()).isNull();
  }

  @ParameterizedTest
  @ValueSource(strings = {ADMIN_ROLE, ARM_TEAM_ROLE})
  void shouldBeAccessibleByPermittedUsers(String role) {
    var response = client.getCommonDashboardRequests(role);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void shouldSortByRequestStartDateDescendingByDefault() {
    var response = client.getCommonDashboardRequests(ADMIN_ROLE);

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent())
        .map(CommonDashboardRequestDTO::getStartDate)
        .isSortedAccordingTo(Comparator.reverseOrder());
  }

  @Test
  void shouldSortByIdAscending() {
    var query = "sort=requestId,asc";

    var response = client.getCommonDashboardRequestsWithQuery(ADMIN_ROLE, query);

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent())
        .map(CommonDashboardRequestDTO::getId)
        .containsExactly(1, 2, 3, 4, 5, 6);
  }

  @Test
  void shouldEmailCreatedReportToUser() {
    var response = client.exportCommonDashboardReport("");
    var expiry = ZonedDateTime.now();
    var result =
        PresignedUrlResult.builder()
            .key("mocked-key")
            .url("http://mocked-host/file.xslx")
            .checksum("mocked-checksum")
            .expiry(expiry)
            .build();
    when(downloadFileStore.generatePresignedUrl(
            anyString(), any(ConvertibleOutputStream.class), any(ZonedDateTime.class)))
        .thenReturn(result);
    when(emailSender.sendEmailAsync(anyString(), any(RenderedMessage.class)))
        .thenReturn(CompletableFuture.completedFuture("mocked-message-reference"));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    var emailSent = new EmailSentCallable(emailSender, "arm@test");
    Awaitility.await().atMost(Duration.ofMillis(7000)).ignoreExceptions().until(emailSent);
    var message = emailSent.getCapturedMessage();
    assertThat(message.subject()).isEqualTo("GS Assurance Platform Report Available");
    assertThat(message.textBody())
        .contains("Dear Test ARM,")
        .contains(result.getKey())
        .contains(result.getUrl())
        .contains(
            String.format("expires on %s", expiry.format(DateTimeFormatter.ofPattern("dd MMMM"))))
        .contains(String.format("at %s", expiry.format(DateTimeFormatter.ofPattern("HH:mm z"))));
  }

  @RequiredArgsConstructor
  private static class EmailSentCallable implements Callable<Boolean> {

    private final EmailSender emailSender;
    private final String expectedEmail;
    private final ArgumentCaptor<RenderedMessage> messageCaptor;

    public EmailSentCallable(EmailSender emailSender, String expectedEmail) {
      this(emailSender, expectedEmail, ArgumentCaptor.forClass(RenderedMessage.class));
    }

    @Override
    public Boolean call() {
      verify(emailSender).sendEmailAsync(eq(expectedEmail), messageCaptor.capture());
      return true;
    }

    public RenderedMessage getCapturedMessage() {
      return messageCaptor.getValue();
    }
  }
}

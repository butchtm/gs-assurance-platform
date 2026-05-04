package org.goldstandard.assurance.api.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.goldstandard.assurance.api.configuration.RendererConfiguration;
import org.goldstandard.assurance.api.entity.notification.NotificationTemplate;
import org.goldstandard.assurance.api.exception.RendererException;
import org.goldstandard.assurance.api.fixtures.NotificationTemplateMother;
import org.goldstandard.assurance.api.fixtures.TemplateKeyMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RendererServiceTest {

  private static final TemplateKey TEMPLATE_KEY = TemplateKeyMother.build("renderer-service");
  private static final String SUBJECT = "A subject";
  private static final String CONTENT = "Some content";
  private static final Map<String, Object> MODEL =
      Map.of(
          "subject", SUBJECT,
          "content", CONTENT);
  private static final String EXPECTED_SUBJECT = "Subject A subject";
  private static final String EXPECTED_HTML_BODY = "HTML Body Some content";
  private static final String EXPECTED_TEXT_BODY = "Text Body Some content";

  @Mock private TemplateService templateService;

  @Mock private RendererConfiguration rendererConfiguration;

  @InjectMocks private RendererService rendererService;

  @Getter
  @AllArgsConstructor
  public enum PartsTestCase {
    FULL_TEMPLATE(NotificationTemplateMother.build(TEMPLATE_KEY)),
    NO_SUBJECT_TEMPLATE(NotificationTemplateMother.withoutSubject(TEMPLATE_KEY)),
    NO_HTML_BODY_TEMPLATE(NotificationTemplateMother.withoutHtmlBody(TEMPLATE_KEY)),
    NO_TEXT_BODY_TEMPLATE(NotificationTemplateMother.withoutTextBody(TEMPLATE_KEY));

    private final NotificationTemplate notificationTemplate;
  }

  @ParameterizedTest
  @EnumSource(PartsTestCase.class)
  void testRenderMessagePartsFromATemplate(PartsTestCase testCase) {
    when(templateService.getNotificationTemplate(TEMPLATE_KEY))
        .thenReturn(Optional.of(testCase.getNotificationTemplate()));

    final RenderedMessage message = rendererService.renderMessage(TEMPLATE_KEY, MODEL);

    assertThat(message).isNotNull();
    assertThat(message.subject())
        .isEqualTo(
            testCase.getNotificationTemplate().getSubject() != null ? EXPECTED_SUBJECT : null);
    assertThat(message.htmlBody())
        .isEqualTo(
            testCase.getNotificationTemplate().getHtmlBody() != null ? EXPECTED_HTML_BODY : null);
    assertThat(message.textBody())
        .isEqualTo(
            testCase.getNotificationTemplate().getTextBody() != null ? EXPECTED_TEXT_BODY : null);
  }

  @Test
  void testRenderMessageErrorsOnNonExistentTemplate() {
    when(templateService.getNotificationTemplate(any())).thenReturn(Optional.empty());
    final RendererException exception =
        assertThrows(
            RendererException.class, () -> rendererService.renderMessage(TEMPLATE_KEY, MODEL));
    assertThat(exception.getMessage())
        .isEqualTo(String.format("Notification template %s not found", TEMPLATE_KEY.fullName()));
  }

  @Test
  void testRenderMessageErrorsOnInvalidTemplate() {
    NotificationTemplate invalidTemplate =
        NotificationTemplateMother.builder(TEMPLATE_KEY).subject("${invalid template").build();
    when(templateService.getNotificationTemplate(any())).thenReturn(Optional.of(invalidTemplate));
    final RendererException exception =
        assertThrows(
            RendererException.class, () -> rendererService.renderMessage(TEMPLATE_KEY, MODEL));
    assertThat(exception.getMessage())
        .startsWith(String.format("Error rendering %s", TEMPLATE_KEY.fullName()));
  }

  @Test
  void testRenderMessageErrorsOnMissingModelObjects() {
    NotificationTemplate missingAttributeTemplate =
        NotificationTemplateMother.builder(TEMPLATE_KEY).subject("${missing_attribute}").build();
    when(templateService.getNotificationTemplate(any()))
        .thenReturn(Optional.of(missingAttributeTemplate));
    final RendererException exception =
        assertThrows(
            RendererException.class, () -> rendererService.renderMessage(TEMPLATE_KEY, MODEL));
    assertThat(exception.getMessage())
        .startsWith(String.format("Error rendering %s", TEMPLATE_KEY.fullName()));
  }
}

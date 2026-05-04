package org.goldstandard.assurance.api.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.goldstandard.assurance.api.entity.notification.NotificationTemplate;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.fixtures.NotificationTemplateMother;
import org.goldstandard.assurance.api.fixtures.TemplateKeyMother;
import org.goldstandard.assurance.api.repository.notification.TemplateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TemplateServiceTest {

  private static final TemplateKey KEY = TemplateKeyMother.build("template-service");
  private static final String NAME = KEY.name();
  private static final String VERSION = KEY.version();

  @Mock private TemplateRepository templateRepository;

  @InjectMocks private TemplateService templateService;

  @Test
  void shouldReturnNotificationTemplateIfFound() {
    NotificationTemplate expectedTemplate = NotificationTemplateMother.build(KEY);
    when(templateRepository.findByNameAndVersion(NAME, VERSION))
        .thenReturn(Optional.of(expectedTemplate));

    Optional<NotificationTemplate> result = templateService.getNotificationTemplate(KEY);

    assertThat(result).contains(expectedTemplate);
  }

  @Test
  void shouldReturnEmptyOptionalIfNotificationTemplateNotFound() {
    when(templateRepository.findByNameAndVersion(NAME, VERSION)).thenReturn(Optional.empty());

    Optional<NotificationTemplate> template = templateService.getNotificationTemplate(KEY);

    assertThat(template).isEmpty();
  }

  @Test
  void shouldThrowExceptionIfNotificationTemplateNotFound() {
    when(templateRepository.findByNameAndVersion(NAME, VERSION)).thenReturn(Optional.empty());

    Throwable error = catchThrowable(() -> templateService.forceGetTemplate(KEY));

    assertThat(error).isInstanceOf(NotFoundException.class);
  }
}

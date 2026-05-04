package org.goldstandard.assurance.api.service.notification.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.goldstandard.assurance.api.service.notification.NotificationRecipient;
import org.junit.jupiter.api.Test;

class NotificationParamsTest {

  @Test
  void shouldReturnRecipientAddressOverrideIfSet() {
    var addressOverride = "override@email.com";
    var params =
        NotificationParams.builder()
            .recipient(givenRecipient())
            .recipientAddressOverride(addressOverride)
            .build();

    var address = params.getRecipientAddress();

    assertThat(address).isEqualTo(addressOverride);
  }

  @Test
  void shouldReturnRecipientAddressIfOverrideAddressIsEmpty() {
    var recipient = givenRecipient();
    var params =
        NotificationParams.builder().recipient(recipient).recipientAddressOverride("").build();

    var address = params.getRecipientAddress();

    assertThat(address).isEqualTo(recipient.getEmailAddress());
  }

  @Test
  void shouldReturnRecipientAddressIfOverrideAddressIsNull() {
    var recipient = givenRecipient();
    var params =
        NotificationParams.builder().recipient(recipient).recipientAddressOverride(null).build();

    var address = params.getRecipientAddress();

    assertThat(address).isEqualTo(recipient.getEmailAddress());
  }

  private NotificationRecipient givenRecipient() {
    var recipient = mock(NotificationRecipient.class);
    when(recipient.getEmailAddress()).thenReturn("original@email.com");
    return recipient;
  }
}

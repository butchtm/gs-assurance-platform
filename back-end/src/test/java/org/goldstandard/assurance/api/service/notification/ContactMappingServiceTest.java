package org.goldstandard.assurance.api.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.goldstandard.assurance.api.configuration.NotificationConfiguration;
import org.goldstandard.assurance.api.repository.notification.NotificationContactMappingRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class ContactMappingServiceTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(ContactMappingServiceTest.class);

  private static final List<String> TEST_MAPPING = List.of("mapped");
  private static final List<String> TEST_NO_MAPPING = List.of();

  @Mock private NotificationContactMappingRepository mappingRepository;

  @Mock private NotificationConfiguration notificationConfiguration;

  @InjectMocks private ContactMappingService contactMappingService;

  @ParameterizedTest
  @EnumSource(MappingTestCase.class)
  void testGetMapping(MappingTestCase testCase) {
    final boolean willSearch = testCase.isMappingEnabled() && testCase.getMockedMapping() != null;
    if (willSearch) {
      when(mappingRepository.findMappingForAddress(testCase.getAddress()))
          .thenReturn(testCase.getMockedMapping());
    }
    if (testCase.getAddress() != null) {
      when(notificationConfiguration.isContactMappingEnabled())
          .thenReturn(testCase.isMappingEnabled());
    }

    final String mappedAddress = contactMappingService.mapContact(testCase.getAddress());

    if (testCase.getExpectedAddress() != null) {
      assertThat(mappedAddress).isEqualTo(testCase.getExpectedAddress());
    } else {
      assertThat(mappedAddress).isNull();
    }

    verify(mappingRepository, times(willSearch ? 1 : 0)).findMappingForAddress(anyString());
    verify(notificationConfiguration, times(testCase.getAddress() != null ? 1 : 0))
        .isContactMappingEnabled();
  }

  @Getter
  @AllArgsConstructor
  enum MappingTestCase {
    MAPPING_DISABLED("original", null, false, "original"),
    NULL_ADDRESS(null, null, true, null),
    MAPPING_FOUND("original", TEST_MAPPING, true, TEST_MAPPING.getFirst()),
    NO_MAPPING_FOUND("original", TEST_NO_MAPPING, true, null);
    private final String address;
    private final List<String> mockedMapping;
    private final boolean mappingEnabled;
    private final String expectedAddress;
  }
}

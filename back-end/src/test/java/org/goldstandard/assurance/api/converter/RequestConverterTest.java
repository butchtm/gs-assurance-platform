package org.goldstandard.assurance.api.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.goldstandard.assurance.api.dto.RequestDTO;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.repository.ProjectCreditingPeriodRepository;
import org.goldstandard.assurance.api.repository.RequestMethodologyRepository;
import org.goldstandard.assurance.api.repository.system.RequestStatusHistoryRepository;
import org.goldstandard.assurance.api.service.BatchRequestFinder;
import org.goldstandard.assurance.api.service.RequestFieldAccessPolicy;
import org.goldstandard.assurance.api.service.StakeholderConsultationFinder;
import org.goldstandard.assurance.api.service.activity.RequestActivityFinder;
import org.goldstandard.assurance.api.service.conversation.ConversationFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestConverterTest {
  @Mock private StakeholderConsultationFinder stakeholderConsultationFinder;
  @Mock private RequestStatusHistoryRepository requestStatusHistoryRepository;
  @Mock private RequestMethodologyRepository requestMethodologyRepository;
  @Mock private BatchRequestFinder batchRequestFinder;
  @Mock private RequestFieldAccessPolicy policy;
  @Mock private RequestActivityFinder activityFinder;
  @Mock private RequestActivityConverter activityMapper;
  @Mock private ProjectConverter projectConverter;
  @Mock private ProjectCreditingPeriodRepository projectCreditingPeriodRepository;
  @Mock private ProjectCreditingPeriodConverter projectCreditingPeriodConverter;
  @Mock private MethodologyConverter methodologyConverter;
  @Mock private ProjectCreditingMonitoringPeriodConverter monitoringPeriodConverter;
  @Mock private ConversationFinder conversationFinder;
  @Mock private ConversationConverter conversationConverter;

  private RequestConverter converter;

  @BeforeEach
  void setUp() {
    converter =
        new RequestConverter(
            stakeholderConsultationFinder,
            requestStatusHistoryRepository,
            requestMethodologyRepository,
            batchRequestFinder,
            policy,
            activityFinder,
            activityMapper,
            projectConverter,
            projectCreditingPeriodRepository,
            projectCreditingPeriodConverter,
            methodologyConverter,
            monitoringPeriodConverter,
            conversationFinder,
            conversationConverter);
  }

  static Stream<Arguments> paaFlagTestCases() {
    return Stream.of(Arguments.of(true, "paa true"), Arguments.of(false, "paa false"));
  }

  @ParameterizedTest(name = "{index}: {1} → expected={0}")
  @MethodSource("paaFlagTestCases")
  void mapsPaaToRequestDetailDto(boolean paa, String description) {
    var request = Request.builder().paa(paa).build();

    RequestDTO result = converter.toRequestDetailDTO(request, 0);

    assertEquals(paa, result.isPaa(), description);
  }
}

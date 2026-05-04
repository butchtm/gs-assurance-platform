package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;

import java.util.Arrays;
import java.util.Collection;
import org.goldstandard.assurance.api.entity.StakeholderConsultation;
import org.junit.jupiter.api.Test;

class ConsultationAuditInfoFactoryTest {

  private static final int EXPECTED_SIZE = 2;

  private final ConsultationAuditInfoFactory factory = new ConsultationAuditInfoFactory();

  @Test
  void shouldPopulateRequestId() {
    var requestId = 1;

    var info = factory.build(requestId, givenConsultationsWithIds(2, 3));

    assertThat(info).hasSize(EXPECTED_SIZE).contains(entry("requestId", requestId));
  }

  @Test
  void shouldPopulateConsultationIds() {
    var consultations = givenConsultationsWithIds(4, 5);

    var info = factory.build(999, consultations);

    assertThat(info).hasSize(EXPECTED_SIZE).contains(entry("consultationIds", "4,5"));
  }

  private static Collection<StakeholderConsultation> givenConsultationsWithIds(Integer... ids) {
    return Arrays.stream(ids)
        .map(ConsultationAuditInfoFactoryTest::givenConsultationWithId)
        .toList();
  }

  private static StakeholderConsultation givenConsultationWithId(int id) {
    var consultation = new StakeholderConsultation();
    consultation.setId(id);
    return consultation;
  }
}

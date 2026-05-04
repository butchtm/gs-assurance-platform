package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import org.goldstandard.assurance.api.entity.ConsultationType;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.StakeholderConsultation;
import org.goldstandard.assurance.api.entity.fixtures.ConsultationTypeMother;
import org.goldstandard.assurance.api.repository.ConsultationTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StakeholderConsultationFactoryTest {

  @InjectMocks StakeholderConsultationFactory stakeholderConsultationFactory;

  @Mock Clock clock;

  @Mock ConsultationTypeRepository consultationTypeRepository;

  @Mock WeekdayCalculator weekdayCalculator;

  @Mock StakeholderConsultationFinder finder;

  @Test
  void shouldReturnEmptyListIfRequestIsAlreadyInConsultation() {
    Request request = buildRequest();
    when(finder.isInConsultation(request)).thenReturn(true);

    List<StakeholderConsultation> consultations =
        stakeholderConsultationFactory.buildConsultations(request);

    assertThat(consultations).isEmpty();
  }

  @Test
  void shouldCreateConsultationOfEachTypeIfNotAlreadyInConsultation() {
    mockTimeNow();
    Request request = buildRequest();
    when(finder.isInConsultation(request)).thenReturn(false);
    ConsultationType publicType = ConsultationTypeMother.publicType();
    ConsultationType tacType = ConsultationTypeMother.tacType();
    when(consultationTypeRepository.findAll()).thenReturn(List.of(publicType, tacType));

    List<StakeholderConsultation> consultations =
        stakeholderConsultationFactory.buildConsultations(request);

    assertThat(consultations)
        .map(StakeholderConsultation::getConsultationType)
        .containsExactly(publicType, tacType);
  }

  @Test
  void shouldPopulateConsultationIfNotAlreadyInConsultation() {
    ZonedDateTime now = mockTimeNow();
    Request request = buildRequest();
    when(finder.isInConsultation(request)).thenReturn(false);
    ConsultationType type = ConsultationTypeMother.publicType();
    when(consultationTypeRepository.findAll()).thenReturn(List.of(type));
    int workingDays = type.getWorkingDays();
    ZonedDateTime expectedEndDate = mockWorkingDays(now, workingDays);

    List<StakeholderConsultation> consultations =
        stakeholderConsultationFactory.buildConsultations(request);

    assertThat(consultations).hasSize(1);
    StakeholderConsultation consultation = consultations.getFirst();
    assertThat(consultation.getRequest()).isEqualTo(request);
    assertThat(consultation.getProject()).isEqualTo(request.getProject());
    assertThat(consultation.getConsultationType()).isEqualTo(type);
    assertThat(consultation.getStartDate()).isEqualTo(now);
    assertThat(consultation.getEndDate()).isEqualTo(expectedEndDate);
  }

  private ZonedDateTime mockTimeNow() {
    ZonedDateTime now = ZonedDateTime.now();
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(clock.instant()).thenReturn(now.toInstant());
    return now;
  }

  private ZonedDateTime mockWorkingDays(ZonedDateTime now, int workingDays) {
    ZonedDateTime endDate = now.plus(Duration.ofDays(workingDays));
    when(weekdayCalculator.addWeekdays(now, workingDays)).thenReturn(endDate);
    return endDate;
  }

  private static Request buildRequest() {
    Request request = new Request();
    request.setProject(new Project(1));
    request.setId(2);
    return request;
  }
}

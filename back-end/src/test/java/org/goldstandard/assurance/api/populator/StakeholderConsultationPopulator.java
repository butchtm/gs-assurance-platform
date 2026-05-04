package org.goldstandard.assurance.api.populator;

import static org.goldstandard.assurance.api.populator.ProjectPopulator.TEST_PROJECTS;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.ConsultationType;
import org.goldstandard.assurance.api.entity.StakeholderConsultation;
import org.goldstandard.assurance.api.repository.ConsultationTypeRepository;
import org.goldstandard.assurance.api.repository.RequestRepository;
import org.goldstandard.assurance.api.repository.StakeholderConsultationRepository;
import org.goldstandard.assurance.api.service.WeekdayCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StakeholderConsultationPopulator {

  public List<StakeholderConsultation> TEST_CONSULTATIONS = new ArrayList<>();

  @Autowired private StakeholderConsultationRepository stakeholderConsultationRepository;

  @Autowired private ConsultationTypeRepository consultationTypeRepository;

  @Autowired private WeekdayCalculator weekdayCalculator;

  @Autowired private RequestRepository requestRepository;

  public void populate() {
    var tacType = new ConsultationType(0, "TAC", 15);
    var pubType = new ConsultationType(1, "PUBLIC", 10);
    var consultationTypes = List.of(tacType, pubType);
    consultationTypeRepository.saveAll(consultationTypes);
    var consultations = new ArrayList<StakeholderConsultation>();
    for (var project : TEST_PROJECTS) {
      for (var type : consultationTypes) {
        var request = requestRepository.findByProjectId(project.getId());
        var start = project.getProjectStartDate();
        var consultation =
            StakeholderConsultation.builder()
                .consultationType(type)
                .project(project)
                .request(request.getFirst())
                .startDate(start)
                .endDate(weekdayCalculator.addWeekdays(start, type.getWorkingDays()))
                .build();
        consultations.add(consultation);
      }
    }
    TEST_CONSULTATIONS = stakeholderConsultationRepository.saveAll(consultations);
    TEST_CONSULTATIONS.forEach(
        c ->
            log.info(
                "populated consultation {} for request {}", c.getId(), c.getRequest().getId()));
  }

  public boolean isPopulated() {
    return stakeholderConsultationRepository.count() > 0;
  }
}

package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import org.goldstandard.assurance.api.dto.ProjectRiskSummaryCreateDTO;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.ProjectRiskSummary;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.repository.ProjectRiskSummaryRepository;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ProjectRiskSummaryServiceTest {

  @Mock ProjectRiskSummaryRepository repository;
  @Mock ProjectFinder projectFinder;
  @InjectMocks ProjectRiskSummaryService service;

  @Mock AuthenticatedUserSupplier authenticatedUserSupplier;

  Project project;
  ProjectRiskSummaryCreateDTO dto;
  ProjectRiskSummary summary;

  @BeforeEach
  void setUp() {
    project = Project.builder().id(1).build();
    dto =
        ProjectRiskSummaryCreateDTO.builder()
            .risk(true)
            .summary("Test summary")
            .dateApplied(LocalDateTime.now())
            .build();
    summary =
        ProjectRiskSummary.builder()
            .id(10)
            .project(project)
            .risk(dto.getRisk())
            .summary(dto.getSummary())
            .dateApplied(dto.getDateApplied())
            .build();
  }

  @Test
  void create_shouldSaveAndReturnSummary() {
    when(projectFinder.forceGetProjectById(1)).thenReturn(project);
    when(repository.save(any(ProjectRiskSummary.class))).thenReturn(summary);
    var user = mock(User.class);
    when(authenticatedUserSupplier.forceGetAuthenticatedUser()).thenReturn(user);
    var result = service.create(1, dto);
    assertThat(result).isEqualTo(summary);
    verify(repository).save(any(ProjectRiskSummary.class));
  }

  @Test
  void getRiskSummary_shouldReturnPage() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<ProjectRiskSummary> page = new PageImpl<>(List.of(summary));
    when(repository.findByProjectId(1, pageable)).thenReturn(page);
    var result = service.getRiskSummary(1, pageable);
    assertThat(result.getContent()).containsExactly(summary);
  }

  @Test
  void getCurrentRiskSummary_shouldReturnLatest() {
    ProjectRiskSummary older =
        ProjectRiskSummary.builder()
            .id(11)
            .project(project)
            .risk(false)
            .summary("Old summary")
            .dateApplied(LocalDateTime.now().minusDays(1))
            .build();
    when(repository.findByProjectId(1)).thenReturn(List.of(older, summary));
    var result = service.getCurrentRiskSummary(1);
    assertThat(result).isEqualTo(summary);
  }

  @Test
  void getCurrentRiskSummary_shouldReturnNullIfEmpty() {
    when(repository.findByProjectId(1)).thenReturn(List.of());
    var result = service.getCurrentRiskSummary(1);
    assertThat(result).isNull();
  }
}

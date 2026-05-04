package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.goldstandard.assurance.api.dto.MethodologyManagementDTO;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriodMethodology;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.exception.BadRequestException;
import org.goldstandard.assurance.api.repository.MethodologyRepository;
import org.goldstandard.assurance.api.repository.ProjectCreditingPeriodMethodologyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MethodologyServiceTest {

  @InjectMocks MethodologyService service;

  @Mock MethodologyValidator validator;

  @Spy MethodologyFactory factory;

  @Mock MethodologyRepository repository;

  @Mock ProjectCreditingPeriodMethodologyRepository periodMethodologyRepository;

  @Mock ProjectFinder projectFinder;

  @Captor ArgumentCaptor<Methodology> methodologyCaptor;

  @Test
  void shouldCreateNewMethodology() {
    var dto = MethodologyManagementDTO.builder().description("test").active(true).build();
    service.create(dto);

    verify(repository).save(methodologyCaptor.capture());

    var actual = methodologyCaptor.getValue();
    assertThat(actual.getDescription()).isEqualTo(dto.getDescription());
    assertThat(actual.getCode()).isEqualTo(dto.getDescription().toUpperCase());
    assertThat(actual.getActive()).isEqualTo(dto.getActive());
  }

  @Test
  void shouldUpdateMethodology() {
    var id = 1000;
    var dto = MethodologyManagementDTO.builder().description("test").active(true).build();
    var existing =
        Methodology.builder().id(id).description("initial").code("INITIAL").isActive(false).build();
    when(repository.forceFindById(id)).thenReturn(existing);
    service.update(id, dto);

    verify(repository).save(methodologyCaptor.capture());

    var actual = methodologyCaptor.getValue();
    assertThat(actual.getId()).isEqualTo(id);
    assertThat(actual.getDescription()).isEqualTo(dto.getDescription());
    assertThat(actual.getCode()).isEqualTo(dto.getDescription().toUpperCase());
    assertThat(actual.getActive()).isEqualTo(dto.getActive());
  }

  @Test
  void shouldDeleteMethodologyIfExistsNotInUse() {
    var id = 1000;
    var existing = Methodology.builder().id(id).build();
    when(repository.forceFindById(id)).thenReturn(existing);
    when(periodMethodologyRepository.findAllByMethodologyId(id))
        .thenReturn(Collections.emptyList());
    service.delete(id);

    verify(repository).delete(methodologyCaptor.capture());
    assertThat(methodologyCaptor.getValue().getId()).isEqualTo(id);
  }

  @Test
  void shouldThrowExceptionIfMethodologyInUse() {
    var id = 1000;
    var creditingMethodology = ProjectCreditingPeriodMethodology.builder().projectId(1).build();
    var project = Project.builder().id(1).gsid("GS123").build();
    when(periodMethodologyRepository.findAllByMethodologyId(id))
        .thenReturn(List.of(creditingMethodology));
    when(projectFinder.getByIds(Set.of(project.getId()))).thenReturn(List.of(project));
    assertThrows(BadRequestException.class, () -> service.delete(id));
    verifyNoMoreInteractions(repository);
  }
}

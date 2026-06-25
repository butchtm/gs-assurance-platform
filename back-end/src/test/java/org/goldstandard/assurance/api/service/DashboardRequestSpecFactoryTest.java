package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import java.util.Collection;
import java.util.List;
import org.goldstandard.assurance.api.dto.CommonDashboardRequestFiltersDTO;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.entity.view.CommonDashboardView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardRequestSpecFactoryTest {

  @InjectMocks private DashboardRequestSpecFactory factory;

  @Mock private ProjectSearchMatchSpecFactory searchMatchSpecFactory;
  @Mock private Root<CommonDashboardView> root;
  @Mock private CriteriaQuery<?> query;
  @Mock private CriteriaBuilder criteriaBuilder;
  @Mock private Path<Object> projectClassificationIdPath;
  @Mock private CriteriaBuilder.In<Object> inPredicate;

  @Test
  void shouldExpandDisplayProjectClassificationFilterIdsToCrossLinkageIds() {
    var filters =
        CommonDashboardRequestFiltersDTO.builder()
            .projectClassificationIds(
                List.of(
                    ProjectClassification.REAL_CASE_VPA_ID,
                    ProjectClassification.REGULAR_CASE_VPA_ID))
            .build();
    when(root.<Object>get("projectClassificationId")).thenReturn(projectClassificationIdPath);
    when(criteriaBuilder.in(projectClassificationIdPath)).thenReturn(inPredicate);

    factory.toSpec(filters).toPredicate(root, query, criteriaBuilder);

    var ids = ArgumentCaptor.forClass(Collection.class);
    verify(inPredicate).value(ids.capture());
    assertThat(ids.getValue())
        .containsExactlyInAnyOrder(
            ProjectClassification.REAL_CASE_VPA_ID,
            ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_ID,
            ProjectClassification.REGULAR_CASE_VPA_ID,
            ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_ID);
  }
}

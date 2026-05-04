package org.goldstandard.assurance.api.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.goldstandard.assurance.api.entity.lookup.BatchType;
import org.junit.jupiter.api.Test;

class IsDirectChildProjectPredicateFactoryTest {

  private final IsDirectChildProjectPredicateFactory factory =
      new IsDirectChildProjectPredicateFactory();

  @Test
  void shouldReturnVpaCpaPredicateForVpaCpaBatchType() {
    var project = mock(Project.class);
    var batchType = mock(BatchType.class);
    when(batchType.isTwoOrMoreVpaCpa()).thenReturn(true);

    var predicate = factory.build(project, batchType);

    assertThat(predicate).isInstanceOf(VpaCpaIsDirectChildProjectPredicate.class);
  }

  @Test
  void shouldReturnDefaultPredicateForAnyOtherBatchType() {
    var project = mock(Project.class);
    var batchType = mock(BatchType.class);
    when(batchType.isTwoOrMoreVpaCpa()).thenReturn(false);

    var predicate = factory.build(project, batchType);

    assertThat(predicate).isInstanceOf(DefaultIsDirectChildProjectPredicate.class);
  }
}

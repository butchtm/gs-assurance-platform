package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.entity.lookup.PiiAction.ACCEPTED_ID;
import static org.goldstandard.assurance.api.entity.lookup.PiiAction.NEW_STATUS_ID;
import static org.goldstandard.assurance.api.entity.lookup.PiiAction.UNDER_INVESTIGATION_ID;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import org.goldstandard.assurance.api.dto.ProbablePii;
import org.goldstandard.assurance.api.dto.TextAnalysisResultsDTO;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.entity.lookup.PiiAction;
import org.goldstandard.assurance.api.entity.pii.PiiMetadata;
import org.goldstandard.assurance.api.enumeration.PiiType;
import org.goldstandard.assurance.api.fixtures.DocumentPiiAnalysisResultsMother;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.goldstandard.assurance.api.service.pii.PiiMetadataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PiiMetadataFactoryTest {

  @InjectMocks PiiMetadataFactory factory;
  @Mock AuthenticatedUserSupplier userSupplier;
  @Mock Clock clock;

  @BeforeEach
  void mockTime() {
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(clock.instant()).thenReturn(ZonedDateTime.now().toInstant());
  }

  @Test
  void shouldCreateSingleMetadataRecord() {
    var piiOne = buildProbablePiiForField("description");
    var piiTwo = buildProbablePiiForField("description");
    var piiThree = buildProbablePiiForField("description");
    var results =
        TextAnalysisResultsDTO.builder().probablePii(List.of(piiOne, piiTwo, piiThree)).build();
    mockUserAndAccount();

    var actual = factory.buildForProject(results, mock(Project.class));

    assertThat(actual).hasSize(1);
    assertThat(actual.stream().map(PiiMetadata::getType).toList()).containsOnly(PiiType.PROJECT);
    assertThat(actual.stream().map(PiiMetadata::getAction).toList()).allMatch(PiiAction::isNew);
    assertThat(actual.stream().map(PiiMetadata::getTypeDetail).toList())
        .containsExactly(piiOne.getField());
  }

  private static ProbablePii buildProbablePiiForField(String field) {
    return ProbablePii.builder().field(field).build();
  }

  @Test
  void shouldCreateMetadataRecordForEachUniqueField() {
    var piiOne = buildProbablePiiForField("description");
    var piiTwo = buildProbablePiiForField("description");
    var piiThree = buildProbablePiiForField("notes");
    var piiFour = buildProbablePiiForField("notes");
    var piiFive = buildProbablePiiForField("title");
    var piiSix = buildProbablePiiForField("title");
    var results =
        TextAnalysisResultsDTO.builder()
            .probablePii(List.of(piiOne, piiTwo, piiThree, piiTwo, piiFour, piiFive, piiSix))
            .build();
    mockUserAndAccount();

    var actual = factory.buildForProject(results, mock(Project.class));

    assertThat(actual).hasSize(3);
    assertThat(actual.stream().map(PiiMetadata::getType).toList()).containsOnly(PiiType.PROJECT);
    assertThat(actual.stream().map(PiiMetadata::getAction).toList()).allMatch(PiiAction::isNew);
    assertThat(actual.stream().map(PiiMetadata::getTypeDetail).toList())
        .containsExactlyInAnyOrder(piiOne.getField(), piiThree.getField(), piiFive.getField());
  }

  private void mockUserAndAccount() {
    var user = mock(User.class);
    when(userSupplier.forceGetAuthenticatedUser()).thenReturn(user);
    when(user.getAccount()).thenReturn(mock(Account.class));
  }

  @Test
  void shouldBuildMetadataForDocumentResults() {
    var results = DocumentPiiAnalysisResultsMother.build(UUID.randomUUID());

    var metadata = factory.buildForDocument(results);

    assertThat(metadata.getDocumentId()).isEqualTo(results.getDocument().getId());
    assertThat(metadata.getProjectId()).isEqualTo(results.getProject().getId());
    assertThat(metadata.getRequestId()).isEqualTo(results.getRequest().getId());
    assertThat(metadata.getType()).isEqualTo(PiiType.DOCUMENT);
    assertThat(metadata.getTypeDetail()).isEqualTo(results.getDocument().getFileName());
    assertThat(metadata.getSubmittedByUserId()).isEqualTo(results.getUser().getId());
    assertThat(metadata.getSubmittedByAccountId())
        .isEqualTo(results.getUser().getAccount().getId());
    assertThat(metadata.getCreated()).isNotNull();
    assertThat(metadata.isActioned()).isFalse();
    assertThat(metadata.getAction().getId()).isEqualTo(NEW_STATUS_ID);
  }

  @Test
  void updateMetadataAction_ShouldUpdateAllMetadataRecords() {
    var user = mock(User.class);
    when(user.getId()).thenReturn(1);
    var account = mock(Account.class);
    when(account.getId()).thenReturn(2);
    when(user.getAccount()).thenReturn(account);
    when(userSupplier.forceGetAuthenticatedUser()).thenReturn(user);

    var metadata1 = buildMetadata();
    var metadata2 = buildMetadata();
    var action = new PiiAction(ACCEPTED_ID);

    var result = factory.updateMetadataAction(List.of(metadata1, metadata2), action);

    assertThat(result)
        .hasSize(2)
        .allSatisfy(
            metadata -> {
              assertThat(metadata.getActionedByUserId()).isEqualTo(1);
              assertThat(metadata.getActionedByAccountId()).isEqualTo(2);
              assertThat(metadata.getAction()).isEqualTo(action);
              assertThat(metadata.isActioned()).isTrue();
              assertThat(metadata.getUpdated()).isNotNull();
            });
  }

  @Test
  void updateMetadataAction_WithNewStatus_ShouldNotMarkAsActioned() {
    mockUserAndAccount();
    var metadata = buildMetadata();
    var action = new PiiAction(UNDER_INVESTIGATION_ID);

    var result = factory.updateMetadataAction(List.of(metadata), action);

    assertThat(result).hasSize(1).allMatch(m -> !m.isActioned());
  }

  private PiiMetadata buildMetadata() {
    return PiiMetadata.builder()
        .id(1)
        .projectId(999)
        .type(PiiType.PROJECT)
        .typeDetail("test")
        .created(ZonedDateTime.now())
        .actioned(false)
        .action(new PiiAction(NEW_STATUS_ID))
        .build();
  }
}

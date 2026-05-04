package org.goldstandard.assurance.api.service.pii;

import static org.goldstandard.assurance.api.entity.lookup.PiiAction.NEW_STATUS_ID;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.TextAnalysisResultsDTO;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Comment;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.entity.lookup.PiiAction;
import org.goldstandard.assurance.api.entity.pii.PiiMetadata;
import org.goldstandard.assurance.api.enumeration.PiiType;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.goldstandard.assurance.api.service.document.pii.analysis.DocumentPiiAnalysisResults;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PiiMetadataFactory {

  private final AuthenticatedUserSupplier userFinder;
  private final RequestFinder requestFinder;
  private final Clock clock;

  public List<PiiMetadata> buildForProject(TextAnalysisResultsDTO textAnalysis, Project project) {
    List<PiiMetadata> piiMetadata = new ArrayList<>();
    var submittedByUser = userFinder.forceGetAuthenticatedUser();
    for (var field : textAnalysis.getDistinctFields()) {
      piiMetadata.add(buildForProject(project, field, submittedByUser));
    }
    return piiMetadata;
  }

  public PiiMetadata buildForDocument(DocumentPiiAnalysisResults results) {
    return buildPiiMetadata(results.getProject(), results.getUser())
        .documentId(results.getDocument().getId())
        .requestId(results.getRequest().getId())
        .type(PiiType.DOCUMENT)
        .typeDetail(results.getDocument().getFileName())
        .build();
  }

  public PiiMetadata buildForComment(Comment comment) {
    User submittedByUser =
        Objects.nonNull(comment.getAccount()) ? userFinder.forceGetAuthenticatedUser() : null;
    var request = requestFinder.getRequestById(comment.getRequestId());
    return buildPiiMetadata(request.getProject(), submittedByUser)
        .requestId(request.getId())
        .comment(comment)
        .type(PiiType.COMMENT)
        .build();
  }

  public PiiMetadata updateMetadataAction(PiiMetadata metadata, PiiAction action) {
    return updateMetadataAction(Collections.singletonList(metadata), action).getFirst();
  }

  public List<PiiMetadata> updateMetadataAction(
      Collection<PiiMetadata> metadataList, PiiAction action) {
    if (metadataList.isEmpty()) {
      return List.of();
    }
    var actionedByUser = userFinder.forceGetAuthenticatedUser();
    return metadataList.stream()
        .map(metadata -> updateMetadataAction(metadata, action, actionedByUser))
        .toList();
  }

  private PiiMetadata updateMetadataAction(
      PiiMetadata metadata, PiiAction action, User actionedByUser) {
    return metadata.toBuilder()
        .actioned(action.isActioned())
        .actionedByUserId(Optional.ofNullable(actionedByUser).map(User::getId).orElse(null))
        .actionedByAccountId(
            Optional.ofNullable(actionedByUser)
                .flatMap(u -> Optional.ofNullable(u.getAccount()))
                .map(Account::getId)
                .orElse(null))
        .action(action)
        .updated(ZonedDateTime.now(clock))
        .build();
  }

  private PiiMetadata buildForProject(Project project, String field, User submittedByUser) {
    return buildPiiMetadata(project, submittedByUser)
        .type(PiiType.PROJECT)
        .typeDetail(field)
        .build();
  }

  private PiiMetadata.PiiMetadataBuilder buildPiiMetadata(Project project, User submittedByUser) {
    return PiiMetadata.builder()
        .projectId(Optional.ofNullable(project).map(Project::getId).orElse(null))
        .submittedByUserId(Optional.ofNullable(submittedByUser).map(User::getId).orElse(null))
        .submittedByAccountId(
            Optional.ofNullable(submittedByUser)
                .map(User::getAccount)
                .map(Account::getId)
                .orElse(null))
        .created(ZonedDateTime.now(clock))
        .actioned(false)
        .action(new PiiAction(NEW_STATUS_ID));
  }
}

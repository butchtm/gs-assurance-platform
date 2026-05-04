package org.goldstandard.assurance.api.fixtures;

import java.util.UUID;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.service.document.pii.analysis.DocumentPiiAnalysisResults;

public class DocumentPiiAnalysisResultsMother {

  public static DocumentPiiAnalysisResults build(UUID documentId) {
    return buildResults()
        .document(DocumentMother.document(documentId, 1))
        .request(new Request())
        .build();
  }

  public static DocumentPiiAnalysisResults withContentType(UUID documentId, String contentType) {
    return buildResults().document(DocumentMother.withContentType(documentId, contentType)).build();
  }

  private static DocumentPiiAnalysisResults.DocumentPiiAnalysisResultsBuilder buildResults() {
    return DocumentPiiAnalysisResults.builder()
        .project(new Project())
        .user(user())
        .probablePii(ProbablePiiMother.buildList());
  }

  private static User user() {
    return User.builder().account(account()).build();
  }

  private static Account account() {
    return new Account();
  }
}

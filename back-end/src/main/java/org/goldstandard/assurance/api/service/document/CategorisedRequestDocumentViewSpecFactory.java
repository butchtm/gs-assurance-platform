package org.goldstandard.assurance.api.service.document;

import static org.goldstandard.assurance.api.entity.lookup.AccountType.REVIEWER_ACCOUNT_TYPE_ID;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.view.CategorisedRequestDocumentView;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategorisedRequestDocumentViewSpecFactory {

  private final DocumentAccessPolicy documentAccessPolicy;

  public Specification<CategorisedRequestDocumentView> toSpec(
      Integer projectId, Integer requestId, Integer displayCategoryId) {
    Specification<CategorisedRequestDocumentView> spec =
        hasRequestId(requestId).and(hasDocumentDisplayCategoryId(displayCategoryId));
    return applyAccessRestrictions(projectId, requestId, spec);
  }

  public Specification<CategorisedRequestDocumentView> toSpec(
      Integer projectId, Integer requestId) {
    Specification<CategorisedRequestDocumentView> spec = hasRequestId(requestId);
    return applyAccessRestrictions(projectId, requestId, spec);
  }

  private Specification<CategorisedRequestDocumentView> applyAccessRestrictions(
      Integer projectId, Integer requestId, Specification<CategorisedRequestDocumentView> spec) {
    if (!documentAccessPolicy.canViewPrivateDocuments(projectId, requestId)) {
      spec = spec.and(publicOnly());
    }
    if (!documentAccessPolicy.canUserViewReviewerUploadedDocuments(requestId)) {
      spec = spec.and(notUploadedByReviewer());
    }
    return spec;
  }

  private static Specification<CategorisedRequestDocumentView> hasRequestId(Integer requestId) {
    return (document, cq, cb) -> cb.equal(document.join("request").get("id"), requestId);
  }

  private static Specification<CategorisedRequestDocumentView> notUploadedByReviewer() {
    return (view, cq, cb) -> {
      Join<Account, Document> account =
          view.join("document").join("uploadedBy", JoinType.LEFT).join("account", JoinType.LEFT);
      Path<Object> accountTypeId = account.get("accountType").get("id");
      return cb.or(cb.isNull(account), cb.notEqual(accountTypeId, REVIEWER_ACCOUNT_TYPE_ID));
    };
  }

  private static Specification<CategorisedRequestDocumentView> hasDocumentDisplayCategoryId(
      Integer displayCategoryId) {
    return (document, cq, cb) ->
        cb.equal(document.get("documentDisplayCategoryId"), displayCategoryId);
  }

  private static Specification<CategorisedRequestDocumentView> publicOnly() {
    return (document, cq, cb) -> cb.equal(document.join("document").get("access"), true);
  }
}

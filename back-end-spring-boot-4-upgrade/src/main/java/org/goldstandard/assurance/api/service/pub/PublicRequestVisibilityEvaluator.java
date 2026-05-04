package org.goldstandard.assurance.api.service.pub;

import java.util.List;
import java.util.Objects;
import org.goldstandard.assurance.api.entity.Comment;
import org.goldstandard.assurance.api.entity.view.PublicDocumentView;
import org.goldstandard.assurance.api.entity.view.PublicRequestsView;
import org.springframework.stereotype.Component;

@Component
public class PublicRequestVisibilityEvaluator {

  public boolean shouldShowRequestOnPublicPage(
      PublicRequestsView request,
      List<PublicDocumentView> requestDocuments,
      List<Comment> requestComments) {
    return !requestDocuments.isEmpty()
        || !requestComments.isEmpty()
        || (Objects.nonNull(request.getConsultationStartDate())
            && Objects.nonNull(request.getConsultationEndDate()));
  }
}

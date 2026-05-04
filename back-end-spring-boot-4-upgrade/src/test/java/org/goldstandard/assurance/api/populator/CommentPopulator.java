package org.goldstandard.assurance.api.populator;

import static org.goldstandard.assurance.api.populator.RequestPopulator.TEST_REQUESTS;

import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Comment;
import org.goldstandard.assurance.api.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CommentPopulator {

  @Autowired CommentRepository commentRepository;

  public void populate() {
    for (var request : TEST_REQUESTS) {
      var comment =
          Comment.builder()
              .requestId(request.getId())
              .submittedComment("test comment: " + request.getRequestName())
              .uploadedByName("test name")
              .uploadedByEmail("test email")
              .uploadedDate(request.getProject().getProjectStartDate())
              .addressed(false)
              .publicAccess(true)
              .anonymous(false)
              .build();

      var savedComment = commentRepository.save(comment);
      log.info(
          "saved comment {} against request {}", savedComment.getId(), savedComment.getRequestId());
    }
  }

  public boolean isPopulated() {
    return commentRepository.count() > 0;
  }
}

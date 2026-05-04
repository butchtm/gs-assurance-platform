package org.goldstandard.assurance.api.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Comment;
import org.goldstandard.assurance.api.repository.CommentRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentFinder {

  private final CommentRepository commentRepository;
  private final CommentAccessPolicy accessPolicy;

  public List<Comment> getCommentsByRequestId(Integer requestId) {
    var comments = commentRepository.findByRequestIdOrderByUploadedDateAsc(requestId);
    return accessPolicy.process(comments);
  }
}

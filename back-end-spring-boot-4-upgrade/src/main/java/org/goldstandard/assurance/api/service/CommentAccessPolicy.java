package org.goldstandard.assurance.api.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Comment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentAccessPolicy {
  private final CommentAccessRightsCalculator accessRightsCalculator;

  public Comment anonymiseIfRequired(Comment comment) {
    var accessRights = accessRightsCalculator.calculateAccessRights();
    return new CommentAnonymiser(accessRights).apply(comment);
  }

  public Optional<Comment> process(Comment comment) {
    return process(Stream.of(comment)).findFirst();
  }

  public List<Comment> process(List<Comment> comments) {
    return process(comments.stream()).toList();
  }

  private Stream<Comment> process(Stream<Comment> comments) {
    var accessRights = accessRightsCalculator.calculateAccessRights();
    return comments
        .filter(new CommentPredicate(accessRights))
        .map(new CommentSuppressor())
        .map(new CommentAnonymiser(accessRights));
  }
}

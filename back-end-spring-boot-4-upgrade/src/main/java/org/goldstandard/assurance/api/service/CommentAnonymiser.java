package org.goldstandard.assurance.api.service;

import java.util.function.UnaryOperator;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Comment;
import org.goldstandard.assurance.api.entity.CommentAccessRights;

@RequiredArgsConstructor
public class CommentAnonymiser implements UnaryOperator<Comment> {

  private final CommentAccessRights accessRights;

  @Override
  public Comment apply(Comment comment) {
    if (comment.isAnonymous() && accessRights.isAnonymise()) {
      return comment.anonymise();
    }
    return comment;
  }
}

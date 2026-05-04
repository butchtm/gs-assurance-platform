package org.goldstandard.assurance.api.service;

import java.util.function.UnaryOperator;
import org.goldstandard.assurance.api.entity.Comment;

public class CommentSuppressor implements UnaryOperator<Comment> {

  @Override
  public Comment apply(Comment comment) {
    if (comment.isSuppressed()) {
      return comment.suppress();
    }
    return comment;
  }
}

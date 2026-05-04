package org.goldstandard.assurance.api.service;

import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Comment;
import org.goldstandard.assurance.api.entity.CommentAccessRights;

@RequiredArgsConstructor
public class CommentPredicate implements Predicate<Comment> {

  private final CommentAccessRights accessRights;

  @Override
  public boolean test(Comment comment) {
    if (comment.isTacComment()) {
      return accessRights.isIncludeTac();
    }
    if (comment.isSuppressed()) {
      return accessRights.isIncludeSuppressed();
    }
    if (comment.isDeleted()) {
      return accessRights.isIncludeDeleted();
    }
    if (comment.isPrivate()) {
      return accessRights.isIncludePrivate();
    }
    return true;
  }
}

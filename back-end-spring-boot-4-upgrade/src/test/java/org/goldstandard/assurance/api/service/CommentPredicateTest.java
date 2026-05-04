package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.goldstandard.assurance.api.entity.Comment;
import org.goldstandard.assurance.api.entity.CommentAccessRights;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentPredicateTest {

  @InjectMocks CommentPredicate predicate;

  @Mock CommentAccessRights accessRights;

  @Mock Comment comment;

  @Test
  void shouldNotIncludeTacCommentIfShouldNotBeIncluded() {
    when(comment.isTacComment()).thenReturn(true);
    when(accessRights.isIncludeTac()).thenReturn(false);

    var result = predicate.test(comment);

    assertThat(result).isFalse();
  }

  @Test
  void shouldShouldIncludeTacCommentIfShouldBeIncluded() {
    when(comment.isTacComment()).thenReturn(true);
    when(accessRights.isIncludeTac()).thenReturn(true);

    var result = predicate.test(comment);

    assertThat(result).isTrue();
  }

  @Test
  void shouldNotIncludeSuppressedCommentIfShouldNotBeIncluded() {
    when(comment.isSuppressed()).thenReturn(true);
    when(accessRights.isIncludeSuppressed()).thenReturn(false);

    var result = predicate.test(comment);

    assertThat(result).isFalse();
  }

  @Test
  void shouldShouldIncludeSuppressedCommentIfShouldBeIncluded() {
    when(comment.isSuppressed()).thenReturn(true);
    when(accessRights.isIncludeSuppressed()).thenReturn(true);

    var result = predicate.test(comment);

    assertThat(result).isTrue();
  }

  @Test
  void shouldNotIncludeDeletedCommentIfShouldNotBeIncluded() {
    when(comment.isDeleted()).thenReturn(true);
    when(accessRights.isIncludeDeleted()).thenReturn(false);

    var result = predicate.test(comment);

    assertThat(result).isFalse();
  }

  @Test
  void shouldShouldIncludeDeletedCommentIfShouldBeIncluded() {
    when(comment.isDeleted()).thenReturn(true);
    when(accessRights.isIncludeDeleted()).thenReturn(true);

    var result = predicate.test(comment);

    assertThat(result).isTrue();
  }

  @Test
  void shouldNotIncludePrivateCommentIfShouldNotBeIncluded() {
    when(comment.isPrivate()).thenReturn(true);
    when(accessRights.isIncludePrivate()).thenReturn(false);

    var result = predicate.test(comment);

    assertThat(result).isFalse();
  }

  @Test
  void shouldShouldIncludePrivateCommentIfShouldBeIncluded() {
    when(comment.isPrivate()).thenReturn(true);
    when(accessRights.isIncludePrivate()).thenReturn(true);

    var result = predicate.test(comment);

    assertThat(result).isTrue();
  }

  @Test
  void shouldIncludeCommentsThatAreNotTacOrSuppressedOrDeletedOrPrivate() {
    when(comment.isTacComment()).thenReturn(false);
    when(comment.isSuppressed()).thenReturn(false);
    when(comment.isDeleted()).thenReturn(false);
    when(comment.isPrivate()).thenReturn(false);

    var result = predicate.test(comment);

    assertThat(result).isTrue();
  }
}

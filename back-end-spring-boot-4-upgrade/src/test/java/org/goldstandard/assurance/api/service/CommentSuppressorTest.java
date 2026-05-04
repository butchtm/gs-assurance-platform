package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.goldstandard.assurance.api.entity.Comment;
import org.junit.jupiter.api.Test;

class CommentSuppressorTest {

  private final CommentSuppressor suppressor = new CommentSuppressor();

  @Test
  void shouldNotSuppressCommentIfNotSuppressed() {
    var originalComment = mock(Comment.class);
    when(originalComment.isSuppressed()).thenReturn(false);

    var comment = suppressor.apply(originalComment);

    assertThat(comment).isEqualTo(originalComment);
  }

  @Test
  void shouldSuppressCommentIftSuppressed() {
    var originalComment = mock(Comment.class);
    when(originalComment.isSuppressed()).thenReturn(true);
    var expectedComment = mock(Comment.class);
    when(originalComment.suppress()).thenReturn(expectedComment);

    var comment = suppressor.apply(originalComment);

    assertThat(comment).isEqualTo(expectedComment);
  }
}

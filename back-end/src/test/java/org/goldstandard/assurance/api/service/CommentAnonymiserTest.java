package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.goldstandard.assurance.api.entity.Comment;
import org.goldstandard.assurance.api.entity.CommentAccessRights;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentAnonymiserTest {

  @InjectMocks CommentAnonymiser anonymiser;

  @Mock CommentAccessRights accessRights;

  @Test
  void shouldNotAnonymiseCommentIfNotAnonymous() {
    var originalComment = mock(Comment.class);
    when(originalComment.isAnonymous()).thenReturn(false);

    var comment = anonymiser.apply(originalComment);

    assertThat(comment).isEqualTo(originalComment);
  }

  @Test
  void shouldNotAnonymiseCommentIfAccessRightsDoNotRequireAnonymisation() {
    var originalComment = mock(Comment.class);
    when(originalComment.isAnonymous()).thenReturn(true);
    when(accessRights.isAnonymise()).thenReturn(false);

    var comment = anonymiser.apply(originalComment);

    assertThat(comment).isEqualTo(originalComment);
  }

  @Test
  void shouldAnonymiseCommentIsAnonymousAndAccessRightsRequiresAnonymisation() {
    var originalComment = mock(Comment.class);
    var expectedComment = mock(Comment.class);
    when(originalComment.isAnonymous()).thenReturn(true);
    when(originalComment.anonymise()).thenReturn(expectedComment);
    when(accessRights.isAnonymise()).thenReturn(true);

    var comment = anonymiser.apply(originalComment);

    assertThat(comment).isEqualTo(expectedComment);
  }
}

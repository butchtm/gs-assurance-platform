package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.goldstandard.assurance.api.entity.CommentAccessRights;
import org.goldstandard.assurance.api.fixtures.AccountMother;
import org.goldstandard.assurance.api.fixtures.CommentMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentAccessPolicyTest {
  private static final String ANONYMOUS = "Anonymous";
  private static final String UPLOADED_BY_EMAIL = "uploadedByEmail";
  private static final String UPLOADED_BY_NAME = "uploadedByName";

  @InjectMocks CommentAccessPolicy accessPolicy;

  @Mock CommentAccessRightsCalculator accessRightsCalculator;

  @Mock CommentAccessRights accessRights;

  @Test
  void shouldAnonymiseCommentIfRequired() {
    var originalComment = CommentMother.anonymousComment();
    when(accessRightsCalculator.calculateAccessRights()).thenReturn(accessRights);
    when(accessRights.isAnonymise()).thenReturn(true);

    var comment = accessPolicy.anonymiseIfRequired(originalComment);

    assertThat(comment.getUploadedByEmail()).isEqualTo(ANONYMOUS);
    assertThat(comment.getUploadedByName()).isEqualTo(ANONYMOUS);
    assertThat(comment)
        .usingRecursiveComparison()
        .ignoringFields(UPLOADED_BY_EMAIL, UPLOADED_BY_NAME)
        .isEqualTo(originalComment);
  }

  @Test
  void shouldAnonymiseCommentsIfRequired() {
    when(accessRights.isAnonymise()).thenReturn(true);
    var originalComment = CommentMother.anonymousComment();
    when(accessRightsCalculator.calculateAccessRights()).thenReturn(accessRights);

    var comment = accessPolicy.process(originalComment).orElseThrow();

    assertThat(comment.getUploadedByEmail()).isEqualTo(ANONYMOUS);
    assertThat(comment.getUploadedByName()).isEqualTo(ANONYMOUS);
    assertThat(comment)
        .usingRecursiveComparison()
        .ignoringFields(UPLOADED_BY_EMAIL, UPLOADED_BY_NAME)
        .isEqualTo(originalComment);
  }

  @Test
  void shouldNotAnonymiseCommentIfNotRequired() {
    var originalComment = CommentMother.anonymousComment();
    when(accessRightsCalculator.calculateAccessRights()).thenReturn(accessRights);
    when(accessRights.isAnonymise()).thenReturn(false);

    var comment = accessPolicy.anonymiseIfRequired(originalComment);

    assertThat(comment).usingRecursiveComparison().isEqualTo(originalComment);
  }

  @Test
  void shouldAlwaysSuppressCommentsIfIncluded() {
    var originalComment = CommentMother.supressedComment();
    when(accessRightsCalculator.calculateAccessRights()).thenReturn(accessRights);
    when(accessRights.isIncludeSuppressed()).thenReturn(true);

    var comment = accessPolicy.process(originalComment).orElseThrow();

    assertThat(comment.getSubmittedComment()).isEqualTo("Comment suppressed");
    assertThat(comment)
        .usingRecursiveComparison()
        .ignoringFields("submittedComment")
        .isEqualTo(originalComment);
  }

  @Test
  void shouldNotFilterOrAnonymiseCommentsIfNotRequiredByAccessRights() {
    var originalComments =
        List.of(
            CommentMother.withAccount(AccountMother.tacAccount()),
            CommentMother.deletedComment(),
            CommentMother.privateComment(),
            CommentMother.anonymousComment());
    when(accessRightsCalculator.calculateAccessRights()).thenReturn(accessRights);
    when(accessRights.isIncludeTac()).thenReturn(true);
    when(accessRights.isIncludeDeleted()).thenReturn(true);
    when(accessRights.isIncludePrivate()).thenReturn(true);
    when(accessRights.isAnonymise()).thenReturn(false);

    var comments = accessPolicy.process(originalComments);

    assertThat(comments).containsExactlyElementsOf(originalComments);
  }

  @Test
  void shouldFilterTacCommentsIfRequiredByAccessRights() {
    var originalComments = List.of(CommentMother.withAccount(AccountMother.tacAccount()));
    when(accessRightsCalculator.calculateAccessRights()).thenReturn(accessRights);
    when(accessRights.isIncludeTac()).thenReturn(false);

    var comments = accessPolicy.process(originalComments);

    assertThat(comments).isEmpty();
  }

  @Test
  void shouldFilterSuppressedCommentsIfRequiredByAccessRights() {
    var originalComments = List.of(CommentMother.supressedComment());
    when(accessRightsCalculator.calculateAccessRights()).thenReturn(accessRights);
    when(accessRights.isIncludeSuppressed()).thenReturn(false);

    var comments = accessPolicy.process(originalComments);

    assertThat(comments).isEmpty();
  }

  @Test
  void shouldFilterDeletedCommentsIfRequiredByAccessRights() {
    var originalComments = List.of(CommentMother.deletedComment());
    when(accessRightsCalculator.calculateAccessRights()).thenReturn(accessRights);
    when(accessRights.isIncludeDeleted()).thenReturn(false);

    var comments = accessPolicy.process(originalComments);

    assertThat(comments).isEmpty();
  }

  @Test
  void shouldFilterPrivateCommentsIfRequiredByAccessRights() {
    var originalComments = List.of(CommentMother.privateComment());
    when(accessRightsCalculator.calculateAccessRights()).thenReturn(accessRights);
    when(accessRights.isIncludePrivate()).thenReturn(false);

    var comments = accessPolicy.process(originalComments);

    assertThat(comments).isEmpty();
  }
}

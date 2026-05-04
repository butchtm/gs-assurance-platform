package org.goldstandard.assurance.api.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.goldstandard.assurance.api.entity.lookup.AccountType;
import org.goldstandard.assurance.api.entity.lookup.DeletionReason;
import org.goldstandard.assurance.api.entity.lookup.SuppressionReason;
import org.goldstandard.assurance.api.fixtures.CommentMother;
import org.junit.jupiter.api.Test;

class CommentTest {

  private static final String DELETED_TEXT = "Comment deleted";

  @Test
  void shouldSuppressSubmittedComment() {
    var comment = CommentMother.build();

    var suppressed = comment.suppress();

    assertThat(suppressed.isSuppressed()).isTrue();
    assertThat(suppressed.getSubmittedComment()).isEqualTo("Comment suppressed");
    assertThat(suppressed)
        .usingRecursiveComparison()
        .ignoringFields("suppressed", "submittedComment")
        .isEqualTo(comment);
  }

  @Test
  void shouldAnonymiseUploadedByEmailAndName() {
    var comment = CommentMother.build();

    var anonymised = comment.anonymise();

    assertThat(anonymised.isAnonymous()).isTrue();
    assertThat(anonymised.getUploadedByEmail()).isEqualTo("Anonymous");
    assertThat(anonymised.getUploadedByName()).isEqualTo("Anonymous");
    assertThat(anonymised)
        .usingRecursiveComparison()
        .ignoringFields("anonymous", "uploadedByEmail", "uploadedByName")
        .isEqualTo(comment);
  }

  @Test
  void shouldSetCommentToDeleted() {
    var account = mock(Account.class);
    var comment = CommentMother.builder().account(account).build();
    var reasonId = 99;

    var deleted = comment.delete(reasonId);

    assertThat(deleted.isDeleted()).isTrue();
    assertThat(deleted.getDeletionReasonId()).isEqualTo(reasonId);
    assertThat(deleted.getSubmittedComment()).isEqualTo(DELETED_TEXT);
    assertThat(deleted.getUploadedByEmail()).isEqualTo(DELETED_TEXT);
    assertThat(deleted.getUploadedByName()).isEqualTo(DELETED_TEXT);
    assertThat(deleted.getAccount()).isNull();
    assertThat(deleted)
        .usingRecursiveComparison()
        .ignoringFields(
            "deleted",
            "deletionReason",
            "submittedComment",
            "uploadedByEmail",
            "uploadedByName",
            "account")
        .isEqualTo(comment);
  }

  @Test
  void shouldReturnTrueIfIsTacComment() {
    var accountType = mock(AccountType.class);
    when(accountType.isTac()).thenReturn(true);
    var comment = CommentMother.builder().account(givenAccountWithType(accountType)).build();

    var isTac = comment.isTacComment();

    assertThat(isTac).isTrue();
  }

  @Test
  void shouldReturnFalseIfNotTacComment() {
    var accountType = mock(AccountType.class);
    when(accountType.isTac()).thenReturn(false);
    var comment = CommentMother.builder().account(givenAccountWithType(accountType)).build();

    var isTac = comment.isTacComment();

    assertThat(isTac).isFalse();
  }

  @Test
  void shouldReturnIsPublicTrueIfPublic() {
    var comment = CommentMother.builder().publicAccess(true).build();

    var isPublic = comment.isPublic();

    assertThat(isPublic).isTrue();
  }

  @Test
  void shouldReturnIsPrivateTrueIfNotPublic() {
    var comment = CommentMother.builder().publicAccess(false).build();

    var isPrivate = comment.isPrivate();

    assertThat(isPrivate).isTrue();
  }

  @Test
  void shouldReturnNullDeletionReasonIdIfNotSet() {
    var comment = CommentMother.build();

    var id = comment.getDeletionReasonId();

    assertThat(id).isNull();
  }

  @Test
  void shouldReturnDeletionReasonIdIfSet() {
    var expectedId = 1;
    var comment = CommentMother.builder().deletionReason(new DeletionReason(expectedId)).build();

    var id = comment.getDeletionReasonId();

    assertThat(id).isEqualTo(expectedId);
  }

  @Test
  void shouldReturnNullSuppressionReasonIdIfNotSet() {
    var comment = CommentMother.build();

    var id = comment.getSuppressionReasonId();

    assertThat(id).isNull();
  }

  @Test
  void shouldReturnSuppressionReasonIdIfSet() {
    var expectedId = 2;
    var comment =
        CommentMother.builder().suppressionReason(new SuppressionReason(expectedId)).build();

    var id = comment.getSuppressionReasonId();

    assertThat(id).isEqualTo(expectedId);
  }

  private Account givenAccountWithType(AccountType type) {
    var account = mock(Account.class);
    when(account.getAccountType()).thenReturn(type);
    return account;
  }
}

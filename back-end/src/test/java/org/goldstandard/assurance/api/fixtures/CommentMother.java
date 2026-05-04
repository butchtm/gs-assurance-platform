package org.goldstandard.assurance.api.fixtures;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Comment;
import org.goldstandard.assurance.api.entity.lookup.DeletionReason;
import org.goldstandard.assurance.api.entity.lookup.SuppressionReason;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMother {

  public static Comment build() {
    return builder().build();
  }

  public static Comment publicComment() {
    return builder()
        .id(2)
        .publicAccess(true)
        .submittedComment("my public comment")
        .uploadedByName("joe public")
        .uploadedByEmail("joe.public@caci.co.uk")
        .build();
  }

  public static Comment deletedComment() {
    return builder()
        .id(3)
        .deleted(true)
        .deletionReason(new DeletionReason(99))
        .submittedComment("my deleted comment")
        .uploadedByName("joe deleted")
        .uploadedByEmail("joe.deleted@caci.co.uk")
        .build();
  }

  public static Comment supressedComment() {
    return builder()
        .id(4)
        .suppressed(true)
        .suppressionReason(new SuppressionReason(99))
        .submittedComment("my suppressed comment")
        .uploadedByName("joe suppressed")
        .uploadedByEmail("joe.suppressed@caci.co.uk")
        .build();
  }

  public static Comment addressedComment() {
    return builder()
        .id(5)
        .addressed(true)
        .submittedComment("my addressed comment")
        .uploadedByName("joe addressed")
        .uploadedByEmail("joe.addressed@caci.co.uk")
        .build();
  }

  public static Comment anonymousComment() {
    return builder()
        .id(6)
        .anonymous(true)
        .submittedComment("my anonymous comment")
        .uploadedByName("joe anonymous")
        .uploadedByEmail("joe.anonymous@caci.co.uk")
        .build();
  }

  public static Comment withAccount(Account account) {
    var accountName = account.getName();
    return builder()
        .id(7)
        .account(account)
        .submittedComment(String.format("my %s user comment", accountName))
        .uploadedByName(String.format("%s uploaded name", accountName))
        .uploadedByEmail(String.format("%s.uploaded.email@caci.co.uk", accountName))
        .build();
  }

  public static Comment privateComment() {
    return builder()
        .id(8)
        .publicAccess(false)
        .submittedComment("my private comment")
        .uploadedByName("joe private")
        .uploadedByEmail("joe.private@caci.co.uk")
        .build();
  }

  public static Comment.CommentBuilder builder() {
    return Comment.builder()
        .id(1)
        .requestId(10)
        .submittedComment("test comment")
        .uploadedByName("mr test caci")
        .uploadedByEmail("test@caci.co.uk")
        .uploadedDate(ZonedDateTimeMother.fixed())
        .publicAccess(true)
        .anonymous(false)
        .addressed(false)
        .suppressed(false)
        .suppressionReason(null)
        .deleted(false)
        .deletionReason(null);
  }
}

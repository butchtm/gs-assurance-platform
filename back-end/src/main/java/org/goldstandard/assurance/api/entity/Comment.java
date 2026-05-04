package org.goldstandard.assurance.api.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.AccountType;
import org.goldstandard.assurance.api.entity.lookup.DeletionReason;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;
import org.goldstandard.assurance.api.entity.lookup.SuppressionReason;
import org.goldstandard.assurance.api.entity.pii.PiiMetadata;

@Entity
@Getter
@Builder(toBuilder = true)
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class Comment {

  private static final String ANONYMOUS_TEXT = "Anonymous";
  private static final String DELETED_TEXT = "Comment deleted";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private final Integer id;

  private final Integer requestId;
  private final String submittedComment;
  private final String uploadedByName;
  private final String uploadedByEmail;
  @Nullable @ManyToOne private final Account account;
  private final ZonedDateTime uploadedDate;
  private final boolean publicAccess;
  private final boolean anonymous;
  private final boolean addressed;
  private final boolean suppressed;
  @ManyToOne private final SuppressionReason suppressionReason;
  private final boolean deleted;
  @ManyToOne private final DeletionReason deletionReason;

  @Nullable
  @OneToOne(mappedBy = "comment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private final PiiMetadata piiMetadata;

  public Integer getDeletionReasonId() {
    return Optional.ofNullable(deletionReason).map(ReferenceDataType::getId).orElse(null);
  }

  public Integer getSuppressionReasonId() {
    return Optional.ofNullable(suppressionReason).map(ReferenceDataType::getId).orElse(null);
  }

  public boolean isTacComment() {
    return Optional.ofNullable(account)
        .map(Account::getAccountType)
        .map(AccountType::isTac)
        .orElse(false);
  }

  public boolean isPublic() {
    return publicAccess;
  }

  public boolean isPrivate() {
    return !isPublic();
  }

  public Comment anonymise() {
    return toBuilder()
        .anonymous(true)
        .uploadedByEmail(ANONYMOUS_TEXT)
        .uploadedByName(ANONYMOUS_TEXT)
        .build();
  }

  public Comment suppress() {
    return toBuilder().suppressed(true).submittedComment("Comment suppressed").build();
  }

  public Comment delete(Integer reasonId) {
    return toBuilder()
        .deleted(true)
        .deletionReason(new DeletionReason(reasonId))
        .submittedComment(DELETED_TEXT)
        .uploadedByEmail(DELETED_TEXT)
        .uploadedByName(DELETED_TEXT)
        .account(null)
        .build();
  }
}

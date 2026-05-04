package org.goldstandard.assurance.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.With;
import org.goldstandard.assurance.api.entity.lookup.AccountType;
import org.goldstandard.assurance.api.entity.lookup.DocumentType;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;

@Entity
@Getter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Builder(toBuilder = true)
public class Document {

  @Id private final UUID id;
  private final String containerName;
  private final String fileName;
  private final Long fileSize;
  private final byte[] hashValue;

  @With(AccessLevel.PRIVATE)
  private final Boolean access;

  private final Boolean category;
  private final String contentType;
  private final String documentHash;
  private final ZonedDateTime uploadedTimestamp;
  private final ZonedDateTime modifiedTimestamp;
  private final Boolean isLatest;
  private final Boolean isMissing;

  @ManyToOne
  @JoinColumn(name = "document_type_id")
  @With(AccessLevel.PRIVATE)
  private final DocumentType documentType;

  @JsonInclude @Transient @With private final Integer documentCategoryType;

  @ManyToOne
  @JoinColumn(name = "uploaded_by_user_id")
  private final User uploadedBy;

  public boolean isPublic() {
    return Boolean.TRUE.equals(access);
  }

  public boolean isPrivate() {
    return !isPublic();
  }

  public Integer getDocumentTypeId() {
    return Optional.ofNullable(documentType).map(ReferenceDataType::getId).orElse(null);
  }

  public Document makePrivate() {
    return withAccess(false);
  }

  public boolean hasOtherType() {
    return Optional.ofNullable(documentType).map(DocumentType::isOther).orElse(false);
  }

  public boolean uploadedByReviewer() {
    return getUploadedBy().map(User::hasReviewerAccount).orElse(false);
  }

  public Optional<AccountType> getUploadedByAccountType() {
    return Optional.ofNullable(uploadedBy)
        .flatMap(u -> Optional.ofNullable(u.getAccount()))
        .map(Account::getAccountType);
  }

  public Optional<User> getUploadedBy() {
    return Optional.ofNullable(uploadedBy);
  }
}

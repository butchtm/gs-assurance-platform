package org.goldstandard.assurance.api.entity.pii;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;
import org.goldstandard.assurance.api.entity.Comment;
import org.goldstandard.assurance.api.entity.lookup.PiiAction;
import org.goldstandard.assurance.api.enumeration.PiiType;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Entity
public class PiiMetadata {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private Integer projectId;
  private Integer requestId;

  @Enumerated(EnumType.STRING)
  private PiiType type;

  private String typeDetail;
  private Integer submittedByUserId;
  private Integer submittedByAccountId;
  private boolean actioned;
  private Integer actionedByUserId;
  private Integer actionedByAccountId;
  @ManyToOne private PiiAction action;
  private ZonedDateTime created;
  private ZonedDateTime updated;
  private UUID documentId;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "comment_id")
  private Comment comment;

  @With private boolean notificationSent;

  public boolean shouldSendNotification() {
    if (notificationSent) {
      return false;
    }
    return action.hasPiiOfConcern();
  }
}

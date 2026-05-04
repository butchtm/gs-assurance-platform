package org.goldstandard.assurance.api.entity.creditingperiod;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.With;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.enumeration.ProjectUpdateVerdict;

@Builder
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Data
@Entity
public class ProjectMethodologyUpdate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private final Integer id;

  @With @Transient private final String gsid;

  private final Integer projectId;
  private final Integer periodId;
  private final ZonedDateTime createdTimestamp;
  @ManyToOne private final Account createdBy;
  @ManyToOne private final Request request;

  @Enumerated(EnumType.STRING)
  private final ProjectUpdateVerdict verdict;

  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(
      name = "projectMethodologyUpdateId",
      referencedColumnName = "id",
      insertable = false,
      updatable = false)
  private final List<DbMethodologyUpdate> methodologyUpdates;

  public Integer getRequestId() {
    return Optional.ofNullable(request).map(Request::getId).orElse(null);
  }

  public Optional<Request> getRequest() {
    return Optional.ofNullable(request);
  }

  public Optional<Account> getCreatedBy() {
    return Optional.ofNullable(createdBy);
  }
}

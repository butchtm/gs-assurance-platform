package org.goldstandard.assurance.api.entity;

import static org.goldstandard.assurance.api.entity.lookup.InvoiceStatus.GENERATED_ID;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.goldstandard.assurance.api.entity.lookup.InvoiceStatus;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private BigDecimal amount;

  @ManyToOne(fetch = FetchType.EAGER)
  private InvoiceStatus status;

  private Boolean isArchived;
  private ZonedDateTime statusChangedAt;
  private String customerEmail;
  private ZonedDateTime lastManualCheck;

  public boolean hasGeneratedStatus() {
    return Objects.equals(status.getId(), GENERATED_ID);
  }

  public Optional<Integer> getStatusId() {
    return Optional.ofNullable(status).map(ReferenceDataType::getId);
  }

  public boolean hasStatusChanged(Invoice otherInvoice) {
    var thisStatusId = this.getStatusId().orElse(null);
    var otherStatusId = otherInvoice.getStatusId().orElse(null);
    return !Objects.equals(thisStatusId, otherStatusId);
  }
}

package org.goldstandard.assurance.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.Methodology;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Entity
@Table(name = "request_methodology", schema = "gs")
public class RequestMethodology {

  @EmbeddedId private final RequestMethodologyId id;

  @ManyToOne(fetch = FetchType.EAGER)
  @MapsId("requestId")
  @JoinColumn(
      name = "request_id",
      referencedColumnName = "id",
      insertable = false,
      updatable = false)
  private final Request request;

  @ManyToOne(fetch = FetchType.EAGER)
  @MapsId("methodologyId")
  @JoinColumn(name = "methodology_id", referencedColumnName = "id")
  private final Methodology methodology;

  @Column(name = "methodology_version", nullable = false)
  private final Double methodologyVersion;

  public RequestMethodology withRequest(Request request) {
    return toBuilder().request(request).id(toId(request)).build();
  }

  private RequestMethodologyId toId(Request request) {
    return RequestMethodologyId.builder()
        .requestId(request.getId())
        .methodologyId(Objects.requireNonNull(methodology).getId())
        .build();
  }
}

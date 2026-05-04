package org.goldstandard.assurance.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RequestStatusHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @JsonIgnore // To prevent recursive issues. In future refactor to use a DTO object.
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "requestId", nullable = false)
  private Request request;

  @ManyToOne private RequestStatus previousStatus;
  @ManyToOne private RequestStatus nextStatus;
  private String changedBy;
  private ZonedDateTime changedAt;
  @ManyToOne private User updatedByUser;
}

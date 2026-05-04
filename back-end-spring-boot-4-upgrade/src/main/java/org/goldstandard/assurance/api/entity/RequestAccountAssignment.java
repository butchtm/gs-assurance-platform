package org.goldstandard.assurance.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.AssignmentStatus;
import org.goldstandard.assurance.api.entity.lookup.AssignmentType;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Entity
public class RequestAccountAssignment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private final Integer id;

  private final Integer requestId;

  @ManyToOne private final Account account;

  @ManyToOne private final AssignmentType assignmentType;
  @ManyToOne private final AssignmentStatus assignmentStatus;
  private final ZonedDateTime createdTime;
  private final ZonedDateTime updatedTime;
  private final ZonedDateTime dueDate;
}

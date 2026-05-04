package org.goldstandard.assurance.api.entity.notification;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.goldstandard.assurance.api.entity.lookup.AssignmentStatus;
import org.goldstandard.assurance.api.entity.lookup.AssignmentType;

@Getter
@Setter
@Entity
public class AssignmentStatusNotification {
  @Id private Integer id;
  @ManyToOne private AssignmentStatus assignmentStatus;
  @ManyToOne private AssignmentType assignmentType;
  @ManyToOne private NotificationTemplate template;
}

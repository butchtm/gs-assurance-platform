package org.goldstandard.assurance.api.entity.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.StringJoiner;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class RequestStatusAssigneeNotification {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "requestStatusNotificationId",
      referencedColumnName = "id",
      insertable = false,
      updatable = false)
  private RequestStatusNotification requestStatusNotification;

  private Integer assignmentTypeId;

  @ManyToOne
  @JoinColumn(name = "assigneeTemplateName", referencedColumnName = "name")
  @JoinColumn(name = "assigneeTemplateVersion", referencedColumnName = "version")
  private NotificationTemplate assigneeTemplate;

  @Override
  public String toString() {
    return new StringJoiner(
            ", ", RequestStatusAssigneeNotification.class.getSimpleName() + "[", "]")
        .add("id=" + id)
        .add("assignmentTypeId=" + assignmentTypeId)
        .add("assigneeTemplate=" + assigneeTemplate)
        .toString();
  }
}

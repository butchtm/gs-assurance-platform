package org.goldstandard.assurance.api.entity.notification;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Getter
@Setter
@ToString
@Entity
public class RequestStatusNotification {
  @Id private Integer id;

  private Integer minRoundIndex;
  private Integer maxRoundIndex;

  @ManyToOne private RequestStatus fromStatus;
  @ManyToOne private RequestStatus toStatus;

  @ManyToOne
  @JoinColumn(name = "requestOwnerTemplateName", referencedColumnName = "name")
  @JoinColumn(name = "requestOwnerTemplateVersion", referencedColumnName = "version")
  private NotificationTemplate requestOwnerTemplate;

  @ManyToOne
  @JoinColumn(name = "armTeamTemplateName", referencedColumnName = "name")
  @JoinColumn(name = "armTeamTemplateVersion", referencedColumnName = "version")
  private NotificationTemplate armTeamTemplate;

  @ManyToOne
  @JoinColumn(name = "registryTeamTemplateName", referencedColumnName = "name")
  @JoinColumn(name = "registryTeamTemplateVersion", referencedColumnName = "version")
  private NotificationTemplate registryTeamTemplate;

  @ManyToOne
  @JoinColumn(name = "stakeholderTemplateName", referencedColumnName = "name")
  @JoinColumn(name = "stakeholderTemplateVersion", referencedColumnName = "version")
  private NotificationTemplate stakeholderTemplate;

  @ManyToOne
  @JoinColumn(name = "financeTeamTemplateName", referencedColumnName = "name")
  @JoinColumn(name = "financeTeamTemplateVersion", referencedColumnName = "version")
  private NotificationTemplate financeTeamTemplate;

  @ManyToOne
  @JoinColumn(name = "vvbTemplateName", referencedColumnName = "name")
  @JoinColumn(name = "vvbTemplateVersion", referencedColumnName = "version")
  private NotificationTemplate vvbTemplate;

  @Fetch(FetchMode.JOIN)
  @OneToMany(mappedBy = "requestStatusNotification")
  private List<RequestStatusAssigneeNotification> assigneeNotifications;
}

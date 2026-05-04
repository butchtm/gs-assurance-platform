package org.goldstandard.assurance.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StakeholderConsultation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne private ConsultationType consultationType;
  @ManyToOne private Project project;
  @ManyToOne private Request request;
  private ZonedDateTime startDate;
  private ZonedDateTime endDate;
  private boolean notificationSent;

  public boolean isPublic() {
    return !isPrivate();
  }

  public boolean isPrivate() {
    return consultationType.isTac();
  }
}

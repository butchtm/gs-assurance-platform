package org.goldstandard.assurance.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.goldstandard.assurance.api.entity.lookup.AccountType;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "name", "emailAddress", "isActive", "address"})
@ToString
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String name;
  private String emailAddress;
  private ZonedDateTime createdTime;
  private Boolean isActive;
  private String address;
  @ManyToOne private AccountType accountType;
  @OneToOne private Competencies competencies;

  public Account(Integer id) {
    this.id = id;
  }

  public boolean hasReviewerType() {
    return accountType.isReviewer();
  }
}

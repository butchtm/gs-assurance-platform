package org.goldstandard.assurance.api.entity;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
// User is a protected keyword in Postgres so we have to escape the table name
@Table(name = "\"user\"")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String userName;
  private String emailAddress;
  private Boolean isActive;
  private UUID idpUserId;
  private String givenName;
  private String familyName;
  private Boolean emailVerified;
  private List<String> groups;
  @ManyToOne private Account account;

  public User(Integer id) {
    this.id = id;
  }

  public String getFullName() {
    return Stream.of(givenName, familyName)
        .filter(StringUtils::isNotEmpty)
        .collect(Collectors.joining(" "));
  }

  public void update(UpsertUserRequest request) {
    setEmailAddress(request.getEmail());
    setFamilyName(request.getLastName());
    setGivenName(request.getFirstName());
    setUserName(request.getUserName());
    setGroups(new ArrayList<>(request.getGroups()));
  }

  public boolean isActive() {
    return Boolean.TRUE.equals(isActive);
  }

  public boolean hasReviewerAccount() {
    return Optional.ofNullable(account).map(Account::hasReviewerType).orElse(false);
  }
}

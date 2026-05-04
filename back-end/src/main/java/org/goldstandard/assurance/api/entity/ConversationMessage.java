package org.goldstandard.assurance.api.entity;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Entity
public class ConversationMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private final Integer id;

  private final Integer conversationId;
  private final String content;
  @ManyToOne private final User sentByUser;
  private final ZonedDateTime sentAt;

  @OneToMany(mappedBy = "message", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private final Collection<ConversationMessageReceipt> receipts;

  public Collection<String> getReadByUsernames() {
    return getReadByUsers()
        .map(User::getUserName)
        .filter(username -> !wasSentBy(username))
        .distinct()
        .toList();
  }

  public Collection<Account> getReadByAccounts() {
    return getReadByUsers()
        .map(User::getAccount)
        .filter(Objects::nonNull)
        .filter(account -> !wasSentBy(account))
        .collect(
            collectingAndThen(
                toCollection(() -> new TreeSet<>(comparingInt(Account::getId))), ArrayList::new));
  }

  private Stream<User> getReadByUsers() {
    if (CollectionUtils.isEmpty(receipts)) {
      return Stream.empty();
    }
    return receipts.stream().map(ConversationMessageReceipt::getReadByUser);
  }

  private boolean wasSentBy(String username) {
    if (Objects.isNull(sentByUser)) {
      return false;
    }
    return sentByUser.getUserName().equals(username);
  }

  private boolean wasSentBy(Account account) {
    if (Objects.isNull(sentByUser)) {
      return false;
    }
    return Optional.ofNullable(sentByUser.getAccount())
        .map(a -> a.getId().equals(account.getId()))
        .orElse(false);
  }
}

package org.goldstandard.assurance.api.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.goldstandard.assurance.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository
    extends JpaRepository<User, Integer>,
        ListPagingAndSortingRepository<User, Integer>,
        JpaSpecificationExecutor<User> {

  List<User> findAllByAccountId(Integer accountId);

  List<User> findAllByAccountIdIsIn(Collection<Integer> accountIds);

  Optional<User> findByIdpUserId(UUID idpUserId);

  Optional<User> findByUserName(String userName);

  long countByIsActive(Boolean isActive);

  default Map<Integer, Integer> allUserCountsByAccountId(Collection<Integer> accountIds) {
    return countUsersByAccountIds(accountIds).stream()
        .collect(Collectors.toMap(row -> (Integer) row[0], row -> ((Long) row[1]).intValue()));
  }

  @Query(
      """
        select u.account.id, count(u)
        from User u
        where u.account.id in :accountIds
        group by u.account.id
    """)
  List<Object[]> countUsersByAccountIds(@Param("accountIds") Collection<Integer> accountIds);
}

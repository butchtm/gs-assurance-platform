package org.goldstandard.assurance.api.repository;

import java.util.List;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.lookup.AccountType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository
    extends PagingAndSortingRepository<Account, Integer>,
        CrudRepository<Account, Integer>,
        JpaSpecificationExecutor<Account> {

  List<Account> findAllAccountsByAccountType(AccountType accountType);
}

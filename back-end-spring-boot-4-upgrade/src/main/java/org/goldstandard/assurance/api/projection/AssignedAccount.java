package org.goldstandard.assurance.api.projection;

import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.RequestAccountAssignment;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = {RequestAccountAssignment.class})
public interface AssignedAccount {
  Integer getId();

  Account getAccount();
}

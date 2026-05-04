package org.goldstandard.assurance.api.converter;

import java.util.Collection;
import org.goldstandard.assurance.api.dto.UserBatchErrorDTO;
import org.goldstandard.assurance.api.entity.UserBatchError;

public class UserBatchErrorConverter {

  public Collection<UserBatchErrorDTO> toApiErrors(Collection<UserBatchError> errors) {
    return errors.stream().map(this::toApiError).toList();
  }

  public UserBatchErrorDTO toApiError(UserBatchError error) {
    return UserBatchErrorDTO.builder()
        .userName(error.getUserName())
        .message(error.getMessage())
        .account(error.getAccount())
        .build();
  }
}

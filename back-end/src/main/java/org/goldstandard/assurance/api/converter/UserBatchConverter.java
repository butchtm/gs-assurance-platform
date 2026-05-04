package org.goldstandard.assurance.api.converter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.UserBatchDTO;
import org.goldstandard.assurance.api.entity.UpsertUserRequest;
import org.goldstandard.assurance.api.entity.UserBatch;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserBatchConverter {

  private final CsvUserConverter csvMapper;
  private final UserBatchErrorConverter errorMapper;

  public UserBatchConverter() {
    this(new CsvUserConverter(), new UserBatchErrorConverter());
  }

  public Collection<UpsertUserRequest> toCreateUserRequests(InputStream inputStream)
      throws IOException {
    return csvMapper.toCreateUserRequests(inputStream);
  }

  public UserBatchDTO toApiUserBatch(UserBatch batch) {
    return UserBatchDTO.builder()
        .id(batch.getId())
        .errors(errorMapper.toApiErrors(batch.getErrors()))
        .createdAt(batch.getCreatedAt())
        .updatedAt(batch.getUpdatedAt())
        .count(batch.getCount())
        .successCount(batch.getSuccessCount())
        .complete(batch.isComplete())
        .build();
  }
}

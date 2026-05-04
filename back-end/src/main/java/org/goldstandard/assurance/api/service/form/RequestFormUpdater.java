package org.goldstandard.assurance.api.service.form;

import static java.time.ZonedDateTime.now;

import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.RequestFormUpdateDTO;
import org.goldstandard.assurance.api.entity.RequestForm;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestFormUpdater {

  private final RequestFormValidator validator;
  private final Clock clock;

  public RequestForm update(RequestForm existing, RequestFormUpdateDTO dto) {
    validator.validateCanBeUpdated(existing);
    return existing.withData(dto.getData(), now(clock));
  }
}

package org.goldstandard.assurance.api.service.form;

import static java.time.ZonedDateTime.now;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.RequestFormSubmitDTO;
import org.goldstandard.assurance.api.entity.RequestForm;
import org.goldstandard.assurance.api.entity.RequestFormStatus;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestFormSubmitter {

  private final AuthenticatedUserSupplier userSupplier;
  private final RequestFormValidator validator;
  private final Clock clock;

  public RequestForm submit(RequestForm existing, RequestFormSubmitDTO dto) {
    validator.validateCanBeSubmitted(existing);
    var updated = applyAllChanges(existing, dto);
    validator.validateDataAgainstSchema(updated);
    return updated;
  }

  private RequestForm applyAllChanges(RequestForm existing, RequestFormSubmitDTO dto) {
    var now = now(clock);
    var builder = applySubmissionChanges(existing, now);
    var data = dto.getData();
    if (isEmpty(data)) {
      return builder.build();
    }
    return builder.data(data).updatedAt(now).build();
  }

  private RequestForm.RequestFormBuilder applySubmissionChanges(
      RequestForm existing, ZonedDateTime now) {
    return existing.toBuilder()
        .status(RequestFormStatus.submitted())
        .submittedAt(now)
        .submittedBy(userSupplier.forceGetAuthenticatedUser());
  }

  private boolean isEmpty(JsonNode node) {
    return Objects.isNull(node) || node.isEmpty();
  }
}

package org.goldstandard.assurance.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import org.goldstandard.assurance.api.dto.RequestDTO;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.facade.RequestFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class RequestControllerTest {

  private static final Integer REQUEST_ID = 5352;

  @InjectMocks RequestController controller;

  @Mock RequestFacade requestFacade;

  @Test
  void getRequestThrowsNotFound() {
    var expectedError = new NotFoundException("test");
    when(requestFacade.getRequestDetailById(REQUEST_ID)).thenThrow(expectedError);

    Throwable error = catchThrowable(() -> controller.getRequest(REQUEST_ID));

    assertThat(error).isEqualTo(expectedError);
  }

  @Test
  void getRequestReturnsRequest() {
    var detail = RequestDTO.builder().id(REQUEST_ID).build();
    when(requestFacade.getRequestDetailById(REQUEST_ID)).thenReturn(detail);

    var response = controller.getRequest(REQUEST_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(detail);
  }
}

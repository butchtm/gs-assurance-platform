package org.goldstandard.assurance.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.goldstandard.assurance.api.converter.UserBatchConverter;
import org.goldstandard.assurance.api.exception.BatchRequestException;
import org.goldstandard.assurance.api.service.user.UserBatchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.dataformat.csv.CsvReadException;

@ExtendWith(MockitoExtension.class)
class UserBatchControllerTest {

  @InjectMocks UserBatchController controller;

  @Mock UserBatchService userBatchService;

  @Mock UserBatchConverter batchMapper;

  @Mock MultipartFile file;

  @Test
  void csvReadErrorWhenReadingFileThrowsBatchRequestException() {
    when(batchMapper.toCreateUserRequests(any())).thenThrow(new CsvReadException(null, null, null));

    Throwable error = catchThrowable(() -> controller.create(file));

    assertThat(error)
        .isInstanceOf(BatchRequestException.class)
        .hasMessage("Invalid csv file uploaded");
  }
}

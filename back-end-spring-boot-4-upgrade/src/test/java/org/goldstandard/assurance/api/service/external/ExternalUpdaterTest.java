package org.goldstandard.assurance.api.service.external;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.goldstandard.assurance.api.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExternalUpdaterTest {

  @InjectMocks ExternalUpdater updater;

  @Mock ExternalUpdateCommand command;

  @Mock ExternalSystemProjectUpdatePersistor persistor;

  @Test
  void shouldExecuteExternalUpdateCommand() {
    var projectId = 1;

    updater.update(projectId);

    var captor = ArgumentCaptor.forClass(Integer.class);
    verify(command).execute(captor.capture());
    assertThat(captor.getValue()).isEqualTo(projectId);
  }

  @Test
  void shouldDeleteAnyExistingProjectUpdatesIfCommandExecutesSuccessfully() {
    var projectId = 1;

    updater.update(projectId);

    var captor = ArgumentCaptor.forClass(Integer.class);
    verify(persistor).deleteAnyExistingUpdates(captor.capture());
    assertThat(captor.getValue()).isEqualTo(projectId);
  }

  @Test
  void shouldPersistUpdateEventIfCallFails() {
    var projectId = 2;
    var expectedError = new ExternalApiClientException(new RuntimeException("test-error"));
    doThrow(expectedError).when(command).execute(projectId);

    var error = catchThrowable(() -> updater.update(projectId));

    assertThat(error).isEqualTo(expectedError);
    verify(persistor).persistUpdateIfRequired(projectId);
  }

  @Test
  void shouldDeleteAnyExistingProjectUpdatesIfCommandFailsBecauseProjectIsNotFound() {
    var projectId = 1;
    var expectedError = new NotFoundException("project %d not found", projectId);
    doThrow(expectedError).when(command).execute(projectId);

    var error = catchThrowable(() -> updater.update(projectId));

    assertThat(error).isEqualTo(expectedError);
    var captor = ArgumentCaptor.forClass(Integer.class);
    verify(persistor).deleteAnyExistingUpdates(captor.capture());
    assertThat(captor.getValue()).isEqualTo(projectId);
  }

  @Test
  void shouldPersistUpdateEvent() {
    var projectId = 2;

    updater.persistUpdateIfRequired(projectId);

    verify(persistor).persistUpdateIfRequired(projectId);
  }
}

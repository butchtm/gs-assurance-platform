package org.goldstandard.assurance.api.service.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.goldstandard.assurance.api.dto.DocumentUpdate;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.goldstandard.assurance.api.service.PermissionsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentUpdateValidatorTest {

  @InjectMocks DocumentUpdateValidator validator;

  @Mock PermissionsService permissionsService;

  @Mock Document document;

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  @NullSource
  void shouldDoNothingIfPublicAccessIsUnchanged(Boolean publicAccess) {
    when(document.getAccess()).thenReturn(publicAccess);
    DocumentUpdate update = updateWithPublicAccess(publicAccess);

    ThrowingCallable call = () -> validator.validateUpdateAllowed(document, update);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldDoNothingIfPublicAccessIsChangedByAnyOtherUserRole() {
    when(document.getAccess()).thenReturn(false);
    DocumentUpdate update = updateWithPublicAccess(true);
    when(permissionsService.isReviewerOrVVBOrRegistry()).thenReturn(false);

    ThrowingCallable call = () -> validator.validateUpdateAllowed(document, update);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldThrowExceptionIfPublicAccessChangedOnReviewerUploadedDocument() {
    when(document.getAccess()).thenReturn(false);
    when(document.uploadedByReviewer()).thenReturn(true);
    DocumentUpdate update = updateWithPublicAccess(true);

    Throwable error = catchThrowable(() -> validator.validateUpdateAllowed(document, update));

    assertThat(error).isInstanceOf(PermissionException.class);
  }

  @Test
  void shouldThrowExceptionIfPublicAccessChangedByReviewerOrVVB() {
    when(document.getAccess()).thenReturn(false);
    DocumentUpdate update = updateWithPublicAccess(true);
    when(permissionsService.isReviewerOrVVBOrRegistry()).thenReturn(true);

    Throwable error = catchThrowable(() -> validator.validateUpdateAllowed(document, update));

    assertThat(error).isInstanceOf(PermissionException.class);
  }

  private static DocumentUpdate updateWithPublicAccess(Boolean access) {
    return new DocumentUpdate(access, false, 1, false);
  }
}

package org.goldstandard.assurance.api.service.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.goldstandard.assurance.api.entity.lookup.DocumentType;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.goldstandard.assurance.api.repository.DocumentTypeRepository;
import org.goldstandard.assurance.api.service.PermissionsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentTypeUploadValidatorTest {

  @InjectMocks DocumentTypeUploadValidator validator;

  @Mock PermissionsService permissionsService;
  @Mock DocumentTypeRepository repository;

  @Mock DocumentType documentType;

  @BeforeEach
  void setUp() {
    when(documentType.getId()).thenReturn(1);
  }

  @Test
  void shouldThrowExceptionIfReviewUserAttemptsToUploadNonReviewFeedbackFormDocument() {
    when(permissionsService.isReviewer()).thenReturn(true);
    when(repository.forceFindById(documentType.getId())).thenReturn(documentType);
    when(documentType.isReviewFeedbackForm()).thenReturn(false);
    when(documentType.getDescription()).thenReturn("mock doc type");

    var error = catchThrowable(() -> validator.validateUserCanUpload(documentType.getId()));

    assertThat(error)
        .isInstanceOf(PermissionException.class)
        .hasMessage("reviewer not permitted to upload mock doc type documents");
  }

  @Test
  void shouldDoNothingIfReviewUserAttemptsToUploadReviewFeedbackForm() {
    when(permissionsService.isReviewer()).thenReturn(true);
    when(repository.forceFindById(documentType.getId())).thenReturn(documentType);
    when(documentType.isReviewFeedbackForm()).thenReturn(true);

    ThrowingCallable call = () -> validator.validateUserCanUpload(documentType.getId());

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldDoNothingIfNonReviewerUser() {
    when(permissionsService.isReviewer()).thenReturn(false);

    ThrowingCallable call = () -> validator.validateUserCanUpload(documentType.getId());

    assertThatCode(call).doesNotThrowAnyException();
    verifyNoInteractions(repository);
  }
}

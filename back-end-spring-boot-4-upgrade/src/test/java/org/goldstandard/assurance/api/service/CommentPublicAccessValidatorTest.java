package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.goldstandard.assurance.api.dto.CommentUpdateDTO;
import org.goldstandard.assurance.api.entity.Comment;
import org.goldstandard.assurance.api.entity.lookup.PiiAction;
import org.goldstandard.assurance.api.entity.pii.PiiMetadata;
import org.goldstandard.assurance.api.exception.BadRequestException;
import org.junit.jupiter.api.Test;

class CommentPublicAccessValidatorTest {

  private final CommentPublicAccessValidator validator = new CommentPublicAccessValidator();

  @Test
  void shouldDoNothingIfAccessIsSetToPrivate() {
    var dto = CommentUpdateDTO.builder().publicAccess(false).build();
    var comment = Comment.builder().id(1).build();

    ThrowingCallable call = () -> validator.validatePublicAccess(comment, dto);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldDoNothingIfAccessIsNotUpdatedAndAlreadySetToPrivate() {
    var dto = CommentUpdateDTO.builder().build();
    var comment = Comment.builder().id(2).publicAccess(false).build();

    ThrowingCallable call = () -> validator.validatePublicAccess(comment, dto);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldDoNothingIfAccessIsSetToPublicAndNoPiiMetadataForComment() {
    var dto = CommentUpdateDTO.builder().publicAccess(true).build();
    var comment = Comment.builder().id(3).publicAccess(false).build();

    ThrowingCallable call = () -> validator.validatePublicAccess(comment, dto);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldDoNothingIfAccessIsSetToPublicAndUpdatedPiiActionIsAcceptableForPublicVisibility() {
    var piiAction = buildPiiActionWithPublicVisibilityAcceptable(true);
    var dto = CommentUpdateDTO.builder().publicAccess(true).piiAction(piiAction).build();
    var comment = Comment.builder().id(4).publicAccess(false).build();

    ThrowingCallable call = () -> validator.validatePublicAccess(comment, dto);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldDoNothingIfAccessIsSetToPublicAndExistingPiiActionIsAcceptableForPublicVisibility() {
    var dto = CommentUpdateDTO.builder().publicAccess(true).build();
    var piiAction = buildPiiActionWithPublicVisibilityAcceptable(true);
    var comment =
        Comment.builder()
            .id(5)
            .publicAccess(false)
            .piiMetadata(buildPiiMetadata(piiAction))
            .build();

    ThrowingCallable call = () -> validator.validatePublicAccess(comment, dto);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void
      shouldThrowErrorIfAccessIsSetToPublicAndUpdatedPiiActionIsNotAcceptableForPublicVisibility() {
    var piiAction = buildPiiActionWithPublicVisibilityAcceptable(false);
    var dto = CommentUpdateDTO.builder().publicAccess(true).piiAction(piiAction).build();
    var comment =
        Comment.builder().id(6).publicAccess(false).piiMetadata(new PiiMetadata()).build();

    var error = catchThrowable(() -> validator.validatePublicAccess(comment, dto));

    assertThat(error).isInstanceOf(BadRequestException.class);
  }

  @Test
  void
      shouldThrowErrorIfAccessIsSetToPublicAndExistingPiiActionIsNotAcceptableForPublicVisibility() {
    var dto = CommentUpdateDTO.builder().publicAccess(true).build();
    var piiAction = buildPiiActionWithPublicVisibilityAcceptable(false);
    var comment =
        Comment.builder()
            .id(7)
            .publicAccess(false)
            .piiMetadata(buildPiiMetadata(piiAction))
            .build();

    var error = catchThrowable(() -> validator.validatePublicAccess(comment, dto));

    assertThat(error).isInstanceOf(BadRequestException.class);
  }

  private PiiMetadata buildPiiMetadata(PiiAction action) {
    var metadata = mock(PiiMetadata.class);
    when(metadata.getAction()).thenReturn(action);
    return metadata;
  }

  private PiiAction buildPiiActionWithPublicVisibilityAcceptable(boolean acceptable) {
    var action = mock(PiiAction.class);
    when(action.publicVisibilityAcceptable()).thenReturn(acceptable);
    return action;
  }
}

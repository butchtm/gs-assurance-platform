package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.service.CommentService.DELETED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.goldstandard.assurance.api.dto.CommentCreateDTO;
import org.goldstandard.assurance.api.dto.CommentDelete;
import org.goldstandard.assurance.api.dto.CommentUpdateDTO;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Comment;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.exception.BadRequestException;
import org.goldstandard.assurance.api.exception.DataException;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.fixtures.AccountMother;
import org.goldstandard.assurance.api.fixtures.CommentMother;
import org.goldstandard.assurance.api.repository.CommentRepository;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.goldstandard.assurance.api.service.notification.CommentNotificationService;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

  public static final int REQUEST_ID = 1;
  public static final int ACCOUNT_ID = 0;
  public static final String TEST_NAME = "testName";
  public static final String TEST_EMAIL = "testEmail";
  public static final int ID = 5;

  @InjectMocks CommentService commentService;

  @Mock CommentRepository commentRepository;

  @Mock AccountFinder accountFinder;

  @Mock RequestFinder requestFinder;

  @Mock AuthenticatedUserSupplier authenticatedUserSupplier;

  @Mock CommentAccessPolicy accessPolicy;

  @Mock CommentNotificationService commentNotificationService;

  @Mock CommentPublicAccessValidator publicAccessValidator;

  @Mock CommentTransaction transaction;

  @Mock CommentFinder commentFinder;

  @Captor ArgumentCaptor<Comment> commentArgumentCaptor;

  @Mock Request request;

  @Test
  void shouldReturnComments() {
    var expectedComments = buildComments();
    when(requestFinder.getParentRequestIdOrRequestId(REQUEST_ID)).thenReturn(REQUEST_ID);
    when(commentFinder.getCommentsByRequestId(REQUEST_ID)).thenReturn(expectedComments);

    var comments = commentService.getCommentsByRequestId(REQUEST_ID);

    assertThat(comments).containsExactlyElementsOf(expectedComments);
  }

  @Test
  void shouldReturnCommentsForParentIfChildOfBatchRequest() {
    var expectedComments = buildComments();
    var parentRequestId = 2;
    when(requestFinder.getParentRequestIdOrRequestId(REQUEST_ID)).thenReturn(parentRequestId);
    when(commentFinder.getCommentsByRequestId(parentRequestId)).thenReturn(expectedComments);

    var comments = commentService.getCommentsByRequestId(REQUEST_ID);

    assertThat(comments).containsExactlyElementsOf(expectedComments);
  }

  @Test
  void saveNewCommentWithAccountId() {
    Comment comment = buildComment();
    when(accountFinder.forceGetAccountById(ACCOUNT_ID)).thenReturn(mock(Account.class));
    String expectedEmail = "joe.bloggs@caci.co.uk";
    when(authenticatedUserSupplier.getAuthenticatedUserEmailAddress())
        .thenReturn(Optional.of(expectedEmail));
    when(request.isBatchChild()).thenReturn(false);
    when(requestFinder.getRequestById(REQUEST_ID)).thenReturn(request);
    CommentCreateDTO create = buildCommentCreateDTO(null, null, ACCOUNT_ID);
    when(transaction.createComment(any(), eq(create.getTextAnalysisResults()))).thenReturn(comment);

    commentService.saveNewComment(create);

    verify(transaction)
        .createComment(commentArgumentCaptor.capture(), eq(create.getTextAnalysisResults()));
    var commentCapture = commentArgumentCaptor.getValue();

    assertEquals(create.getComment(), commentCapture.getSubmittedComment());
    assertEquals(expectedEmail, commentCapture.getUploadedByEmail());
    if (commentCapture.getAccount() != null) {
      assertEquals(create.getAccountId(), commentCapture.getAccount().getId());
    }
    assertTrue(commentCapture.isAnonymous());
    assertFalse(commentCapture.isAddressed());
    assertFalse(commentCapture.isPublicAccess());
  }

  @Test
  void saveNewCommentWithoutAccountId() {
    Comment comment = buildComment();
    when(request.isBatchChild()).thenReturn(false);
    when(requestFinder.getRequestById(REQUEST_ID)).thenReturn(request);
    CommentCreateDTO create = buildCommentCreateDTO(TEST_NAME, TEST_EMAIL, null);
    when(transaction.createComment(any(), eq(create.getTextAnalysisResults()))).thenReturn(comment);

    commentService.saveNewComment(create);

    verify(transaction)
        .createComment(commentArgumentCaptor.capture(), eq(create.getTextAnalysisResults()));
    var commentCapture = commentArgumentCaptor.getValue();

    assertEquals(create.getComment(), commentCapture.getSubmittedComment());
    assertEquals(TEST_EMAIL, commentCapture.getUploadedByEmail());
    assertEquals(TEST_NAME, commentCapture.getUploadedByName());
    assertNull(commentCapture.getAccount());
    assertTrue(commentCapture.isAnonymous());
    assertFalse(commentCapture.isAddressed());
    assertFalse(commentCapture.isPublicAccess());
  }

  @Test
  void saveNewCommentAnonymous() {
    Comment comment = buildComment().toBuilder().anonymous(true).build();
    when(request.isBatchChild()).thenReturn(false);
    when(requestFinder.getRequestById(REQUEST_ID)).thenReturn(request);
    CommentCreateDTO create = buildCommentCreateDTO(TEST_NAME, TEST_EMAIL, null);
    when(transaction.createComment(any(), eq(create.getTextAnalysisResults()))).thenReturn(comment);
    var anonymisedComment = mock(Comment.class);
    when(accessPolicy.anonymiseIfRequired(comment)).thenReturn(anonymisedComment);

    var result = commentService.saveNewComment(create);

    assertThat(result).isEqualTo(anonymisedComment);
  }

  @Test
  void saveNewCommentAnonymousOnBatchThrowsError() {
    when(request.isBatchChild()).thenReturn(true);
    when(requestFinder.getRequestById(REQUEST_ID)).thenReturn(request);

    CommentCreateDTO create = buildCommentCreateDTO(TEST_NAME, TEST_EMAIL, null);

    assertThrows(BadRequestException.class, () -> commentService.saveNewComment(create));
  }

  @Test
  void saveNewCommentAccountNotFound() {
    when(request.isBatchChild()).thenReturn(false);
    when(requestFinder.getRequestById(REQUEST_ID)).thenReturn(request);
    CommentCreateDTO create = buildCommentCreateDTO(null, null, ACCOUNT_ID);
    when(accountFinder.forceGetAccountById(create.getAccountId()))
        .thenThrow(new DataException("Account with id %d not found", ACCOUNT_ID));

    assertThrows(DataException.class, () -> commentService.saveNewComment(create));
  }

  @ParameterizedTest
  @EnumSource(UpdateCommentVariations.class)
  void updateComment(UpdateCommentVariations variations) {
    var update =
        CommentUpdateDTO.builder()
            .id(ID)
            .anonymous(variations.getAnonymous())
            .addressed(variations.getAddressed())
            .publicAccess(variations.getPublicAccess())
            .suppressed(variations.getSuppressed())
            .build();
    var comment = buildComment();
    when(commentRepository.findById(ID)).thenReturn(Optional.of(comment));
    when(transaction.updateComment(eq(comment), any(Comment.class), eq(update.getPiiAction())))
        .thenReturn(comment);

    when(accessPolicy.process(comment)).thenReturn(Optional.of(comment));

    commentService.updateComment(update);

    verify(transaction)
        .updateComment(eq(comment), commentArgumentCaptor.capture(), eq(update.getPiiAction()));
    var commentCapture = commentArgumentCaptor.getValue();

    assertEquals(
        variations.getAnonymous() != null ? variations.getAnonymous() : false,
        commentCapture.isAnonymous());
    assertEquals(
        variations.getAddressed() != null ? variations.getAddressed() : false,
        commentCapture.isAddressed());
    assertEquals(
        variations.getPublicAccess() != null ? variations.getPublicAccess() : false,
        commentCapture.isPublicAccess());
    assertEquals(
        variations.getSuppressed() != null ? variations.getSuppressed() : false,
        commentCapture.isSuppressed());
  }

  @Getter
  @AllArgsConstructor
  public enum UpdateCommentVariations {
    ANONYMOUS(true, null, null, null),
    ADDRESSED(null, true, null, null),
    PUBLIC(null, null, true, null),
    SUPPRESSED(null, null, null, true);
    private final Boolean anonymous;
    private final Boolean addressed;
    private final Boolean publicAccess;
    private final Boolean suppressed;
  }

  @Test
  void updateCommentNotFound() {
    assertThrows(
        NotFoundException.class, () -> commentService.updateComment(mock(CommentUpdateDTO.class)));
  }

  @Test
  void deleteComment() {
    var comment = buildComment();
    var deleteDto = CommentDelete.builder().id(comment.getId()).deletionReasonId(0).build();
    when(commentRepository.findById(ID)).thenReturn(Optional.of(comment));

    commentService.deleteComment(deleteDto);

    verify(transaction).saveCommentAsDeleted(commentArgumentCaptor.capture());
    var commentCapture = commentArgumentCaptor.getValue();
    assertTrue(commentCapture.isDeleted());
    assertEquals(DELETED, commentCapture.getSubmittedComment());
    assertEquals(DELETED, commentCapture.getUploadedByEmail());
    assertEquals(DELETED, commentCapture.getUploadedByName());
    assertNull(commentCapture.getAccount());
  }

  @Test
  void deleteCommentNotFound() {
    var commentDelete = CommentDelete.builder().id(1).deletionReasonId(1).build();
    assertThrows(NotFoundException.class, () -> commentService.deleteComment(commentDelete));
  }

  private static Comment buildComment() {
    Request request = Request.builder().id(456).build();
    return buildComment(request);
  }

  private static Comment buildComment(Request request) {
    Project project = new Project();
    project.setId(123);
    request.setProject(project);
    return Comment.builder()
        .id(ID)
        .submittedComment("test")
        .requestId(request.getId())
        .uploadedByName(TEST_NAME)
        .uploadedByEmail(TEST_EMAIL)
        .anonymous(false)
        .publicAccess(false)
        .addressed(false)
        .uploadedDate(ZonedDateTime.now())
        .build();
  }

  private static CommentCreateDTO buildCommentCreateDTO(
      String name, String email, Integer accountId) {
    return CommentCreateDTO.builder()
        .comment("test")
        .accountId(accountId)
        .usersEmail(email)
        .usersName(name)
        .requestId(REQUEST_ID)
        .anonymous(true)
        .build();
  }

  private static List<Comment> buildComments() {
    return List.of(
        CommentMother.publicComment(),
        CommentMother.deletedComment(),
        CommentMother.supressedComment(),
        CommentMother.addressedComment(),
        CommentMother.anonymousComment(),
        CommentMother.withAccount(AccountMother.tacAccount()));
  }
}

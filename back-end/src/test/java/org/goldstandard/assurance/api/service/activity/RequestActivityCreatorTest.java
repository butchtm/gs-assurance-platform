package org.goldstandard.assurance.api.service.activity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.GS_ARM_ACCOUNT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.REVIEWER_ACCOUNT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.TAC_ACCOUNT_TYPE_ID;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.activity.Activity;
import org.goldstandard.assurance.api.entity.lookup.AccountType;
import org.goldstandard.assurance.api.service.AssignmentFinder;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestActivityCreatorTest {

  @InjectMocks RequestActivityCreator creator;

  @Mock AssignmentFinder assignmentFinder;

  @Mock AccountFinder accountFinder;

  @Mock ActivityDeadlineCalculator activityDeadlineCalculator;

  @Mock Clock clock;

  @Mock RequestActivityDisplayNameFactory displayNameFactory;

  @Mock AuthenticatedUserSupplier authenticatedUserSupplier;

  @Test
  void shouldBuildRequestActivity() {
    var request = buildRequest();
    var account = buildAccount(GS_ARM_ACCOUNT_TYPE_ID);
    when(accountFinder.getArmTeamAccount()).thenReturn(account);
    var activity = buildActivity(new AccountType(TAC_ACCOUNT_TYPE_ID));
    var now = mockTimeNow();
    var expectedPlannedEndDate = givenPlannedEndDate(now, activity, request);
    var expectedDisplayName = givenExpectedDisplayName(activity, request);

    var requestActivity = creator.buildRequestActivity(activity, request);

    assertThat(requestActivity.getId()).isNull();
    assertThat(requestActivity.getRequestId()).isEqualTo(request.getId());
    assertThat(requestActivity.getActivity()).isEqualTo(activity);
    assertThat(requestActivity.getStartDate()).isEqualTo(now);
    assertThat(requestActivity.getPlannedEndDate()).isEqualTo(expectedPlannedEndDate);
    assertThat(requestActivity.getEndDate()).isNull();
    assertThat(requestActivity.getDisplayName()).isEqualTo(expectedDisplayName);
    assertThat(requestActivity.getSkipped()).isFalse();
  }

  @Test
  void shouldBuildSkippedRequestActivity() {
    var request = buildRequest();
    var account = buildAccount(101);
    when(authenticatedUserSupplier.getAuthenticatedUserAccount()).thenReturn(Optional.of(account));
    var activity = buildActivity(new AccountType(REVIEWER_ACCOUNT_TYPE_ID));
    var now = ZonedDateTime.now();
    var expectedPlannedEndDate = givenPlannedEndDate(now, activity, request);
    var expectedDisplayName = givenExpectedDisplayName(activity, request);

    var requestActivity = creator.buildSkippedRequestActivity(activity, request, now);

    assertThat(requestActivity.getId()).isNull();
    assertThat(requestActivity.getRequestId()).isEqualTo(request.getId());
    assertThat(requestActivity.getActivity()).isEqualTo(activity);
    assertThat(requestActivity.getStartDate()).isEqualTo(now);
    assertThat(requestActivity.getPlannedEndDate()).isEqualTo(expectedPlannedEndDate);
    assertThat(requestActivity.getEndDate()).isEqualTo(now);
    assertThat(requestActivity.getDisplayName()).isEqualTo(expectedDisplayName);
    assertThat(requestActivity.getSkipped()).isTrue();
  }

  @Test
  void shouldPopulateAssignedQAQCAccountAsResponsibleAccountIfAssignedAccountTypeIsArm() {
    var request = buildRequest();
    var qaqcAccount = buildAccount(GS_ARM_ACCOUNT_TYPE_ID);
    when(assignmentFinder.findLatestAccountAssignedToQAQCRequest(request.getId()))
        .thenReturn(Optional.of(qaqcAccount));
    var activity = buildActivity(new AccountType(GS_ARM_ACCOUNT_TYPE_ID));
    var now = mockTimeNow();
    givenPlannedEndDate(now, activity, request);

    var requestActivity = creator.buildRequestActivity(activity, request);

    assertThat(requestActivity.getResponsibleAccount()).isEqualTo(qaqcAccount);
  }

  @Test
  void shouldPopulateArmAccountAsResponsibleAccountIfAssignedAccountTypeIsArmButQAQCNotAssigned() {
    var request = buildRequest();
    when(assignmentFinder.findLatestAccountAssignedToQAQCRequest(request.getId()))
        .thenReturn(Optional.empty());
    var armAccount = buildAccount(GS_ARM_ACCOUNT_TYPE_ID);
    when(accountFinder.getArmTeamAccount()).thenReturn(armAccount);
    var activity = buildActivity(new AccountType(GS_ARM_ACCOUNT_TYPE_ID));
    var now = mockTimeNow();
    givenPlannedEndDate(now, activity, request);

    var requestActivity = creator.buildRequestActivity(activity, request);

    assertThat(requestActivity.getResponsibleAccount()).isEqualTo(armAccount);
  }

  @Test
  void shouldPopulateArmAccountAsResponsibleAccountIfAssignedAccountTypeIsNotReviewer() {
    var request = buildRequest();
    var armAccount = buildAccount(GS_ARM_ACCOUNT_TYPE_ID);
    when(accountFinder.getArmTeamAccount()).thenReturn(armAccount);
    var activity = buildActivity(new AccountType(TAC_ACCOUNT_TYPE_ID));
    var now = mockTimeNow();
    givenPlannedEndDate(now, activity, request);

    var requestActivity = creator.buildRequestActivity(activity, request);

    assertThat(requestActivity.getResponsibleAccount()).isEqualTo(armAccount);
  }

  @Test
  void shouldPopulateAssignedAccountAsResponsibleAccountIfAssignedAccountTypeIsReviewer() {
    var request = buildRequest();
    var account = buildAccount(REVIEWER_ACCOUNT_TYPE_ID);
    when(assignmentFinder.forceFindLatestAccountAssignedToReviewRequest(request.getId()))
        .thenReturn(account);
    var activity = buildActivity(account.getAccountType());
    var now = mockTimeNow();
    givenPlannedEndDate(now, activity, request);

    var requestActivity = creator.buildRequestActivity(activity, request);

    assertThat(requestActivity.getResponsibleAccount()).isEqualTo(account);
  }

  private ZonedDateTime mockTimeNow() {
    var now = ZonedDateTime.now();
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(clock.instant()).thenReturn(now.toInstant());
    return now;
  }

  private ZonedDateTime givenPlannedEndDate(ZonedDateTime now, Activity activity, Request request) {
    var plannedEndDate = now.plusDays(3);
    when(activityDeadlineCalculator.calculateDeadline(now, activity, request.getId()))
        .thenReturn(plannedEndDate);
    return plannedEndDate;
  }

  private String givenExpectedDisplayName(Activity activity, Request request) {
    var displayName = "expected-display-name";
    when(displayNameFactory.toDisplayName(request, activity)).thenReturn(displayName);
    return displayName;
  }

  private static Account buildAccount(int typeId) {
    var account = new Account();
    account.setAccountType(toAccountType(typeId));
    return account;
  }

  private static AccountType toAccountType(int id) {
    return new AccountType(id);
  }

  private static Activity buildActivity(AccountType accountType) {
    return Activity.builder().id(2).totalDays(4).accountType(accountType).build();
  }

  private static Request buildRequest() {
    return Request.builder().id(3).build();
  }
}

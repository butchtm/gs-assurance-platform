package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentAccessRightsCalculatorTest {
  @InjectMocks CommentAccessRightsCalculator calculator;

  @Mock PermissionsService permissionsService;

  @Test
  void shouldIncludeTacCommentsIfUserIsNotProjectDeveloperOrVVB() {
    when(permissionsService.isProjectDeveloperOrVVB()).thenReturn(false);

    var accessRights = calculator.calculateAccessRights();

    assertThat(accessRights.isIncludeTac()).isTrue();
  }

  @Test
  void shouldNotIncludeTacCommentsIfUserIsProjectDeveloperOrVVB() {
    when(permissionsService.isProjectDeveloperOrVVB()).thenReturn(true);

    var accessRights = calculator.calculateAccessRights();

    assertThat(accessRights.isIncludeTac()).isFalse();
  }

  @Test
  void shouldIncludeSuppressedCommentsIfUserIsAdminOrArm() {
    when(permissionsService.isAdminOrArm()).thenReturn(true);

    var accessRights = calculator.calculateAccessRights();

    assertThat(accessRights.isIncludeSuppressed()).isTrue();
  }

  @Test
  void shouldNotIncludeSuppressedCommentsIfUserIsNotAdminOrArm() {
    when(permissionsService.isAdminOrArm()).thenReturn(false);

    var accessRights = calculator.calculateAccessRights();

    assertThat(accessRights.isIncludeSuppressed()).isFalse();
  }

  @Test
  void shouldIncludeDeletedCommentsIfUserIsAdminOrArm() {
    when(permissionsService.isAdminOrArm()).thenReturn(true);

    var accessRights = calculator.calculateAccessRights();

    assertThat(accessRights.isIncludeDeleted()).isTrue();
  }

  @Test
  void shouldNotIncludeDeletedCommentsIfUserIsNotAdminOrArm() {
    when(permissionsService.isAdminOrArmOrTac()).thenReturn(false);

    var accessRights = calculator.calculateAccessRights();

    assertThat(accessRights.isIncludeDeleted()).isFalse();
  }

  @Test
  void shouldIncludePrivateCommentsIfUserIsAdminOrTac() {
    when(permissionsService.isAdminOrArmOrTac()).thenReturn(true);

    var accessRights = calculator.calculateAccessRights();

    assertThat(accessRights.isIncludePrivate()).isTrue();
  }

  @Test
  void shouldNotIncludePrivateCommentsIfUserIsNotAdminOrTac() {
    when(permissionsService.isAdminOrArmOrTac()).thenReturn(false);

    var accessRights = calculator.calculateAccessRights();

    assertThat(accessRights.isIncludePrivate()).isFalse();
  }

  @Test
  void shouldNotAnonymiseCommentsIfUserIsAdminOrArmOrTac() {
    when(permissionsService.isAdminOrArmOrTac()).thenReturn(true);

    var accessRights = calculator.calculateAccessRights();

    assertThat(accessRights.isAnonymise()).isFalse();
  }

  @Test
  void shouldAnonymiseCommentsIfUserIsNotAdminOrArmOrTac() {
    when(permissionsService.isAdminOrArmOrTac()).thenReturn(false);

    var accessRights = calculator.calculateAccessRights();

    assertThat(accessRights.isAnonymise()).isTrue();
  }
}

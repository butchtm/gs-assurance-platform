package org.goldstandard.assurance.api.controller.pub;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.converter.AuthUserConverter;
import org.goldstandard.assurance.api.dto.UserDTO;
import org.goldstandard.assurance.api.dto.auth.AuthResultDTO;
import org.goldstandard.assurance.api.dto.auth.ConfirmForgotPasswordDTO;
import org.goldstandard.assurance.api.dto.auth.ForgotPasswordDTO;
import org.goldstandard.assurance.api.dto.auth.LoginRequestDTO;
import org.goldstandard.assurance.api.dto.auth.LoginResponseDTO;
import org.goldstandard.assurance.api.dto.auth.MfaChallengeRequestDTO;
import org.goldstandard.assurance.api.dto.auth.SetNewPasswordDTO;
import org.goldstandard.assurance.api.dto.auth.VerifyTokenResponse;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.service.auth.AuthService;
import org.goldstandard.assurance.api.service.auth.CookieService;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public")
public class AuthController {
  private final AuthService authService;
  private final CookieService cookieService;
  private final AuthUserConverter accountMapper;

  @PostMapping("/login")
  public LoginResponseDTO login(
      @RequestBody LoginRequestDTO loginRequestDTO, HttpServletResponse servletResponse) {
    var result = authService.login(loginRequestDTO);
    if (result.isNewPasswordRequired()) {
      servletResponse.addCookie(cookieService.buildPasswordSessionIdCookie(result.getSession()));
      return new LoginResponseDTO();
    }
    if (result.isMfaChallengeRequired()) {
      servletResponse.addCookie(cookieService.buildMfaSessionIdCookie(result.getSession()));
      return LoginResponseDTO.builder()
          .mfaChallengeRequired(true)
          .challengeType(result.getChallengeType().orElse(null))
          .availableChallengeTypes(result.getAvailableChallengeTypes())
          .build();
    }
    var user = findUserAndPopulateCookies(result, servletResponse);
    populateLoggingContext(user);
    return new LoginResponseDTO(toUserDto(user));
  }

  @GetMapping("/verify-token")
  public VerifyTokenResponse verifyToken(
      HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
    return cookieService
        .findAccessTokenValue(servletRequest)
        .map(accessToken -> verifyAccessToken(accessToken, servletRequest, servletResponse))
        .orElse(new VerifyTokenResponse());
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(
      HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
    cookieService.findRefreshTokenValue(servletRequest).ifPresent(authService::logout);
    cookieService.removeCookies(servletResponse);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordDTO request) {
    authService.forgotPassword(request);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/confirm-forgot-password")
  public ResponseEntity<Void> confirmForgotPassword(@RequestBody ConfirmForgotPasswordDTO request) {
    authService.confirmForgotPassword(request);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/set-new-password")
  public ResponseEntity<Void> setNewPassword(
      HttpServletRequest servletRequest,
      HttpServletResponse servletResponse,
      @RequestBody SetNewPasswordDTO setNewPasswordDTO) {
    String sessionId = cookieService.forceFindPasswordSessionIdValue(servletRequest);
    authService.setNewPassword(setNewPasswordDTO.withSessionId(sessionId));
    cookieService.removePasswordSessionIdCookie(servletResponse);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/select-mfa-type")
  public ResponseEntity<Void> selectMfaType(
      HttpServletRequest servletRequest,
      HttpServletResponse servletResponse,
      @RequestBody MfaChallengeRequestDTO requestDTO) {
    String sessionId = cookieService.forceFindMfaSessionIdValue(servletRequest);
    var result =
        authService.respondToSelectMfaType(
            sessionId, requestDTO.getUsername(), requestDTO.getChallengeType());

    if (result
        .getChallengeType()
        .map(type -> type.equals(requestDTO.getChallengeType()))
        .orElse(false)) {
      servletResponse.addCookie(cookieService.buildMfaSessionIdCookie(result.getSession()));
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.internalServerError().build();
  }

  @PostMapping("/respond-to-software-token-mfa")
  public LoginResponseDTO respondToSoftwareTokenMfa(
      HttpServletRequest servletRequest,
      HttpServletResponse servletResponse,
      @RequestBody MfaChallengeRequestDTO requestDTO) {
    String sessionId = cookieService.forceFindMfaSessionIdValue(servletRequest);
    var result =
        authService.respondToSoftwareTokenMfa(
            sessionId, requestDTO.getUsername(), requestDTO.getCode());
    cookieService.removeMfaSessionIdCookie(servletResponse);

    if (result.isSuccessful()) {
      var user = findUserAndPopulateCookies(result, servletResponse);
      populateLoggingContext(user);
      return new LoginResponseDTO(toUserDto(user));
    }

    return new LoginResponseDTO();
  }

  @PostMapping("/respond-to-email-otp")
  public LoginResponseDTO respondToEmailOtp(
      HttpServletRequest servletRequest,
      HttpServletResponse servletResponse,
      @RequestBody MfaChallengeRequestDTO requestDTO) {
    String sessionId = cookieService.forceFindMfaSessionIdValue(servletRequest);
    var result =
        authService.respondToEmailOtp(sessionId, requestDTO.getUsername(), requestDTO.getCode());
    cookieService.removeMfaSessionIdCookie(servletResponse);

    if (result.isSuccessful()) {
      var user = findUserAndPopulateCookies(result, servletResponse);
      populateLoggingContext(user);
      return new LoginResponseDTO(toUserDto(user));
    }

    return new LoginResponseDTO();
  }

  private VerifyTokenResponse verifyAccessToken(
      String accessToken, HttpServletRequest request, HttpServletResponse response) {
    if (authService.isExpired(accessToken)) {
      var refreshToken = cookieService.findRefreshTokenValue(request);
      if (refreshToken.isPresent()) {
        var result = authService.refreshToken(refreshToken.get());
        if (result.isSuccessful()) {
          cookieService.populateCookies(result, response);
          var user = authService.findUserByToken(result.getAccessToken());
          populateLoggingContext(user);
          return new VerifyTokenResponse(toUserDto(user));
        }
      }
    }
    var user = authService.findUserByToken(accessToken);
    populateLoggingContext(user);
    return new VerifyTokenResponse(toUserDto(user));
  }

  private UserDTO toUserDto(User user) {
    return accountMapper.toUserDto(user);
  }

  private User findUserAndPopulateCookies(
      AuthResultDTO result, HttpServletResponse servletResponse) {
    var user = authService.findUserByToken(result.getAccessToken());
    cookieService.populateCookies(result, servletResponse);
    return user;
  }

  private void populateLoggingContext(User user) {
    MDC.put("username", user.getUserName());
  }
}

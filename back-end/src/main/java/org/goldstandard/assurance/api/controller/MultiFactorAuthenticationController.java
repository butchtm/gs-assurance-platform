package org.goldstandard.assurance.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.mfa.MfaSettingsDTO;
import org.goldstandard.assurance.api.dto.mfa.SoftwareTokenSetupResponseDTO;
import org.goldstandard.assurance.api.dto.mfa.UpdateMfaSettingsResponseDTO;
import org.goldstandard.assurance.api.dto.mfa.VerifySoftwareTokenRequestDTO;
import org.goldstandard.assurance.api.dto.mfa.VerifySoftwareTokenResponseDTO;
import org.goldstandard.assurance.api.service.mfa.MfaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mfa")
public class MultiFactorAuthenticationController {

  private final MfaService mfaService;

  @GetMapping("/settings")
  public MfaSettingsDTO getMfaSettings(@RequestParam(required = false) String username) {
    return mfaService.getMfaSettings(username);
  }

  @PutMapping("/settings")
  public UpdateMfaSettingsResponseDTO updateMfaSettings(@RequestBody MfaSettingsDTO request) {
    return mfaService.updateMfaSettings(request);
  }

  @PostMapping("/software-token/setup")
  public SoftwareTokenSetupResponseDTO setupSoftwareToken() {
    return mfaService.setupSoftwareToken();
  }

  @PostMapping("/software-token/verify")
  public VerifySoftwareTokenResponseDTO verifySoftwareToken(
      @Valid @RequestBody VerifySoftwareTokenRequestDTO request) {
    return mfaService.verifySoftwareToken(request);
  }
}

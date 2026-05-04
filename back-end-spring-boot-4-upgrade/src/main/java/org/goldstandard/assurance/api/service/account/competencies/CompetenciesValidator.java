package org.goldstandard.assurance.api.service.account.competencies;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.CompetenciesDTO;
import org.goldstandard.assurance.api.entity.Account;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CompetenciesValidator {

  private final CompetenciesAccountValidator accountValidator;
  private final CompetenciesDTOValidator dtoValidator;

  public void validateNew(CompetenciesDTO competenciesDto, Account account) {
    accountValidator.validateNew(account);
    validateDto(competenciesDto, account);
  }

  public void validateUpdate(CompetenciesDTO competenciesDto, Account account) {
    accountValidator.validateUpdate(account);
    validateDto(competenciesDto, account);
  }

  private void validateDto(CompetenciesDTO competenciesDto, Account account) {
    dtoValidator.validate(competenciesDto, account);
  }
}

package org.goldstandard.assurance.api.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class UserBatchFileValidator
    implements ConstraintValidator<ValidUserBatchCsvFile, MultipartFile> {

  @Override
  public boolean isValid(
      MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
    String contentType = multipartFile.getContentType();
    return contentType != null && contentType.equals("text/csv");
  }
}

package org.goldstandard.assurance.api.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.apache.commons.beanutils.PropertyUtils;

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = {CheckAtLeastOneNotNull.CheckAtLeastOneNotNullValidator.class})
@Documented
public @interface CheckAtLeastOneNotNull {

  String message() default
      "Validation of input has failed. Must have at least one field not null or empty.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  class CheckAtLeastOneNotNullValidator
      implements ConstraintValidator<CheckAtLeastOneNotNull, Object> {

    public boolean isValid(Object object, ConstraintValidatorContext constraintContext) {
      Field[] fields = object.getClass().getDeclaredFields();
      return Arrays.stream(fields).anyMatch(field -> isNotEmpty(field, object));
    }

    private boolean isNotEmpty(Field field, Object object) {
      var propertyValue = extractPropertyValue(field, object);
      return !isEmptyOrNull(propertyValue);
    }

    private Object extractPropertyValue(Field field, Object object) {
      try {
        return PropertyUtils.getProperty(object, field.getName());
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        return null;
      }
    }

    private boolean isEmptyOrNull(Object property) {
      if (property instanceof List<?> list) {
        return list.isEmpty();
      }
      return Objects.isNull(property);
    }
  }
}

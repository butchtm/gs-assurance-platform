package org.goldstandard.assurance.api.service.form;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.goldstandard.assurance.api.fixtures.JsonFactory.toJsonArray;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.goldstandard.assurance.api.entity.RequestForm;
import org.goldstandard.assurance.api.exception.BadRequestException;
import org.goldstandard.assurance.api.fixtures.FormDataMother;
import org.goldstandard.assurance.api.fixtures.FormDefinitionMother;
import org.junit.jupiter.api.Test;

class RequestFormDataValidatorTest {

  private final RequestFormDataValidator validator = new RequestFormDataValidator();

  @Test
  void shouldThrowExceptionIfRequestFormDataIsNotValidAgainstSchema() {
    var form =
        RequestForm.builder()
            .id(1)
            .definition(FormDefinitionMother.build())
            .data(FormDataMother.emptyObject())
            .build();

    var error = catchThrowable(() -> validator.validateDataAgainstSchema(form));

    assertThat(error)
        .isInstanceOf(BadRequestException.class)
        .hasMessage("required property 'field' not found");
  }

  @Test
  void shouldThrowExceptionIfRequestSchemaIsArrayAndDataIsNotArray() {
    var form =
        RequestForm.builder()
            .id(2)
            .definition(FormDefinitionMother.withArraySchema())
            .data(FormDataMother.emptyObject())
            .build();

    var error = catchThrowable(() -> validator.validateDataAgainstSchema(form));

    assertThat(error)
        .isInstanceOf(BadRequestException.class)
        .hasMessage("form schema is an array, form data must also be an array");
  }

  @Test
  void shouldThrowExceptionIfRequestSchemaIsNotArrayAndDataIsArray() {
    var form =
        RequestForm.builder()
            .id(3)
            .definition(FormDefinitionMother.build())
            .data(FormDataMother.buildArray())
            .build();

    var error = catchThrowable(() -> validator.validateDataAgainstSchema(form));

    assertThat(error)
        .isInstanceOf(BadRequestException.class)
        .hasMessage("array found, object expected");
  }

  @Test
  void shouldThrowExceptionIfRequestSchemaIsNotTheSameSizeAsDataArray() {
    var form =
        RequestForm.builder()
            .id(4)
            .definition(FormDefinitionMother.withArraySchema(4))
            .data(FormDataMother.buildArray(3))
            .build();

    var error = catchThrowable(() -> validator.validateDataAgainstSchema(form));

    assertThat(error)
        .isInstanceOf(BadRequestException.class)
        .hasMessage("form data array size 3 must match schema array size 4");
  }

  @Test
  void shouldThrowExceptionIfAnyItemInDataArrayDoesNotMatchItemInSchemaArray() {
    var form =
        RequestForm.builder()
            .id(5)
            .definition(FormDefinitionMother.withArraySchema())
            .data(toJsonArray(FormDataMother.build(), FormDataMother.emptyObject()))
            .build();

    var error = catchThrowable(() -> validator.validateDataAgainstSchema(form));

    assertThat(error)
        .isInstanceOf(BadRequestException.class)
        .hasMessage("required property 'field' not found");
  }

  @Test
  void shouldThrowExceptionIfAnyItemInDataArrayDoesNotMatchItemInSchemaStepsArray() {
    var form =
        RequestForm.builder()
            .id(6)
            .definition(FormDefinitionMother.withStepsArraySchema())
            .data(toJsonArray(FormDataMother.build(), FormDataMother.emptyObject()))
            .build();

    var error = catchThrowable(() -> validator.validateDataAgainstSchema(form));

    assertThat(error)
        .isInstanceOf(BadRequestException.class)
        .hasMessage("required property 'field' not found");
  }

  @Test
  void shouldNotThrowExceptionIfRequestFormDataIsValidAgainstSchema() {
    var form =
        RequestForm.builder()
            .id(7)
            .definition(FormDefinitionMother.build())
            .data(FormDataMother.build())
            .build();

    ThrowingCallable call = () -> validator.validateDataAgainstSchema(form);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldNotThrowExceptionIfAllItemsInDataMatchAllItemsInSchemaArray() {
    var form =
        RequestForm.builder()
            .id(8)
            .definition(FormDefinitionMother.withArraySchema())
            .data(FormDataMother.buildArray())
            .build();

    ThrowingCallable call = () -> validator.validateDataAgainstSchema(form);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldNotThrowExceptionIfAllItemsInDataMatchAllItemsInSchemaStepsArray() {
    var form =
        RequestForm.builder()
            .id(9)
            .definition(FormDefinitionMother.withStepsArraySchema())
            .data(FormDataMother.buildArray())
            .build();

    ThrowingCallable call = () -> validator.validateDataAgainstSchema(form);

    assertThatCode(call).doesNotThrowAnyException();
  }
}

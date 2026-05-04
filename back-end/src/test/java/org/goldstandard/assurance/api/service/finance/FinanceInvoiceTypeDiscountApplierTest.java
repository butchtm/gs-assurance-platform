package org.goldstandard.assurance.api.service.finance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.goldstandard.assurance.api.service.finance.FinanceInvoiceType.ISSUANCE_FEES;
import static org.goldstandard.assurance.api.service.finance.FinanceInvoiceType.PERFORMANCE_REVIEW_FEES;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.exception.DataException;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.fixtures.FeeScheduleMother;
import org.goldstandard.assurance.api.repository.FeeScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FinanceInvoiceTypeDiscountApplierTest {

  @InjectMocks FinanceInvoiceTypeDiscountApplier applier;

  @Mock FeeScheduleRepository repository;

  @Mock Project project;

  @Mock Request request;

  @Test
  void shouldReturnInvoiceTypeLabelIfNoDiscountApplied() {
    when(request.getProject()).thenReturn(project);

    var result = applier.applyDiscountsIfRequired(request, PERFORMANCE_REVIEW_FEES);

    assertThat(result).isEqualTo(PERFORMANCE_REVIEW_FEES.getLabel());
  }

  @Nested
  class IssuanceDiscountTest {

    private static final int FEE_SCHEDULE_ID = 0;

    @BeforeEach
    void setUp() {
      when(request.hasIssuanceType()).thenReturn(true);
    }

    @Test
    void shouldThrowExceptionIfProjectDoesNotHaveFeeSchedule() {
      when(request.getProject()).thenReturn(project);
      when(project.getLatestFeeScheduleId()).thenReturn(Optional.empty());
      when(project.getGsid()).thenReturn("GS1111");

      var error = catchThrowable(() -> applier.applyDiscountsIfRequired(request, ISSUANCE_FEES));

      assertThat(error)
          .isInstanceOf(NotFoundException.class)
          .hasMessage("fee schedule not found for project GS1111");
    }

    @Test
    void shouldThrowExceptionIfFeeScheduleNotFound() {
      when(request.hasIssuanceType()).thenReturn(true);
      when(request.getProject()).thenReturn(project);
      when(project.getLatestFeeScheduleId()).thenReturn(Optional.of(FEE_SCHEDULE_ID));
      when(project.getGsid()).thenReturn("GS2222");
      when(repository.findById(FEE_SCHEDULE_ID)).thenReturn(Optional.empty());

      var error = catchThrowable(() -> applier.applyDiscountsIfRequired(request, ISSUANCE_FEES));

      assertThat(error)
          .isInstanceOf(NotFoundException.class)
          .hasMessage("fee schedule not found for project GS2222");
    }

    @Test
    void shouldThrowExceptionIfFeeScheduleNotMapped() {
      when(request.hasIssuanceType()).thenReturn(true);
      when(request.getProject()).thenReturn(project);
      when(project.getLatestFeeScheduleId()).thenReturn(Optional.of(FEE_SCHEDULE_ID));
      when(project.getGsid()).thenReturn("GS3333");
      when(repository.findById(FEE_SCHEDULE_ID))
          .thenReturn(Optional.of(FeeScheduleMother.withCode("OTHER_CODE")));

      var error = catchThrowable(() -> applier.applyDiscountsIfRequired(request, ISSUANCE_FEES));

      assertThat(error)
          .isInstanceOf(DataException.class)
          .hasMessage("fee schedule code OTHER_CODE for project GS3333 not mapped");
    }

    @ParameterizedTest
    @MethodSource("feeScheduleCodeAndIsLDCAndExpectedInvoiceType")
    void shouldApplyFeeScheduleDiscountForIssuanceRequest(
        String feeScheduleCode, boolean isLdc, String expectedDiscountedInvoiceType) {
      when(request.getProject()).thenReturn(project);
      when(project.getLatestFeeScheduleId()).thenReturn(Optional.of(FEE_SCHEDULE_ID));
      when(project.inLeastDevelopedCountry()).thenReturn(isLdc);
      when(repository.findById(FEE_SCHEDULE_ID))
          .thenReturn(Optional.of(FeeScheduleMother.withCode(feeScheduleCode)));

      var result = applier.applyDiscountsIfRequired(request, ISSUANCE_FEES);

      assertThat(result).isEqualTo(expectedDiscountedInvoiceType);
    }

    private static Stream<Arguments> feeScheduleCodeAndIsLDCAndExpectedInvoiceType() {
      return Stream.of(
          Arguments.of("CASH_PER_CREDIT", false, "Issuance Fees VERs CASH"),
          Arguments.of("CASH_PER_CREDIT", true, "Issuance Fees VERs CASH LDC"),
          Arguments.of("SHARE_OF_PROCEEDS", false, "Issuance Fees VERs SoP"),
          Arguments.of("SHARE_OF_PROCEEDS", true, "Issuance Fees VERs SoP LDC"));
    }
  }

  @Nested
  class StandardDiscountTest {

    @ParameterizedTest
    @MethodSource("invoiceTypeAndRegularCaseVpaAndExpectedFinanceInvoiceType")
    void shouldApplyRegularCaseVpaDiscountIfApplicable(
        FinanceInvoiceType type, boolean isRegularVpa, String expectedInvoiceType) {
      when(request.getProject()).thenReturn(project);
      when(project.isRegularCaseVpa()).thenReturn(isRegularVpa);

      var result = applier.applyDiscountsIfRequired(request, type);

      assertThat(result).isEqualTo(expectedInvoiceType);
    }

    @ParameterizedTest
    @MethodSource("invoiceTypeAndIsMicroscaleAndExpectedFinanceInvoiceType")
    void shouldApplyMicroscaleDiscountIfApplicable(
        FinanceInvoiceType type, boolean isMicroscale, String expectedInvoiceType) {
      when(request.getProject()).thenReturn(project);
      when(project.isMicroscale()).thenReturn(isMicroscale);

      var result = applier.applyDiscountsIfRequired(request, type);

      assertThat(result).isEqualTo(expectedInvoiceType);
    }

    private static Stream<Arguments> invoiceTypeAndRegularCaseVpaAndExpectedFinanceInvoiceType() {
      return Arrays.stream(FinanceInvoiceType.values())
          .flatMap(type -> toArguments(type, "Regular VPA"));
    }

    private static Stream<Arguments> invoiceTypeAndIsMicroscaleAndExpectedFinanceInvoiceType() {
      return Arrays.stream(FinanceInvoiceType.values())
          .flatMap(type -> toArguments(type, "Microscale"));
    }

    private static Stream<Arguments> toArguments(FinanceInvoiceType type, String suffix) {
      return Stream.of(toArguments(type, false, suffix), toArguments(type, true, suffix));
    }

    private static Arguments toArguments(FinanceInvoiceType type, boolean b, String suffix) {
      var expectedLabel = type.getLabel();
      if (b) {
        expectedLabel = String.format("%s %s", expectedLabel, suffix);
      }
      return Arguments.of(type, b, expectedLabel);
    }
  }
}

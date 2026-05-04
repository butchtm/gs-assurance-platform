package org.goldstandard.assurance.api.service.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.goldstandard.assurance.api.dto.AssignmentSummaryFiltersDTO;
import org.goldstandard.assurance.api.enumeration.AssignmentSummaryType;
import org.goldstandard.assurance.api.exception.ReportGenerationException;
import org.goldstandard.assurance.api.service.ArmAssignmentSummaryFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class AssignmentSummaryReportGeneratorTest {

  @InjectMocks private AssignmentSummaryReportGenerator generator;
  @Mock private AssignmentSummaryFilenameGenerator filenameGenerator;
  @Mock private AssignmentSummaryReportFactoryProvider factoryProvider;
  @Mock private AssignmentSummaryReportWriterBuilder writerBuilder;

  @Mock private ArmAssignmentSummaryFinder armSummaryFinder;
  @Mock private ArmAssignmentSummaryReportFactory armReportFactory;
  @Mock private AssignmentSummaryReportWriter writer;

  @Captor private ArgumentCaptor<AssignmentSummaryReportWriterParams> writerParamsCaptor;

  private static final String FILE_NAME = "MyReport.xlsx";
  private static final byte[] REPORT = "MyReport".getBytes();

  @BeforeEach
  void setup() {
    when(armSummaryFinder.getType()).thenReturn(AssignmentSummaryType.ARM);
  }

  @Test
  void shouldWriteReportWithCorrectReportFactory() throws IOException {
    var filters = AssignmentSummaryFiltersDTO.builder().build();
    var pageable = mock(Pageable.class);

    when(factoryProvider.getReportFactory(AssignmentSummaryType.ARM)).thenReturn(armReportFactory);
    when(writerBuilder.buildWriter(writerParamsCaptor.capture())).thenReturn(writer);
    when(writer.writeToByteArray()).thenReturn(REPORT);
    when(filenameGenerator.generateFilename(filters)).thenReturn(FILE_NAME);

    var result = generator.generate(filters, pageable, armSummaryFinder);

    assertThat(result.getData()).isEqualTo(REPORT);
    assertThat(result.getFilename()).isEqualTo(FILE_NAME);
    var paramsUsed = writerParamsCaptor.getValue();
    assertThat(paramsUsed.getSummaryFinder()).isEqualTo(armSummaryFinder);
    assertThat(paramsUsed.getFactory()).isEqualTo(armReportFactory);
    assertThat(paramsUsed.getFilters()).isEqualTo(filters);
    assertThat(paramsUsed.getOriginalPageable()).isEqualTo(pageable);
  }

  @Test
  void shouldThrowReportGenerationExceptionWhenIOExceptionOccurs() throws IOException {
    var filters = AssignmentSummaryFiltersDTO.builder().build();
    var pageable = mock(Pageable.class);

    when(factoryProvider.getReportFactory(AssignmentSummaryType.ARM)).thenReturn(armReportFactory);
    when(writerBuilder.buildWriter(writerParamsCaptor.capture())).thenReturn(writer);
    when(writer.writeToByteArray()).thenThrow(IOException.class);

    assertThrows(
        ReportGenerationException.class,
        () -> generator.generate(filters, pageable, armSummaryFinder));
  }
}

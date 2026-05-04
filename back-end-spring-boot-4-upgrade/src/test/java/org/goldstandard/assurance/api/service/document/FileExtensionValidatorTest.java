package org.goldstandard.assurance.api.service.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.util.Set;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.goldstandard.assurance.api.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class FileExtensionValidatorTest {

  private final FileExtensionProperties props = new FileExtensionProperties(Set.of("txt", "pdf"));

  private final FileExtensionValidator validator = new FileExtensionValidator(props);

  @ParameterizedTest
  @ValueSource(strings = {"test_file1", "test_file2."})
  void shouldThrowExceptionIfExtensionIsMissing(String filename) {
    var file = toMultipartFile(filename);

    Throwable error = catchThrowable(() -> validator.validateFileExtension(file));

    assertThat(error)
        .isInstanceOf(BadRequestException.class)
        .hasMessage("filename %s is missing extension", filename);
  }

  @Test
  void shouldThrowExceptionIfFilenameIsNull() {
    String filename = null;
    Throwable error = catchThrowable(() -> validator.validateFileExtension(filename));

    assertThat(error)
        .isInstanceOf(BadRequestException.class)
        .hasMessage("filename %s is missing extension", filename);
  }

  @ParameterizedTest
  @ValueSource(strings = {"bat", "exe", "BAT", "EXE"})
  void shouldThrowExceptionIfExtensionIsNotAllowed(String extension) {
    var filename = String.format("test_file.%s", extension);
    var file = toMultipartFile(filename);

    Throwable error = catchThrowable(() -> validator.validateFileExtension(file));

    assertThat(error)
        .isInstanceOf(BadRequestException.class)
        .hasMessage("file extension %s is not allowed", extension);
  }

  @ParameterizedTest
  @ValueSource(strings = {"test_file3.txt", "test_file.pdf", "test_file.PDF", "test_file.TXT"})
  void shouldDoNothingIfFilenameHasValidExtension(String filename) {
    var file = toMultipartFile(filename);

    ThrowingCallable call = () -> validator.validateFileExtension(file);

    assertThatCode(call).doesNotThrowAnyException();
  }

  private static MultipartFile toMultipartFile(String filename) {
    return new MockMultipartFile(filename, filename, "", "BLOB".getBytes());
  }
}

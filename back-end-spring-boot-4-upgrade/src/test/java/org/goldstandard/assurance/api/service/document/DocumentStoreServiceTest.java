package org.goldstandard.assurance.api.service.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.exception.ConflictException;
import org.goldstandard.assurance.api.exception.FileNotFoundInStoreException;
import org.goldstandard.assurance.api.exception.FileStoreException;
import org.goldstandard.assurance.api.repository.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@Slf4j
class DocumentStoreServiceTest {

  private static final String CONTAINER_NAME = "GS1234";
  private static final String FILENAME = "test_file.pdf";
  private static final String KEY = String.format("%s/%s", CONTAINER_NAME, FILENAME);
  private static final String ERROR_MESSAGE = "test message";
  private static final String BLOB = "test blob data";
  private static final MultipartFile FILE =
      new MockMultipartFile(FILENAME, FILENAME, "", BLOB.getBytes());

  @InjectMocks DocumentStoreService service;

  @Mock DocumentRepository repository;

  @Mock FileStore fileStore;

  @Test
  void uploadFileShouldThrowExceptionIfDocumentExistsInFileStore() {
    when(fileStore.objectExists(KEY)).thenReturn(true);

    Throwable error = catchThrowable(() -> service.uploadFile(CONTAINER_NAME, FILE));

    assertThat(error)
        .isInstanceOf(ConflictException.class)
        .hasMessage("filename %s is already in use for container %s", FILENAME, CONTAINER_NAME);
  }

  @Test
  void uploadFileShouldThrowExceptionIfFileStoreThrowsExceptionOnExists() {
    FileStoreException expectedError = new FileStoreException(ERROR_MESSAGE);
    when(fileStore.objectExists(KEY)).thenThrow(expectedError);

    Throwable error = catchThrowable(() -> service.uploadFile(CONTAINER_NAME, FILE));

    assertThat(error).isEqualTo(expectedError);
  }

  @Test
  void uploadFileShouldThrowExceptionIfDocumentExistsInDatabase() {
    when(repository.existsByFileNameAndContainerName(FILENAME, CONTAINER_NAME)).thenReturn(true);

    Throwable error = catchThrowable(() -> service.uploadFile(CONTAINER_NAME, FILE));

    assertThat(error)
        .isInstanceOf(ConflictException.class)
        .hasMessage("filename %s is already in use for container %s", FILENAME, CONTAINER_NAME);
  }

  @Test
  void uploadFileShouldReturnSumForUploadedFile() throws IOException {
    ArgumentMatcher<InputStream> matcher = new InputStreamMatcher(FILE.getInputStream());
    String expectedSum = "a58dd8680234c1f8cc2ef2b325a43733605a7f16f288e072de8eae81fd8d6433";
    when(fileStore.uploadStream(eq(KEY), argThat(matcher), eq(FILE.getSize())))
        .thenReturn(expectedSum);

    String result = service.uploadFile(CONTAINER_NAME, FILE);

    assertThat(result).isEqualTo(expectedSum);
  }

  @Test
  void uploadFileShouldThrowErrorOnStreamIOException() throws IOException {
    MultipartFile brokenFile = mock(MultipartFile.class);
    IOException expectedCause = new IOException(ERROR_MESSAGE);
    when(brokenFile.getInputStream()).thenThrow(expectedCause);

    Throwable error = catchThrowable(() -> service.uploadFile(CONTAINER_NAME, brokenFile));

    assertThat(error).isInstanceOf(FileStoreException.class).hasCause(expectedCause);
  }

  @Test
  void uploadFileShouldThrowExceptionIfFileStoreThrowsExceptionOnUpload() {
    FileStoreException expectedError = new FileStoreException(ERROR_MESSAGE);
    when(fileStore.uploadStream(eq(KEY), any(), eq(FILE.getSize()))).thenThrow(expectedError);

    Throwable error = catchThrowable(() -> service.uploadFile(CONTAINER_NAME, FILE));

    assertThat(error).isEqualTo(expectedError);
  }

  @Test
  void shouldRetrieveDocumentAsStream() throws IOException {
    when(fileStore.objectExists(KEY)).thenReturn(true);
    when(fileStore.getObjectAsStream(KEY)).thenReturn(new ByteArrayInputStream(BLOB.getBytes()));

    try (InputStream documentStream = service.retrieveDocumentAsStream(createDocument())) {
      assertThat(documentStream.readAllBytes()).isEqualTo(BLOB.getBytes());
    }
  }

  @Test
  void retrieveDocumentShouldThrowExceptionIfFileDoesNotExist() {
    when(fileStore.objectExists(KEY)).thenReturn(false);
    var document = createDocument();

    Throwable error = catchThrowable(() -> service.retrieveDocumentAsStream(document));

    assertThat(error)
        .isInstanceOf(FileNotFoundInStoreException.class)
        .hasMessage("file not found for key %s", KEY);
  }

  @Test
  void retrieveDocumentShouldThrowExceptionIfFileStoreThrowsException() {
    when(fileStore.objectExists(KEY)).thenReturn(true);
    FileStoreException expectedError = new FileStoreException(ERROR_MESSAGE);
    when(fileStore.getObjectAsStream(String.format(KEY, FILENAME))).thenThrow(expectedError);

    Throwable error = catchThrowable(() -> service.retrieveDocumentAsStream(createDocument()));

    assertThat(error).isEqualTo(expectedError);
  }

  private static Document createDocument() {
    return Document.builder()
        .id(UUID.fromString("1213f4af-0b0b-4862-8678-fcf043030d39"))
        .containerName(CONTAINER_NAME)
        .fileName(FILENAME)
        .build();
  }

  @RequiredArgsConstructor
  private static class InputStreamMatcher implements ArgumentMatcher<InputStream> {

    private final InputStream expectedStream;

    @Override
    public boolean matches(InputStream suppliedStream) {
      boolean matched = false;
      try {
        if (suppliedStream != null && expectedStream != null) {
          suppliedStream.mark(Integer.MAX_VALUE);
          expectedStream.mark(Integer.MAX_VALUE);
          matched = Arrays.equals(suppliedStream.readAllBytes(), expectedStream.readAllBytes());
          suppliedStream.reset();
          expectedStream.reset();
        }
      } catch (IOException e) {
        log.error(e.getMessage());
      }
      return matched;
    }
  }
}

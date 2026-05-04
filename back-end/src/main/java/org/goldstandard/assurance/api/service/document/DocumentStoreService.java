package org.goldstandard.assurance.api.service.document;

import static org.goldstandard.assurance.api.util.S3Utils.toKey;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.exception.ConflictException;
import org.goldstandard.assurance.api.exception.FileNotFoundInStoreException;
import org.goldstandard.assurance.api.exception.FileStoreException;
import org.goldstandard.assurance.api.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class DocumentStoreService {

  private final DocumentRepository repository;
  private final FileStore fileStore;

  public DocumentStoreService(
      DocumentRepository repository, @Qualifier("documentFileStore") FileStore fileStore) {
    this.repository = repository;
    this.fileStore = fileStore;
  }

  public String uploadFile(String containerName, MultipartFile file) {
    var filename = file.getOriginalFilename();
    validateFilenameNotAlreadyInUse(containerName, filename);
    var key = toKey(containerName, filename);
    try {
      return fileStore.uploadStream(key, file.getInputStream(), file.getSize());
    } catch (IOException e) {
      throw new FileStoreException(e.getMessage(), e);
    }
  }

  public InputStream retrieveDocumentAsStream(Document document) {
    validateFileExists(document);
    return forceRetrieveDocumentAsStream(document);
  }

  public InputStream forceRetrieveDocumentAsStream(Document document) {
    return retrieveDocumentAsStream(toKey(document));
  }

  public void validateFilesExist(Collection<Document> documents) {
    documents.forEach(this::validateFileExists);
  }

  public void validateFileExists(Document document) {
    var containerName = document.getContainerName();
    var filename = document.getFileName();
    var key = toKey(containerName, filename);
    validateFileExists(key);
  }

  public InputStream retrieveDocumentAsStream(String key) {
    log.info("Creating download stream {}", key);
    return fileStore.getObjectAsStream(key);
  }

  private void validateFilenameNotAlreadyInUse(String containerName, String filename) {
    if (isFilenameInUse(containerName, filename)) {
      throw new ConflictException(
          "filename %s is already in use for container %s", filename, containerName);
    }
  }

  private void validateFileExists(String key) {
    if (!doesFileExist(key)) {
      throw new FileNotFoundInStoreException(String.format("file not found for key %s", key));
    }
  }

  /**
   * Verify no document with that name already exists for this project (container name). TODO This
   * is not good as it potentially reveals a conflict with a document they shouldn't have access to
   * We need to move away from storing documents by filename and store by the UUID or other
   * mechanism instead.
   */
  private boolean isFilenameInUse(String containerName, String filename) {
    return repository.existsByFileNameAndContainerName(filename, containerName)
        || doesFileExist(toKey(containerName, filename));
  }

  private boolean doesFileExist(String key) {
    log.debug("checking file {} exists", key);
    return fileStore.objectExists(key);
  }
}

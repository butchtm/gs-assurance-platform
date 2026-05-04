package org.goldstandard.assurance.api.service.document;

import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.exception.BadRequestException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
public class FileExtensionValidator {

  private final Set<String> whitelist;

  public FileExtensionValidator(FileExtensionProperties props) {
    this.whitelist = props.getWhitelist();
  }

  public void validateFileExtension(MultipartFile file) {
    validateFileExtension(file.getOriginalFilename());
  }

  public void validateFileExtension(String filename) {
    var ext =
        extractExtension(filename)
            .orElseThrow(
                () -> new BadRequestException("filename %s is missing extension", filename));
    if (!isWhitelisted(ext.toLowerCase())) {
      throw new BadRequestException("file extension %s is not allowed", ext);
    }
  }

  private boolean isWhitelisted(String extension) {
    return whitelist.contains(extension);
  }

  private static Optional<String> extractExtension(String filename) {
    if (filename == null) {
      return Optional.empty();
    }
    var lastIndexOfDot = filename.lastIndexOf(".");
    if (lastIndexOfDot == -1) {
      return Optional.empty();
    }
    var extension = filename.substring(lastIndexOfDot + 1);
    if (extension.isBlank()) {
      return Optional.empty();
    }
    return Optional.of(extension);
  }
}

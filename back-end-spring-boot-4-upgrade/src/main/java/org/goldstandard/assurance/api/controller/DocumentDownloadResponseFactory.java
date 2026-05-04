package org.goldstandard.assurance.api.controller;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.exception.FileNotFoundInStoreException;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.goldstandard.assurance.api.service.document.DocumentStreamer;
import org.goldstandard.assurance.api.service.document.DocumentsZipFilenameFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RequiredArgsConstructor
@Component
public class DocumentDownloadResponseFactory {

  private final DocumentStreamer streamer;
  private final DocumentsZipFilenameFactory zipFilenameFactory;
  private final DownloadResponseHeaderPopulator headerPopulator;

  public void addDocumentToResponse(Document document, HttpServletResponse response) {
    try {
      headerPopulator.populate(response, document.getFileName());
      response.setContentType(document.getContentType());
      streamer.streamDocument(document, response);
    } catch (FileNotFoundInStoreException e) {
      // set the content type back to null if document not found in store
      // otherwise the spring exception handler gets confused by the content
      // type and throws additional confusing error messages
      response.setContentType(null);
      throw e;
    } catch (IOException e) {
      throw new NotFoundException(e.getMessage(), e);
    } catch (PermissionException e) {
      throw new NotFoundException(
          String.format("document not found with id %s", document.getId()), e);
    }
  }

  public ResponseEntity<StreamingResponseBody> toStreamResponse(
      Integer requestId, Collection<Document> documents, Integer displayCategoryId) {
    var filename = zipFilenameFactory.toFilename(requestId, displayCategoryId);
    return headerPopulator
        .populate(ResponseEntity.ok(), filename)
        .header("Content-Length-File-Count", Integer.toString(documents.size()))
        .contentType(MediaType.parseMediaType("application/zip"))
        .body(streamer.toZipStream(documents));
  }
}

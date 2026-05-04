package org.goldstandard.assurance.api.controller;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.stereotype.Component;

@Component
public class DownloadResponseHeaderPopulator {

  public void populate(HttpServletResponse response, String filename) {
    response.setHeader(ACCESS_CONTROL_EXPOSE_HEADERS, CONTENT_DISPOSITION);
    response.setHeader(CONTENT_DISPOSITION, toContentDisposition(filename));
  }

  public BodyBuilder populate(BodyBuilder builder, String filename) {
    return builder
        .header(ACCESS_CONTROL_EXPOSE_HEADERS, CONTENT_DISPOSITION)
        .header(CONTENT_DISPOSITION, toContentDisposition(filename));
  }

  private static String toContentDisposition(String filename) {
    return String.format("attachment; filename=%s", URLEncoder.encode(filename, UTF_8));
  }
}

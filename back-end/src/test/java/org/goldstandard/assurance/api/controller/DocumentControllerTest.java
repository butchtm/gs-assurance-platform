package org.goldstandard.assurance.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import org.goldstandard.assurance.api.service.document.DocumentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;

@ExtendWith(MockitoExtension.class)
class DocumentControllerTest {

  private static final Integer REQUEST_ID = 5352;
  private static final UUID DOCUMENT_ID = UUID.fromString("83e31c05-a2f2-49b2-befc-69fe12047efb");

  @InjectMocks DocumentController controller;
  @Mock DocumentService service;

  @Test
  void shouldCallServiceToDeleteDocumentFromRequest() {
    var response = controller.deleteDocumentFromRequest(REQUEST_ID, DOCUMENT_ID);

    verify(service).deleteRequestDocument(REQUEST_ID, DOCUMENT_ID);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
  }
}

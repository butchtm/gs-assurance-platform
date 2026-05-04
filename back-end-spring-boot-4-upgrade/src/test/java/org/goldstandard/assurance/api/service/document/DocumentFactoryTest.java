package org.goldstandard.assurance.api.service.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.function.Supplier;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.goldstandard.assurance.api.service.RequestTypeDocumentTypeService;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class DocumentFactoryTest {

  private static final String HASH = "document-hash";
  private static final int DOCUMENT_TYPE_ID = 5;

  @InjectMocks DocumentFactory factory;

  @Mock RequestTypeDocumentTypeService service;

  @Mock Clock clock;

  @Mock Supplier<UUID> uuidSupplier;

  @Mock AuthenticatedUserSupplier userSupplier;

  @BeforeEach
  void setUp() {
    givenTimeNow();
  }

  @Test
  void shouldPopulateHash() {
    var document = factory.createDocument(buildFile(), buildRequest(), DOCUMENT_TYPE_ID, HASH);

    assertThat(document.getDocumentHash()).isEqualTo(HASH);
  }

  @Test
  void shouldPopulateId() {
    var expectedId = UUID.fromString("d98f78aa-0ca4-42af-9d86-d849127dda58");
    when(uuidSupplier.get()).thenReturn(expectedId);

    var document = factory.createDocument(buildFile(), buildRequest(), DOCUMENT_TYPE_ID, HASH);

    assertThat(document.getId()).isEqualTo(expectedId);
  }

  @Test
  void shouldPopulatePublicAccessToFalse() {
    var document = factory.createDocument(buildFile(), buildRequest(), DOCUMENT_TYPE_ID, HASH);

    assertThat(document.getAccess()).isFalse();
  }

  @Test
  void shouldPopulateContainerNameAsGsId() {
    var project = buildProject();
    var request = buildRequest(project);

    var document = factory.createDocument(buildFile(), request, DOCUMENT_TYPE_ID, HASH);

    assertThat(document.getContainerName()).isEqualTo(project.getGsid());
  }

  @Test
  void shouldPopulateFileProperties() {
    var file = buildFile();

    var document = factory.createDocument(file, buildRequest(), DOCUMENT_TYPE_ID, HASH);

    assertThat(document.getFileName()).isEqualTo(file.getOriginalFilename());
    assertThat(document.getContentType()).isEqualTo(file.getContentType());
    assertThat(document.getFileSize()).isEqualTo(file.getSize());
  }

  @Test
  void shouldPopulateDocumentTypeId() {
    var document = factory.createDocument(buildFile(), buildRequest(), DOCUMENT_TYPE_ID, HASH);

    assertThat(document.getDocumentTypeId()).isEqualTo(DOCUMENT_TYPE_ID);
  }

  @Test
  void shouldPopulateModifiedAndUploadedTimestampsWithTimeNow() {
    var now = givenTimeNow();

    var document = factory.createDocument(buildFile(), buildRequest(), DOCUMENT_TYPE_ID, HASH);

    assertThat(document.getModifiedTimestamp()).isEqualTo(now);
    assertThat(document.getUploadedTimestamp()).isEqualTo(now);
  }

  @Test
  void shouldPopulateUploadedByWithAuthenticatedUser() {
    var user = givenAuthenticatedUser();

    var document = factory.createDocument(buildFile(), buildRequest(), DOCUMENT_TYPE_ID, HASH);

    assertThat(document.getUploadedBy()).contains(user);
  }

  @Test
  void shouldPopulateLatestToTrue() {
    var document = factory.createDocument(buildFile(), buildRequest(), DOCUMENT_TYPE_ID, HASH);

    assertThat(document.getIsLatest()).isTrue();
  }

  @Test
  void shouldPopulateCategoryToFalse() {
    var document = factory.createDocument(buildFile(), buildRequest(), DOCUMENT_TYPE_ID, HASH);

    assertThat(document.getCategory()).isFalse();
  }

  @Test
  void shouldPopulateDocumentCategoryType() {
    var request = buildRequest();
    int expectedCategoryTypeId = 10;
    when(service.findDocumentDisplayCategoryId(request.getRequestType().getId(), DOCUMENT_TYPE_ID))
        .thenReturn(expectedCategoryTypeId);

    var document = factory.createDocument(buildFile(), buildRequest(), DOCUMENT_TYPE_ID, HASH);

    assertThat(document.getDocumentCategoryType()).isEqualTo(expectedCategoryTypeId);
  }

  private ZonedDateTime givenTimeNow() {
    ZonedDateTime now = ZonedDateTime.now();
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(clock.instant()).thenReturn(now.toInstant());
    return now;
  }

  private User givenAuthenticatedUser() {
    var user = mock(User.class);
    when(userSupplier.forceGetAuthenticatedUser()).thenReturn(user);
    return user;
  }

  private static MultipartFile buildFile() {
    return new MockMultipartFile("test-file.txt", "some content".getBytes());
  }

  private static Request buildRequest() {
    return buildRequest(buildProject());
  }

  private static Request buildRequest(Project project) {
    return buildRequest(project, new RequestType(99));
  }

  private static Request buildRequest(Project project, RequestType requestType) {
    var request = new Request();
    request.setProject(project);
    request.setRequestType(requestType);
    return request;
  }

  private static Project buildProject() {
    var project = new Project();
    project.setGsid("GS1234");
    return project;
  }
}

package org.goldstandard.assurance.api;

import org.goldstandard.assurance.api.database.h2.Compatibility;
import org.goldstandard.assurance.api.populator.AccountPopulator;
import org.goldstandard.assurance.api.populator.AssignmentPopulator;
import org.goldstandard.assurance.api.populator.BundlePopulator;
import org.goldstandard.assurance.api.populator.CommentPopulator;
import org.goldstandard.assurance.api.populator.DocumentPopulator;
import org.goldstandard.assurance.api.populator.ProjectPopulator;
import org.goldstandard.assurance.api.populator.ReferenceDataPopulator;
import org.goldstandard.assurance.api.populator.RequestActivityPopulator;
import org.goldstandard.assurance.api.populator.RequestPopulator;
import org.goldstandard.assurance.api.populator.StakeholderConsultationPopulator;
import org.goldstandard.assurance.api.populator.UserBatchPopulator;
import org.goldstandard.assurance.api.populator.UserPopulator;
import org.goldstandard.assurance.api.service.document.FileStore;
import org.goldstandard.assurance.api.service.document.WritableFileStore;
import org.goldstandard.assurance.api.service.notification.EmailSender;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestConfiguration("org.goldstandard.assurance.api.populator")
abstract class AbstractApiTest {

  @MockitoBean(name = "documentFileStore")
  protected FileStore documentFileStore;

  @MockitoBean(name = "downloadFileStore")
  protected FileStore downloadFileStore;

  @MockitoBean(name = "salesOrderFileStore")
  protected WritableFileStore salesOrderFileStore;

  @MockitoBean(name = "projectContractFileStore")
  protected WritableFileStore projectContractFileStore;

  @MockitoBean protected EmailSender emailSender;

  @BeforeAll
  public static void setup(
      @Autowired ReferenceDataPopulator referenceDataPopulator,
      @Autowired UserBatchPopulator userBatchPopulator,
      @Autowired UserPopulator userPopulator,
      @Autowired AccountPopulator accountPopulator,
      @Autowired BundlePopulator bundlePopulator,
      @Autowired ProjectPopulator projectPopulator,
      @Autowired RequestActivityPopulator requestActivityPopulator,
      @Autowired RequestPopulator requestPopulator,
      @Autowired DocumentPopulator documentPopulator,
      @Autowired AssignmentPopulator assignmentPopulator,
      @Autowired Compatibility compatibility,
      @Autowired StakeholderConsultationPopulator consultationPopulator,
      @Autowired CommentPopulator commentPopulator) {

    compatibility.patch();

    if (!referenceDataPopulator.isPopulated()) {
      referenceDataPopulator.populate();
    }

    if (!userBatchPopulator.isPopulated()) {
      userBatchPopulator.populate();
    }

    if (!accountPopulator.isPopulated()) {
      accountPopulator.populate();
    }

    if (!userPopulator.isPopulated()) {
      userPopulator.populate();
    }

    if (!bundlePopulator.isPopulated()) {
      bundlePopulator.populate();
    }

    if (!projectPopulator.isPopulated()) {
      projectPopulator.populate();
    }

    if (!requestActivityPopulator.isPopulated()) {
      requestActivityPopulator.populate();
    }

    if (!requestPopulator.isPopulated()) {
      requestPopulator.populate();
    }

    if (!documentPopulator.isPopulated()) {
      documentPopulator.populate();
    }

    if (!assignmentPopulator.isPopulated()) {
      assignmentPopulator.populate();
    }

    if (!consultationPopulator.isPopulated()) {
      consultationPopulator.populate();
    }

    if (!commentPopulator.isPopulated()) {
      commentPopulator.populate();
    }
  }
}

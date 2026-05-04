package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static uk.co.mruoc.file.FileLoader.loadContentFromClasspath;

import org.goldstandard.assurance.api.helper.RequestHelper;
import org.goldstandard.assurance.api.populator.ProjectPopulator;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.xmlunit.assertj.XmlAssert;

@Import(RequestHelper.class)
class FinanceApiIntegrationTest extends AbstractApiTest {

  @Autowired ProjectPopulator projectPopulator;
  @Autowired RequestHelper requestHelper;
  @Autowired ApiClient client;

  @Captor ArgumentCaptor<String> path;
  @Captor ArgumentCaptor<String> content;

  @Test
  void shouldWriteRequestSalesOrderXmlToFileStore() {
    var project = projectPopulator.populateProject(5);
    var request = requestHelper.createTestRequestWithRequestStatusAndType(project, 6, false, 1001);
    try {
      var response = client.issueRequestSalesOrder(request.getId());

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

      verify(salesOrderFileStore).uploadString(path.capture(), content.capture());
      assertThat(path.getValue())
          .startsWith(String.format("Inprocess/sales-order-%d-", request.getId()))
          .endsWith(".xml");
      XmlAssert.assertThat(content.getValue())
          .and(
              loadContentFromClasspath("xml/integration-test/expected-sales-order.xml")
                  .replaceAll("%ACCOUNT_ID%", Integer.toString(project.getAccountId()))
                  .replaceAll("%GSID%", project.getGsid())
                  .replaceAll("%REQUEST_ID%", Integer.toString(request.getId())))
          .ignoreWhitespace()
          .areIdentical();
    } finally {
      requestHelper.deleteTestRequest(request);
      projectPopulator.deleteProjects(project);
    }
  }

  @Test
  void shouldWriteProjectContractXmlToFileStore() {
    var project = projectPopulator.populateProject(6);
    try {
      var response = client.issueProjectContract(project.getGsid());

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

      verify(projectContractFileStore).uploadString(path.capture(), content.capture());
      assertThat(path.getValue())
          .startsWith(String.format("Inprocess/project-contract-%s-", project.getGsid()))
          .endsWith(".xml");
      XmlAssert.assertThat(content.getValue())
          .and(
              loadContentFromClasspath("xml/integration-test/expected-project-contract.xml")
                  .replaceAll("%GSID%", project.getGsid())
                  .replaceAll("%PROJECT_NAME%", project.getName())
                  .replaceAll("%PROJECT_DESCRIPTION%", project.getDescription())
                  .replaceAll("%ACCOUNT_ID%", Integer.toString(project.getAccountId())))
          .ignoreWhitespace()
          .withAttributeFilter((attr) -> !attr.getName().equals("REQUESTID"))
          .areIdentical();
    } finally {
      projectPopulator.deleteProjects(project);
    }
  }
}

package org.goldstandard.assurance.api.service.finance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.service.ProjectFinder;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.goldstandard.assurance.api.service.document.WritableFileStore;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinanceService {

  private final FinanceObjectPathFactory pathFactory;

  private final RequestFinder requestFinder;
  private final SalesOrderFactory salesOrderFactory;
  private final WritableFileStore salesOrderFileStore;

  private final ProjectFinder projectFinder;
  private final ProjectContractFactory projectContractFactory;
  private final WritableFileStore projectContractFileStore;

  public void triggerProjectContractIfPossible(String gsId) {
    var project = projectFinder.forceGetProjectByGsId(gsId);
    triggerProjectContractIfPossible(project);
  }

  public void triggerProjectContractIfPossible(Integer id) {
    var project = projectFinder.forceGetProjectById(id);
    triggerProjectContractIfPossible(project);
  }

  public void triggerRequestSalesOrderIfPossible(Integer requestId) {
    var request = requestFinder.getRequestById(requestId);
    triggerRequestSalesOrderIfPossible(request);
  }

  public void triggerRequestSalesOrderIfPossible(Request request) {
    salesOrderFactory
        .buildSalesOrderXml(request)
        .ifPresent(xml -> triggerRequestSalesOrder(request.getId(), xml));
  }

  private void triggerProjectContractIfPossible(Project project) {
    projectContractFactory
        .buildProjectContractXml(project)
        .ifPresent(xml -> triggerProjectContract(project, xml));
  }

  private void triggerRequestSalesOrder(Integer requestId, String xml) {
    var path = pathFactory.toSalesOrderPath(requestId);
    salesOrderFileStore.uploadString(path, xml);
  }

  private void triggerProjectContract(Project project, String xml) {
    var path = pathFactory.toProjectContractPath(project.getGsid());
    projectContractFileStore.uploadString(path, xml);
  }
}

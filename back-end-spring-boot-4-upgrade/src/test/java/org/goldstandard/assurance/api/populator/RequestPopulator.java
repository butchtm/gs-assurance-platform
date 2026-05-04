package org.goldstandard.assurance.api.populator;

import static org.goldstandard.assurance.api.populator.ProjectPopulator.TEST_PROJECTS;
import static org.goldstandard.assurance.api.populator.RequestActivityPopulator.TEST_REQUEST_ACTIVITIES;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.goldstandard.assurance.api.entity.Invoice;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.InvoiceStatus;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.goldstandard.assurance.api.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RequestPopulator {

  public static final int NUMBER_OF_REQUESTS_PER_PROJECT = 2;
  public static final ZonedDateTime FIXED_DATE = ZonedDateTime.now();
  // drop timestamp resolution to h2 resolution
  public static final ZonedDateTime TEST_INVOICE_FIXED_DATE = FIXED_DATE.withNano(0);
  public static final InvoiceStatus TEST_INVOICE_STATUS = new InvoiceStatus(2);

  public static List<Request> TEST_REQUESTS;

  @Autowired private RequestRepository requestRepository;

  public boolean isPopulated() {
    return requestRepository.count() > 0;
  }

  public void populate() {
    var requests = new ArrayList<Request>();
    var requestIdCounter = 1;
    for (int i = 1; i <= TEST_PROJECTS.size(); i++) {
      for (int j = 1; j <= NUMBER_OF_REQUESTS_PER_PROJECT; j++) {
        var project = TEST_PROJECTS.get(i - 1);
        var request = new Request();
        requestIdCounter++;
        request.setBookingDate(FIXED_DATE);
        request.setCreditingPeriod(1);
        request.setIndex(j - 1);
        request.setRequestType(new RequestType(999 + j));
        request.setEmissionReductionAmount(i * 10);
        request.setEndDate(FIXED_DATE);
        request.setLastRequestStatusChangeDate(FIXED_DATE);
        request.setManuallyAdded(true);
        request.setRequestDocumentStatus(i);
        request.setRequestName("review name " + i);
        request.setSameRequestTypeCount(i);
        request.setStartDate(FIXED_DATE);
        // Alternate odd and even requests to be IN_PROGRESS or NOT_REQUESTED
        request.setCurrentRequestStatus(new RequestStatus(j % 2 == 0 ? 10 : 1));
        request.setProject(project);

        var invoice =
            Invoice.builder()
                .status(TEST_INVOICE_STATUS)
                .statusChangedAt(TEST_INVOICE_FIXED_DATE)
                .amount(new BigDecimal(i * 300))
                .isArchived(true)
                .build();
        request.setInvoice(invoice);

        if (requestIdCounter % 2 == 0) {
          request.setCurrentActivity(TEST_REQUEST_ACTIVITIES.get((i / 2)));
        }

        requests.add(request);
      }
    }

    TEST_REQUESTS = IterableUtils.toList(requestRepository.saveAll(requests));
    TEST_REQUESTS.forEach(this::logRequest);
  }

  private void logRequest(Request request) {
    var loadedRequest = requestRepository.findById(request.getId()).orElseThrow();
    var project = loadedRequest.getProject();
    var status = loadedRequest.getCurrentRequestStatus();
    var type = loadedRequest.getRequestType();
    var invoice = loadedRequest.getInvoice();
    log.info(
        "populated request {} with type {} and status {} for project {} {} with account {} with invoice {}",
        loadedRequest.getId(),
        type.getId(),
        status.getDescription(),
        project.getId(),
        project.getGsid(),
        project.getAccountId(),
        invoice.getId());
  }
}

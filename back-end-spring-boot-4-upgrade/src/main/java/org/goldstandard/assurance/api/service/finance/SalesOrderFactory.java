package org.goldstandard.assurance.api.service.finance;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.finance.XmlMapperFactory;
import org.goldstandard.assurance.api.dto.finance.salesorder.SalesOrder;
import org.goldstandard.assurance.api.dto.finance.salesorder.SalesOrderHeader;
import org.goldstandard.assurance.api.dto.finance.salesorder.SalesOrderLine;
import org.goldstandard.assurance.api.entity.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Component
@Slf4j
public class SalesOrderFactory {

  private final ObjectMapper mapper;
  private final FinanceInvoiceTypeFactory invoiceTypeFactory;

  @Autowired
  public SalesOrderFactory(FinanceInvoiceTypeFactory invoiceTypeFactory) {
    this(XmlMapperFactory.build(), invoiceTypeFactory);
  }

  public Optional<String> buildSalesOrderXml(Request request) {
    return toSalesOrder(request).map(this::toXml);
  }

  private Optional<SalesOrder> toSalesOrder(Request request) {
    return invoiceTypeFactory
        .toInvoiceType(request)
        .map(invoiceType -> toSalesOrder(request, invoiceType));
  }

  private SalesOrder toSalesOrder(Request request, String invoiceType) {
    var project = request.getProject();
    log.debug(
        "building sales order for request {} and project {} with invoice type {}",
        request.getId(),
        project.getGsid(),
        invoiceType);
    var header =
        SalesOrderHeader.builder()
            .accountId(project.getAccountId())
            .gsid(project.getGsid())
            .requestId(request.getId())
            .line(new SalesOrderLine(invoiceType))
            .build();
    return new SalesOrder(header);
  }

  private String toXml(SalesOrder salesOrder) {
    try {
      return mapper.writeValueAsString(salesOrder);
    } catch (JacksonException e) {
      throw new SalesOrderCreationFailedException(salesOrder.getRequestId(), e);
    }
  }
}

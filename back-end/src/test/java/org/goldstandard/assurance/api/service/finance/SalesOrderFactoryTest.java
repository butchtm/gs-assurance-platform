package org.goldstandard.assurance.api.service.finance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.goldstandard.assurance.api.dto.finance.XmlMapperFactory;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xmlunit.assertj.XmlAssert;
import uk.co.mruoc.file.FileLoader;

class SalesOrderFactoryTest {

  private static final ObjectMapper MAPPER = XmlMapperFactory.build();

  private final FinanceInvoiceTypeFactory invoiceTypeFactory =
      mock(FinanceInvoiceTypeFactory.class);

  private SalesOrderFactory salesOrderFactory;

  @BeforeEach
  void setUp() {
    salesOrderFactory = new SalesOrderFactory(MAPPER, invoiceTypeFactory);
  }

  @Test
  void shouldCreateXmlSalesOrderForConfiguredRequestType() {
    var request = buildRequest(buildRequestType("PRELIMINARY_REVIEW"));
    when(invoiceTypeFactory.toInvoiceType(request))
        .thenReturn(Optional.of("Preliminary Review (listing)"));

    var xml = salesOrderFactory.buildSalesOrderXml(request);

    assertThat(xml).isPresent();
    XmlAssert.assertThat(xml.get())
        .and(FileLoader.loadContentFromClasspath("xml/unit-test/expected-sales-order.xml"))
        .ignoreWhitespace()
        .areIdentical();
  }

  @Test
  void shouldNotCreateXmlSalesOrderForNonConfiguredRequestType() {
    var request = buildRequest(buildRequestType("NOT_CONFIGURED"));
    when(invoiceTypeFactory.toInvoiceType(request)).thenReturn(Optional.empty());

    var xml = salesOrderFactory.buildSalesOrderXml(request);

    assertThat(xml).isEmpty();
  }

  private static Request buildRequest(RequestType requestType) {
    return Request.builder().id(67891).project(buildProject()).requestType(requestType).build();
  }

  private static Project buildProject() {
    return Project.builder().gsid("GS12345").accountId(123).build();
  }

  private static RequestType buildRequestType(String code) {
    var type = new RequestType();
    type.setCode(code);
    return type;
  }
}

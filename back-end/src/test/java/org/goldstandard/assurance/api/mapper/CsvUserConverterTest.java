package org.goldstandard.assurance.api.mapper;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.goldstandard.assurance.api.converter.CsvUserConverter;
import org.junit.jupiter.api.Test;

class CsvUserConverterTest {

  private final CsvUserConverter mapper = new CsvUserConverter();

  @Test
  void shouldCreateUsers() throws IOException {
    int numberOfRequestLines = 2;
    var csv = buildCsv(numberOfRequestLines);

    InputStream stream = new ByteArrayInputStream(csv.getBytes(UTF_8));

    var requests = mapper.toCreateUserRequests(stream);

    assertThat(requests).hasSize(numberOfRequestLines);
    for (var r : requests) {
      assertThat(r.getUserName()).startsWith("userName");
      assertThat(r.getFirstName()).startsWith("firstName");
      assertThat(r.getLastName()).startsWith("lastName");
      assertThat(r.getEmail()).startsWith("username").endsWith("@email.com");
      assertThat(r.getGroups()).hasSize(2);
      assertThat(r.getAccountId()).contains(1);
    }
  }

  private String buildCsv(int count) {
    var header = "userName,firstName,lastName,email,groups,accountId";
    var data = IntStream.range(0, count).mapToObj(CsvUserConverterTest::toDataLine);
    return Stream.concat(Stream.of(header), data).collect(Collectors.joining("\n"));
  }

  private static String toDataLine(int i) {
    var template = "userName%d,firstName%d,lastName%d,username%d@email.com,group-1~group-2,1";
    return String.format(template, i, i, i, i);
  }
}

package org.goldstandard.assurance.api.converter;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.CsvUserDTO;
import org.goldstandard.assurance.api.entity.UpsertUserRequest;

@RequiredArgsConstructor
public class CsvUserConverter {
  private final CsvMapper mapper;

  public CsvUserConverter() {
    this(new CsvMapper());
  }

  public Collection<UpsertUserRequest> toCreateUserRequests(InputStream inputStream)
      throws IOException {
    return toCreateUserRequests(toCsvUsers(inputStream));
  }

  private static Collection<UpsertUserRequest> toCreateUserRequests(Collection<CsvUserDTO> lines) {
    return lines.stream().map(CsvUserConverter::toCreateUserRequest).toList();
  }

  private Collection<CsvUserDTO> toCsvUsers(InputStream inputStream) throws IOException {
    CsvSchema schema = mapper.schemaFor(CsvUserDTO.class).withHeader();
    MappingIterator<CsvUserDTO> iterator =
        mapper.readerFor(CsvUserDTO.class).with(schema).readValues(inputStream);
    return iterator.readAll();
  }

  private static UpsertUserRequest toCreateUserRequest(CsvUserDTO line) {
    return UpsertUserRequest.builder()
        .userName(line.getUserName())
        .accountId(line.getAccountId())
        .firstName(line.getFirstName())
        .lastName(line.getLastName())
        .email(line.getEmail())
        .groups(Set.of(line.getGroups().split("~")))
        .build();
  }
}

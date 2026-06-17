package org.goldstandard.assurance.api.service;

import static org.springframework.data.domain.Sort.Direction.ASC;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.converter.RequestTypeConverter;
import org.goldstandard.assurance.api.dto.RequestTypeDTO;
import org.goldstandard.assurance.api.repository.RequestTypeRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestTypeService {
  private final RequestTypeRepository repository;
  private final RequestTypeConverter converter;

  public List<RequestTypeDTO> getAll() {
    return repository.findAll(sortByIdAsc()).stream().map(converter::toDTO).toList();
  }

  private static Sort sortByIdAsc() {
    return Sort.by(ASC, "id");
  }
}

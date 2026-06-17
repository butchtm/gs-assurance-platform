package org.goldstandard.assurance.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import org.goldstandard.assurance.api.converter.RequestTypeConverter;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.goldstandard.assurance.api.repository.RequestTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class RequestTypeServiceTest {
  @InjectMocks RequestTypeService requestTypeService;
  @Mock RequestTypeRepository repository;
  @Spy RequestTypeConverter converter;

  @Test
  void getAllShouldConvertToDTO() {
    var id = 1;
    var requestType = RequestType.builder().id(id).build();

    when(repository.findAll(any(Sort.class))).thenReturn(List.of(requestType));

    var dtos = requestTypeService.getAll();

    assertEquals(1, dtos.size());
    assertEquals(id, dtos.getFirst().getId());
  }
}

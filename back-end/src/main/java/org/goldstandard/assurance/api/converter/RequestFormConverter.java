package org.goldstandard.assurance.api.converter;

import java.util.Collection;
import org.goldstandard.assurance.api.dto.RequestFormDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.entity.RequestForm;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class RequestFormConverter {

  public PagedResource<RequestFormDTO> toPagedResource(Page<RequestForm> page) {
    return new PagedResource<>(page.map(this::toDTO));
  }

  public Collection<RequestFormDTO> toDTOs(Collection<RequestForm> forms) {
    return forms.stream().map(this::toDTO).toList();
  }

  public RequestFormDTO toDTO(RequestForm form) {
    return RequestFormDTO.builder()
        .id(form.getId())
        .version(form.getVersion())
        .type(form.getType())
        .schema(form.getSchema())
        .data(form.getData())
        .status(form.getStatus())
        .createdAt(form.getCreatedAt())
        .updatedAt(form.getUpdatedAt())
        .submittedAt(form.getSubmittedAt())
        .build();
  }
}

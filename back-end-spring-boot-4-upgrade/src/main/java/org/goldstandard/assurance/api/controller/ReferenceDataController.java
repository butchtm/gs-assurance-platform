package org.goldstandard.assurance.api.controller;

import java.util.List;
import java.util.Map;
import org.goldstandard.assurance.api.entity.lookup.ExtendedReferenceDataType;
import org.goldstandard.assurance.api.service.ReferenceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReferenceDataController {

  @Autowired private ReferenceDataService referenceDataService;

  @GetMapping("/reference-data-types")
  public Map<String, List<ExtendedReferenceDataType>> getReferenceDataTypes(
      @RequestParam(required = false) Boolean active) {
    return referenceDataService.getAllItems(active);
  }
}

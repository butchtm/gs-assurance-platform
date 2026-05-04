package org.goldstandard.assurance.api.dto;

public record DocumentUpdate(
    Boolean access, Boolean category, Integer documentTypeId, Boolean isLatest) {}

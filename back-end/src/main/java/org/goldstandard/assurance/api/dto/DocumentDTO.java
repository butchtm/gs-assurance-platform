package org.goldstandard.assurance.api.dto;

import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.goldstandard.assurance.api.dto.lookup.AccountTypeDTO;

@Getter
@Builder
public class DocumentDTO {
  private UUID id;
  private String containerName;
  private String fileName;
  private Long fileSize;
  private byte[] hashValue;
  private Boolean access;
  private Boolean category;
  private Integer documentTypeId;
  private String contentType;
  private String documentHash;
  private ZonedDateTime uploadedTimestamp;
  private ZonedDateTime modifiedTimestamp;
  private Boolean isLatest;
  private Boolean isMissing;
  private Integer documentCategoryType;
  private String piiScanStatus;
  private String piiAction;
  private AccountTypeDTO uploadedByAccountType;
}

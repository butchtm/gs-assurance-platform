import React from "react";
import { Box } from "@mui/material";

import { DocumentPiiScanStatus } from "../../../../common/document/GSDocument";

const PILL_SCAN_STATUSES = [DocumentPiiScanStatus.IN_PROGRESS, DocumentPiiScanStatus.FAILED];

const PiiScanStatusMessage = {
  IN_PROGRESS: "PII SCAN IN PROGRESS",
  COMPLETED_NO_PII: "",
  COMPLETED_PII_FOUND: "IDENTIFIED PERSONAL INFORMATION (PII)",
  FAILED: "PII IDENTIFICATION NOT POSSIBLE",
};

export const DocumentPiiScanPill: React.FC<{
  scanStatus: DocumentPiiScanStatus;
}> = ({ scanStatus }) => {
  if (!PILL_SCAN_STATUSES.includes(scanStatus)) {
    return <></>;
  }
  return (
    <Box
      sx={{
        display: "inline-flex",
        borderRadius: "5px",
        padding: "1px 6px",
        alignItems: "center",
        marginLeft: "11px",
        fontSize: "13px",
        color: "#fff",
        backgroundColor: "gray",
      }}
      data-testid="document-pii-warning-pill"
    >
      {PiiScanStatusMessage[scanStatus]}
    </Box>
  );
};

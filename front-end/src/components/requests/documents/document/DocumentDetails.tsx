import { Box } from "@mui/material";
import dayjs from "dayjs";
import React from "react";

import { useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { GSDocument } from "../../../../common/document/GSDocument";
import { GSRequest } from "../../../../common/request/GSRequest";

import { DocumentAccess } from "./DocumentAccess";
import { DocumentPiiWarning } from "./DocumentPiiWarning";

export const DocumentDetails: React.FC<{
  document: GSDocument;
  request: GSRequest;
  form?: boolean;
  onDocumentModify: () => void;
}> = ({ document, request, form, onDocumentModify }) => {
  const { allReferenceTypes } = useReferenceTypes();

  const documentType = allReferenceTypes.DocumentType.find((docType) => docType.id === document.documentTypeId);
  return (
    <Box sx={{ mr: "10px" }}>
      <Box sx={{ display: "flex", justifyContent: "flex-start" }}>
        <span style={{ fontWeight: "bold" }}>
          {form ? `${documentType?.description} Document` : documentType?.description}
        </span>
        <DocumentAccess access={document.access} />
        <DocumentPiiWarning document={document} request={request} onDocumentModify={onDocumentModify} />
      </Box>
      <Box sx={{ display: "flex", flexDirection: "column", textAlign: "left" }}>
        <span>
          <b>Filename:</b> {document.fileName}
        </span>
        <span>
          <b>Uploaded:</b> {dayjs.utc(document.uploadedTimestamp).format("DD/MM/YYYY HH:mm")}
        </span>
        <span>
          <b>Modified:</b> {dayjs.utc(document.modifiedTimestamp).format("DD/MM/YYYY HH:mm")}
        </span>
      </Box>
    </Box>
  );
};

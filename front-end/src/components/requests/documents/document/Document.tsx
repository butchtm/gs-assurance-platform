import { Box } from "@mui/material";

import { GSDocument } from "../../../../common/document/GSDocument";
import { GSRequest } from "../../../../common/request/GSRequest";
import { DocumentHistory } from "../document-history/DocumentHistory";

import { DocumentActions } from "./DocumentActions";
import { DocumentDetails } from "./DocumentDetails";

export const Document = ({
  document,
  history,
  request,
  onDocumentModify,
}: {
  document: GSDocument;
  history?: GSDocument[];
  request: GSRequest;
  onDocumentModify: () => void;
}) => {
  return (
    <Box
      key={document.id}
      sx={{
        marginBottom: "8px",
        padding: 1.5,
        borderRadius: "5px",
        backgroundColor: "rgb(235, 250, 250);",
      }}
    >
      <Box sx={{ display: "flex", justifyContent: "space-between" }}>
        <DocumentDetails document={document} request={request} onDocumentModify={onDocumentModify} />
        <DocumentActions document={document} request={request} onDocumentModify={onDocumentModify} />
      </Box>
      {history && history.length > 0 && (
        <DocumentHistory documents={history} request={request} onDocumentModify={onDocumentModify} />
      )}
    </Box>
  );
};

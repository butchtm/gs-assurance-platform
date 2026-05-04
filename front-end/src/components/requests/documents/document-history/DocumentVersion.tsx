import { Box, Divider } from "@mui/material";
import dayjs from "dayjs";

import { GSDocument } from "../../../../common/document/GSDocument";
import { GSRequest } from "../../../../common/request/GSRequest";
import { DocumentAccess } from "../document/DocumentAccess";
import { DocumentActions } from "../document/DocumentActions";
import { DocumentPiiWarning } from "../document/DocumentPiiWarning";

export const DocumentVersion = ({
  document,
  request,
  onDocumentModify,
}: {
  document: GSDocument;
  request: GSRequest;
  onDocumentModify: () => void;
}) => {
  return (
    <Box
      sx={{
        display: "flex",
        justifyContent: "space-between",
        marginBottom: "15px",
        padding: "10px",
        backgroundColor: "var(--color-surface-neutral)",
        borderRadius: "5px",
      }}
    >
      <Box
        sx={{
          width: "fit-content",
          display: "flex",
          flexDirection: "column",
          textAlign: "left",
        }}
      >
        <Box>
          <span style={{ fontWeight: "bold" }}>
            {dayjs.utc(document.modifiedTimestamp).format("D MMMM YYYY HH:mm")}
          </span>
          <DocumentAccess access={document.access} />
          <DocumentPiiWarning document={document} request={request} onDocumentModify={onDocumentModify} />
        </Box>
        <Divider sx={{ borderWidth: "1px", margin: "4px 0" }} />
        <span>
          <b>Filename:</b> {document.fileName}
        </span>
        <span>
          <b>Uploaded:</b> {dayjs.utc(document.uploadedTimestamp).format("DD/MM/YYYY HH:mm")}
        </span>
      </Box>
      <DocumentActions document={document} request={request} onDocumentModify={onDocumentModify} />
    </Box>
  );
};

import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { Box, IconButton } from "@mui/material";
import { useState } from "react";

import { GSDocument } from "../../../../common/document/GSDocument";
import { GSRequest } from "../../../../common/request/GSRequest";

import { DocumentVersion } from "./DocumentVersion";

export const DocumentHistory = ({
  documents,
  request,
  onDocumentModify,
}: {
  documents: GSDocument[];
  request: GSRequest;
  onDocumentModify: () => void;
}) => {
  const [expanded, setExpanded] = useState<boolean>();

  return (
    <Box>
      <Box sx={{ display: "flex", cursor: "pointer" }} onClick={() => setExpanded(!expanded)}>
        <IconButton
          sx={(theme) => ({
            transform: !expanded ? "rotate(-90deg)" : "rotate(0deg)",
            transition: theme.transitions.create("transform", {
              duration: theme.transitions.duration.shortest,
            }),
          })}
        >
          <ExpandMoreIcon data-testid="expand-history" />
        </IconButton>
        <span style={{ margin: "auto 0", fontWeight: "bold" }}>Document History</span>
      </Box>
      {expanded &&
        documents.map((document) => (
          <DocumentVersion
            key={document.id}
            document={document}
            request={request}
            onDocumentModify={onDocumentModify}
          />
        ))}
    </Box>
  );
};

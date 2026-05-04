import { MenuBook } from "@mui/icons-material";
import { Box, Button } from "@mui/material";
import { useState } from "react";

import { GSDocument } from "../../../../common/document/GSDocument";
import { GSRequest } from "../../../../common/request/GSRequest";
import { ReviewDocumentPiiDialog } from "../../documents/review-document-pii-dialog/ReviewDocumentPiiDialog";

export const DocumentReviewPiiAction = (props: {
  request: GSRequest;
  document: GSDocument;
  onDocumentModify: () => void;
}) => {
  const [isDialogOpen, setIsDialogOpen] = useState(false);

  const handleOpenDialog = () => {
    setIsDialogOpen(true);
  };

  const handleCloseDialog = () => {
    setIsDialogOpen(false);
  };

  return (
    <Box
      sx={{
        display: "inline-flex",
        borderRadius: "5px",
        padding: "0",
        alignItems: "center",
        marginLeft: "11px",
        fontSize: "13px",
        color: "#fff",
        backgroundColor: "white",
      }}
    >
      <Button onClick={handleOpenDialog} sx={{ padding: "0 6px" }} aria-haspopup="dialog">
        Review PII &nbsp;
        <MenuBook />
      </Button>
      {isDialogOpen && (
        <ReviewDocumentPiiDialog
          isOpen={isDialogOpen}
          onClose={handleCloseDialog}
          request={props.request}
          document={props.document}
          onDocumentModify={props.onDocumentModify}
        />
      )}
    </Box>
  );
};

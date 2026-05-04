import { Box, Dialog, DialogActions, DialogContent, DialogTitle, Typography } from "@mui/material";
import Button from "@mui/material/Button";
import React from "react";
import { toast } from "react-toastify";

import { GSDocument } from "../../../../common/document/GSDocument";
import { GSRequest } from "../../../../common/request/GSRequest";

import { deleteDocument } from "./DeleteRequestDocument";

interface DeleteDocumentDialogProps {
  isOpen: boolean;
  request: GSRequest;
  document: GSDocument;
  onClose: () => void;
}

const DeleteDocumentDialog: React.FC<DeleteDocumentDialogProps> = ({ isOpen, request, document, onClose }) => {
  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    const deleteToast = toast.info("Deleting document...", {
      autoClose: false,
      closeButton: false,
      draggable: false,
      closeOnClick: false,
    });

    const response = await deleteDocument(document?.id, request?.id);

    if (!response.success) {
      const errMsg = "Failed to delete document: ";
      toast.update(deleteToast, {
        render: errMsg + response.errorMessage,
        type: toast.TYPE.ERROR,
        autoClose: 5000,
        closeButton: true,
      });
      return;
    }

    toast.update(deleteToast, {
      render: "Document has been deleted successfully",
      type: toast.TYPE.SUCCESS,
      autoClose: 3000,
      closeButton: true,
    });
    onClose();
  };

  return (
    <Dialog open={isOpen} onClose={onClose} aria-labelledby="confirm-delete-dialog-title">
      <DialogTitle id="confirm-delete-dialog-title">Confirm File Deletion</DialogTitle>
      <DialogContent>
        <Box sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
          <Box>
            <Typography component="span" variant="body1" sx={{ fontWeight: "bold", mr: 2 }}>
              File Name:
            </Typography>
            <Typography component="span" variant="body1">
              {document.fileName}
            </Typography>
          </Box>
          <Box>
            <Typography component="span" variant="body1" sx={{ fontWeight: "bold", mr: 2 }}>
              Request Name:
            </Typography>
            <Typography component="span" variant="body1">
              {request.reviewName}
            </Typography>
          </Box>
          <Box>
            <Typography component="span" variant="body1">
              Are you sure you want to delete this file?
            </Typography>
          </Box>
        </Box>
      </DialogContent>
      <DialogActions>
        <Button variant="outlined" onClick={onClose}>
          Cancel
        </Button>
        <Button variant="contained" onClick={handleSubmit} autoFocus aria-label="Delete">
          Delete
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default DeleteDocumentDialog;

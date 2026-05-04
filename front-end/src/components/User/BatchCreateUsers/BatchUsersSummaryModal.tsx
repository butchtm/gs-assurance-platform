import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import { Box, Button, CircularProgress, Dialog, DialogActions, DialogContent, DialogTitle } from "@mui/material";
import React, { ChangeEvent } from "react";
import { toast } from "react-toastify";

import { BatchUserResponse } from "../User";

import { BatchUsersSummaryFailuresTable } from "./BatchUsersSummaryFailuresTable";

export const BatchUsersSummaryModal: React.FC<{
  isOpen: boolean;
  onClose: () => void;
  isLoading: boolean;
  batchUserResponse?: BatchUserResponse;
  onBatchedUsersUploaded: (file: File) => void;
}> = ({ isOpen, onClose, isLoading, batchUserResponse, onBatchedUsersUploaded }) => {
  const handleFileSelected = (event: ChangeEvent<HTMLInputElement>) => {
    if (!event.target.files) {
      toast.error("Unable to read CSV, please try again");
      console.error("Files array was empty");
      return;
    }

    onBatchedUsersUploaded(event.target.files[0]);
  };

  const buildDialogContent = () => {
    if (isLoading) {
      return (
        <Box sx={{ width: "340px", textAlign: "center", margin: "50px" }}>
          <CircularProgress size={100} />
        </Box>
      );
    }

    if (!batchUserResponse) {
      return <p>Error uploading batch!</p>;
    }

    return (
      <>
        {batchUserResponse.count} uploaded - {batchUserResponse.successCount} successful,{" "}
        {batchUserResponse.errors.length} failed
        {batchUserResponse.errors.length > 0 && (
          <Box sx={{ paddingTop: "32px" }}>
            <b>Failed Users</b>
            <Box sx={{ overflowY: "scroll", maxHeight: "300px" }}>
              <BatchUsersSummaryFailuresTable failedUsers={batchUserResponse.errors} />
            </Box>
          </Box>
        )}
      </>
    );
  };

  return (
    <Dialog open={isOpen} onClose={onClose}>
      <DialogTitle>Batch Creation Status</DialogTitle>
      <DialogContent>{buildDialogContent()}</DialogContent>

      <DialogActions>
        {!isLoading && (
          <>
            <Button component="label" startIcon={<CloudUploadIcon />}>
              Upload CSV{" "}
              <input
                type="file"
                hidden
                onChange={handleFileSelected}
                accept="text/csv"
                data-testid="file-upload-input"
              />
            </Button>
            <Button onClick={onClose} variant="contained" disabled={isLoading}>
              Close
            </Button>
          </>
        )}
      </DialogActions>
    </Dialog>
  );
};

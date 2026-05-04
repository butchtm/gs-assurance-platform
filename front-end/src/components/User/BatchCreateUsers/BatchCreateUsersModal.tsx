import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, List, ListItem } from "@mui/material";
import React, { ChangeEvent } from "react";
import { toast } from "react-toastify";

export const BatchCreateUsersModal: React.FC<{
  isOpen: boolean;
  onClose: () => void;
  onBatchedUsersUploaded: (csvFile: File) => void;
}> = ({ isOpen, onClose, onBatchedUsersUploaded }) => {
  const handleFileSelected = (event: ChangeEvent<HTMLInputElement>) => {
    if (!event.target.files) {
      toast.error("Unable to read CSV, please try again");
      console.error("Files array was empty");
      return;
    }

    onBatchedUsersUploaded(event.target.files[0]);
  };

  const fields = ["userName", "firstName", "lastName", "email", "groups", "accountId"];
  const groups = [
    "ARM-Team",
    "Finance",
    "GS-Admin",
    "GS-Registry",
    "GS-Reviewer",
    "Project-Developer",
    "TAC",
    "VVB",
    "Support",
  ];

  return (
    <Dialog open={isOpen} onClose={onClose}>
      <DialogTitle>Batch Create Users</DialogTitle>
      <DialogContent>
        Please upload a CSV of users with the following fields, in order:
        <List sx={{ listStyleType: "disc", paddingLeft: "20px" }}>
          {fields.map((field) => (
            <ListItem key={field} sx={{ display: "list-item", padding: 0, margin: 0 }}>
              {field === "groups" ? field + ":" + groups.map((group) => " " + group) : field}
            </ListItem>
          ))}
        </List>
      </DialogContent>

      <DialogActions>
        <Button onClick={onClose} data-testid="cancel-button">
          Cancel
        </Button>
        <Button component="label" variant="contained" startIcon={<CloudUploadIcon />}>
          Upload CSV{" "}
          <input type="file" hidden onChange={handleFileSelected} accept="text/csv" data-testid="file-input" />
        </Button>
      </DialogActions>
    </Dialog>
  );
};

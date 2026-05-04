import Button from "@mui/material/Button";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogContentText from "@mui/material/DialogContentText";
import DialogTitle from "@mui/material/DialogTitle";
import React from "react";

interface ConfirmCloseDialogProps {
  isOpen: boolean;
  onConfirm: () => void;
  onCancel: () => void;
}

const ConfirmCloseDialog: React.FC<ConfirmCloseDialogProps> = ({ isOpen, onConfirm, onCancel }) => {
  return (
    <Dialog
      open={isOpen}
      onClose={onCancel}
      aria-labelledby="confirm-close-dialog-title"
      aria-describedby="confirm-close-dialog-description"
    >
      <DialogTitle id="confirm-close-dialog-title">{"Confirm Close"}</DialogTitle>
      <DialogContent>
        <DialogContentText id="confirm-close-dialog-description">
          Are you sure you want to close? All unsaved changes will be lost.
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button variant="outlined" onClick={onCancel}>
          Cancel
        </Button>
        <Button variant="contained" onClick={onConfirm} autoFocus>
          Confirm
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default ConfirmCloseDialog;

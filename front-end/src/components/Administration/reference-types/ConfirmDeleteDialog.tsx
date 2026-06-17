import { Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from "@mui/material";

export const ConfirmDeleteDialog = ({
  isOpen,
  onClose,
  onConfirm,
  referenceType,
}: {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  referenceType: string;
}) => {
  return (
    <Dialog open={isOpen} onClose={onClose}>
      <DialogTitle>Confirm Delete</DialogTitle>
      <DialogContent>
        <DialogContentText>
          Are you sure you want to delete this {referenceType} ? This cannot be undone.
        </DialogContentText>
      </DialogContent>
      <DialogActions sx={{ justifyContent: "space-between" }}>
        <Button onClick={onClose} data-testid="confirm-delete-cancel-button">
          Cancel
        </Button>
        <Button onClick={onConfirm} color="error" autoFocus data-testid="confirm-delete-button">
          Delete
        </Button>
      </DialogActions>
    </Dialog>
  );
};

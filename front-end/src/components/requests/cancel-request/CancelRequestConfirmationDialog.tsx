import { Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from "@mui/material";

export const CancelRequestConfirmationDialog = ({
  isOpen,
  onClose,
  onConfirm,
  selectedStatus,
}: {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  selectedStatus: string;
}) => {
  return (
    <Dialog open={isOpen} onClose={onClose}>
      <DialogTitle>Confirm Request Closure</DialogTitle>
      <DialogContent>
        <DialogContentText>
          Are you sure you want to permanently close this request and mark it as &ldquo;{selectedStatus}&rdquo;? This
          action cannot be undone and the request will no longer be active.
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>
        <Button
          onClick={onConfirm}
          color="error"
          variant="contained"
          autoFocus
          data-testid="confirm-close-request-button"
        >
          {selectedStatus === "Cancelled" ? "Mark as Cancelled" : "Mark as Omitted"}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

import { Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from "@mui/material";

export const ConfirmCompletionModal = ({
  isOpen,
  onClose,
  onConfirm,
  selectedStatus,
}: {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  selectedStatus?: string;
}) => {
  if (!selectedStatus) {
    return <></>;
  }

  const simplifiedStatus = selectedStatus == "Completed - Approved" ? "Approved" : "Rejected";

  return (
    <Dialog open={isOpen} onClose={onClose}>
      <DialogTitle>{"Confirm Request " + simplifiedStatus}</DialogTitle>
      <DialogContent>
        <DialogContentText>
          Are you sure you want to permanently complete this request and mark it as &ldquo;{simplifiedStatus}&rdquo;?
          This action cannot be undone.
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>
        <Button onClick={onConfirm} variant="contained" autoFocus data-testid="confirm-complete-request-button">
          Confirm
        </Button>
      </DialogActions>
    </Dialog>
  );
};

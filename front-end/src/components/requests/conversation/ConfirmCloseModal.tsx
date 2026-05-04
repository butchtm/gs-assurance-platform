import { Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from "@mui/material";

export const ConfirmCloseModal = ({ isOpen, onClose }: { isOpen: boolean; onClose: (submit: boolean) => void }) => {
  return (
    <Dialog open={isOpen} onClose={onClose} aria-labelledby="confirm-close-title">
      <DialogTitle id="confirm-close-title">Confirm Closure of Conversation</DialogTitle>
      <DialogContent>
        <DialogContentText>
          Are you sure you want to close this conversation? It will cease communications for all parties. Messages will
          still be viewable.
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={() => onClose(false)}>Cancel</Button>
        <Button
          onClick={() => onClose(true)}
          color="error"
          variant="contained"
          data-testid="confirm-close-conversation-button"
        >
          Close Conversation
        </Button>
      </DialogActions>
    </Dialog>
  );
};

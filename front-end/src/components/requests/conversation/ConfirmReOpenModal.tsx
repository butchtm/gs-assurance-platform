import { Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from "@mui/material";

export const ConfirmReOpenModal = ({ isOpen, onClose }: { isOpen: boolean; onClose: (submit: boolean) => void }) => {
  return (
    <Dialog open={isOpen} onClose={onClose} aria-labelledby="confirm-re-open-title">
      <DialogTitle id="confirm-re-open-title">Confirm Re-Open of Conversation</DialogTitle>
      <DialogContent>
        <DialogContentText>
          Are you sure you want to re-open this conversation? It will enable communications for all parties.
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={() => onClose(false)}>Cancel</Button>
        <Button onClick={() => onClose(true)} variant="contained" data-testid="confirm-re-open-conversation-button">
          Re-Open Conversation
        </Button>
      </DialogActions>
    </Dialog>
  );
};

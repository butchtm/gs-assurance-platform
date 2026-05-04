import { Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from "@mui/material";

export const ConfirmExportDialog = ({
  isOpen,
  onClose,
  userEmail,
}: {
  isOpen: boolean;
  onClose: () => void;
  userEmail: string;
}) => {
  return (
    <Dialog open={isOpen} onClose={onClose}>
      <DialogTitle>Export Report Confirmation</DialogTitle>
      <DialogContent>
        <DialogContentText>
          Your report request has been issued successfully. You should recieve your report via email to{" "}
          <b>{userEmail}</b> within the next 5 minutes. If you do not recieve your report as expected please contact{" "}
          <a href="mailto:help@goldstandard.org">help@goldstandard.org</a>.
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Close</Button>
      </DialogActions>
    </Dialog>
  );
};

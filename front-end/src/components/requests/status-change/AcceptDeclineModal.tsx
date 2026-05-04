import { Box, Button, FormControl, Modal, Typography } from "@mui/material";
import React from "react";
import { toast } from "react-toastify";

import { useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";
import { updateRequestAssignmentStatus } from "../assignment/Assignment";

interface AcceptDeclineModalProps {
  assignmentId: number;
  isOpen: boolean;
  onClose: () => void;
  onDecline: () => Promise<void>;
  onAccept: () => void;
}

const AcceptDeclineModal: React.FC<AcceptDeclineModalProps> = ({
  assignmentId,
  isOpen,
  onClose,
  onDecline,
  onAccept,
}) => {
  const { activeReferenceTypes } = useReferenceTypes();
  const acceptedStatus = activeReferenceTypes.AssignmentStatus.find((status) => status.code === "ACCEPTED");
  const declinedStatus = activeReferenceTypes.AssignmentStatus.find((status) => status.code === "CANCELLED");

  const handleAccept = async () => {
    await updateRequestAssignmentStatus(assignmentId, { assignmentStatusId: acceptedStatus?.id });
    onAccept();
    toast.success("Request Assignment successfully accepted");
    onClose();
  };
  const handleDecline = async () => {
    await updateRequestAssignmentStatus(assignmentId, { assignmentStatusId: declinedStatus?.id });
    await onDecline();
    toast.success("Request Assignment successfully declined");
    onClose();
  };

  return (
    <Modal open={isOpen} onClose={() => onClose()}>
      <Box sx={{ backgroundColor: "white", width: 600, padding: 10, borderRadius: 8 }}>
        <Typography variant="h6" component="h2" sx={{ display: "flex", justifyContent: "space-around", mb: 2 }}>
          Accept / Decline Request
        </Typography>
        <form>
          <FormControl fullWidth margin="normal"></FormControl>
        </form>
        <div style={{ display: "flex", justifyContent: "space-around", paddingTop: "10px" }}>
          <Button variant="contained" onClick={handleAccept}>
            Accept
          </Button>
          <Button variant="contained" onClick={handleDecline}>
            Decline
          </Button>
          <Button variant="outlined" onClick={() => onClose()}>
            Cancel
          </Button>
        </div>
      </Box>
    </Modal>
  );
};

export default AcceptDeclineModal;

import CloseIcon from "@mui/icons-material/Close";
import { Box, IconButton, Modal } from "@mui/material";
import { useState } from "react";

import ConfirmCloseDialog from "../../../common/dialog/ConfirmCloseDialog";
import { GSRequest } from "../../../common/request/GSRequest";
import { RequestStatusResponse } from "../Requests";

import InAssignmentStatusForm from "./InAssignmentStatusForm";
import InAuditStatusForm from "./InAuditStatusForm";
import InCompletenessStatusForm from "./InCompletenessStatusForm";
import InPreparationStatusForm from "./InPreparationStatusForm";
import InProgressStatusForm from "./InProgressStatusForm";
import InRoundCompletionStatusForm from "./InRoundCompletion/InRoundCompletionStatusForm";

interface ChangeRequestStatusModalProps {
  request: GSRequest;
  availableTransitionStatuses?: RequestStatusResponse;
  onClose: () => void;
  isOpen: boolean;
  updateRequest: () => void;
}

export const ChangeRequestStatusModal = ({
  request,
  availableTransitionStatuses,
  onClose,
  isOpen,
  updateRequest,
}: ChangeRequestStatusModalProps) => {
  const [openConfirmDialog, setOpenConfirmDialog] = useState(false);

  const handleCloseClick = () => {
    setOpenConfirmDialog(true);
  };

  const handleCancelClose = () => {
    setOpenConfirmDialog(false);
  };

  const handleCancelChange = () => {
    onClose();
    setOpenConfirmDialog(false);
  };

  const onSubmit = () => {
    updateRequest();
    onClose();
  };

  const drawForm = (): JSX.Element => {
    switch (request.requestStatus.code) {
      case "IN_PREPARATION":
        return <InPreparationStatusForm request={request} onSubmit={onSubmit} />;
      case "IN_AUDIT":
        return <InAuditStatusForm request={request} onSubmit={onSubmit} />;
      case "COMPLETENESS_CHECK":
        return <InCompletenessStatusForm request={request} onSubmit={onSubmit} />;
      case "IN_ASSIGNMENT":
        return <InAssignmentStatusForm request={request} onSubmit={onSubmit} />;
      case "IN_PROGRESS":
        return <InProgressStatusForm request={request} onSubmit={onSubmit} />;
      case "IN_ROUND_COMPLETION":
        return (
          <InRoundCompletionStatusForm
            request={request}
            availableTransitionStatuses={availableTransitionStatuses}
            onSubmit={onSubmit}
          />
        );
      default:
        throw Error("Unexpected requestStatus code");
    }
  };

  return (
    <>
      <Modal open={isOpen} onClose={handleCloseClick}>
        <Box
          sx={{
            backgroundColor: "white",
            width: "100%",
            maxWidth: 600,
            maxHeight: "100vh",
            overflowY: "auto",
            padding: 2,
            borderRadius: 1,
            position: "relative",
          }}
        >
          <IconButton
            onClick={handleCloseClick}
            sx={{
              position: "absolute",
              top: 8,
              right: 8,
            }}
          >
            <CloseIcon />
          </IconButton>
          {drawForm()}
        </Box>
      </Modal>
      <ConfirmCloseDialog isOpen={openConfirmDialog} onConfirm={handleCancelChange} onCancel={handleCancelClose} />
    </>
  );
};

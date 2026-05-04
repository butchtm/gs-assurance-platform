import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Typography } from "@mui/material";
import { useContext, useState } from "react";

import { GSRequest, hasClosedStatus } from "../../../common/request/GSRequest";
import { AuthContext } from "../../Auth/Login/AuthProvider";
import { useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";

import { updateConversationStatus } from "./Conversation";
import { ConfirmCloseModal } from "./ConfirmCloseModal";
import { Messages } from "./Messages";
import { ConfirmReOpenModal } from "./ConfirmReOpenModal";

export const ConversationModal = ({
  isOpen,
  onClose,
  request,
  updateRequest,
}: {
  isOpen: boolean;
  onClose: () => void;
  request: GSRequest;
  updateRequest: () => void;
}) => {
  const authContext = useContext(AuthContext);
  const { activeReferenceTypes } = useReferenceTypes();
  const [confirmModalOpen, setConfirmModalOpen] = useState<boolean>(false);
  const [confirmReopenModalOpen, setConfirmReopenModalOpen] = useState<boolean>(false);

  const isArmOrAdmin = authContext.isAdminUser() || authContext.isArmUser();
  const closedStatus = activeReferenceTypes.ConversationStatus.find((x) => x.code === "CLOSED");
  const openStatus = activeReferenceTypes.ConversationStatus.find((x) => x.code === "OPEN");

  const isClosedRequest = hasClosedStatus(request);

  const participants = activeReferenceTypes.AccountType.filter((x) =>
    ["PROJECT-DEVELOPER", "VVB", "GS-ARM-TEAM"].includes(x.code),
  );

  const handleConfirmCloseConversation = async (submit: boolean) => {
    setConfirmModalOpen(false);
    if (submit) {
      await updateConversationStatus(request.project.id, request.id, request.conversation?.id, closedStatus);
      updateRequest();
    }
    onClose();
  };

  const handleReOpenConversation = async (submit: boolean) => {
    setConfirmReopenModalOpen(false);
    if (submit) {
      await updateConversationStatus(request.project.id, request.id, request.conversation?.id, openStatus);
      updateRequest();
    }
  };

  return (
    <>
      <Dialog open={isOpen} onClose={onClose} aria-labelledby="conversation-title" maxWidth={false}>
        <DialogTitle id="conversation-title">
          {request.project.gsid} - {request.reviewName}
        </DialogTitle>
        <DialogContent sx={{ height: "fit-content" }}>
          <Typography variant="body1" sx={{ paddingBottom: "10px" }}>
            Participants: {participants.map((x) => x.description).join(", ")}
          </Typography>
          <Box
            sx={{
              width: "70vw",
              height: "70vh",
            }}
          >
            {request.conversation && (
              <Messages
                projectId={request.project.id}
                requestId={request.id}
                conversationId={request.conversation.id}
                openConversation={request.conversation.status.code === "OPEN"}
                updateRequest={updateRequest}
              />
            )}
          </Box>
        </DialogContent>
        <DialogActions>
          <Button variant="outlined" onClick={onClose}>
            Close
          </Button>
          {isArmOrAdmin && request.conversation?.status.code === "OPEN" && (
            <Button color="error" variant="contained" onClick={() => setConfirmModalOpen(true)}>
              Close Conversation
            </Button>
          )}
          {isArmOrAdmin && request.conversation?.status.code === "CLOSED" && !isClosedRequest && (
            <Button variant="contained" onClick={() => setConfirmReopenModalOpen(true)}>
              Re-Open Conversation
            </Button>
          )}
        </DialogActions>
      </Dialog>
      {confirmModalOpen && <ConfirmCloseModal isOpen={confirmModalOpen} onClose={handleConfirmCloseConversation} />}
      {confirmReopenModalOpen && (
        <ConfirmReOpenModal isOpen={confirmReopenModalOpen} onClose={handleReOpenConversation} />
      )}
    </>
  );
};

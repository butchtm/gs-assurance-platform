import { Badge, Button } from "@mui/material";
import { Message } from "@mui/icons-material";
import { useState } from "react";

import { ConversationModal } from "../conversation/ConversationModal";
import { GSRequest } from "../../../common/request/GSRequest";
import { openConversation } from "../conversation/Conversation";

export const ConversationAction = ({
  request,
  updateRequest,
  initial,
}: {
  request: GSRequest;
  updateRequest: () => void;
  initial?: boolean;
}) => {
  const [modalOpen, setModalOpen] = useState<boolean>(false);
  const conversationOpen = request.conversation?.status.code === "OPEN";

  const getButtonMessage = () => {
    if (initial) {
      return "Open Conversation";
    }
    if (conversationOpen) {
      return "View Active Conversation";
    }
    return "View Closed Conversation";
  };

  const openInitialConversation = async () => {
    await openConversation(request.project.id, request.id);
    updateRequest();
  };

  const handleClick = async () => {
    if (initial) {
      await openInitialConversation();
    }
    setModalOpen(true);
  };

  return (
    <>
      <Badge
        badgeContent={conversationOpen ? request.conversation?.unreadMessages : 0}
        color="primary"
        sx={{ width: "100%", marginTop: "11px !important" }}
      >
        <Button
          onClick={handleClick}
          variant="outlined"
          sx={{ width: "100%", justifyContent: "left !important", padding: "12px 17px !important" }}
        >
          <Message />
          <span className="action-text">{getButtonMessage()}</span>
        </Button>
      </Badge>
      {modalOpen && (
        <ConversationModal
          isOpen={modalOpen}
          onClose={() => setModalOpen(false)}
          request={request}
          updateRequest={updateRequest}
        />
      )}
    </>
  );
};

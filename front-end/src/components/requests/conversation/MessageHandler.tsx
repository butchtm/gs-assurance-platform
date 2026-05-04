import { Box, Typography } from "@mui/material";
import { useContext } from "react";
import dayjs from "dayjs";
import ReactQuill from "react-quill-new";
import "react-quill-new/dist/quill.bubble.css";
import "./quill-overrides.css";

import { AuthContext } from "../../Auth/Login/AuthProvider";

import { ConversationParticipant, Message } from "./Conversation";

export const MessageHandler = ({ message }: { message: Message }) => {
  const authContext = useContext(AuthContext);
  const isCurrentUser = authContext.user?.userName === message.senderUsername;
  const isArmOrAdmin = authContext.isAdminUser() || authContext.isArmUser();

  const toColorScheme = () => {
    switch (message.senderAccountType) {
      case ConversationParticipant.ProjectDeveloper:
        return {
          text: "#036DA6",
          background: "#CFEDFC",
        };
      case ConversationParticipant.VVB:
        return {
          text: "#257500",
          background: "#EEFFE5",
        };
      case ConversationParticipant.GSARMTeam:
        return {
          text: "#B34700",
          background: "#FFE9DB",
        };
      default:
        return {
          text: "#000000",
          background: "#F2F2F2",
        };
    }
  };

  const getSenderDetails = () => {
    const timestamp = dayjs.utc(message.sentTimestamp).format("DD/MM/YYYY HH:mm");
    const accountType = message.senderAccountType ?? "Gold Standard Admin";
    if (![ConversationParticipant.GSARMTeam, "Gold Standard Admin"].includes(accountType) || isArmOrAdmin) {
      return `${message.senderUsername} - ${accountType} (${timestamp})`;
    }
    return `${accountType} (${timestamp})`;
  };

  const colourScheme = toColorScheme();
  return (
    <Box
      sx={{
        marginTop: "5px",
        display: "flex",
        justifyContent: isCurrentUser ? "flex-end" : "flex-start",
      }}
    >
      <Box
        sx={{
          backgroundColor: colourScheme.background,
          borderRadius: "5px",
          padding: "10px",
          maxWidth: "90%",
        }}
      >
        <Typography sx={{ color: colourScheme.text, fontSize: "13px" }}>{getSenderDetails()}</Typography>
        <ReactQuill theme="bubble" value={message.content} readOnly />
      </Box>
    </Box>
  );
};

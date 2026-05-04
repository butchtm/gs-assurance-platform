import { useEffect, useState } from "react";
import { Box, Button, CircularProgress, Typography } from "@mui/material";

import { ConversationParticipant, getMessages, markRead, Message } from "./Conversation";
import { MessageHandler } from "./MessageHandler";
import { MessageSender } from "./MessageSender";

export const Messages = ({
  projectId,
  requestId,
  conversationId,
  openConversation,
  updateRequest,
}: {
  projectId: number;
  requestId: number;
  conversationId: number;
  openConversation: boolean;
  updateRequest: () => void;
}) => {
  const [totalMessages, setTotalMessages] = useState<number>(0);
  const [messages, setMessages] = useState<Message[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [page, setPage] = useState<number>(1);
  const [morePages, setMorePages] = useState<boolean>(false);
  const [readBy, setReadBy] = useState<ConversationParticipant[]>([]);

  const fetchMessages = async () => {
    setLoading(true);
    const response = await getMessages(projectId, requestId, conversationId, page);
    if (response) {
      setTotalMessages(response.totalElements);
      const messageIds = new Set(messages.map((message) => message.id));
      const newMessages = response.content.filter((candidateMessage) => !messageIds.has(candidateMessage.id));
      const updatedMessages = [...messages.slice(), ...newMessages];
      setMessages(updatedMessages);
      setMorePages(response.totalPages !== 0 && response.totalPages !== page);
      if (updatedMessages.length > 0) {
        const latestMessage = updatedMessages[0];
        setReadBy(latestMessage.readByAccountTypes ?? []);
      }
      setLoading(false);
    }
  };

  const handleMessageSent = (newMessage: Message) => {
    const updatedMessages = [newMessage, ...messages.slice()];
    setTotalMessages(totalMessages + 1);
    setMessages(updatedMessages);
    setReadBy(newMessage.readByAccountTypes ?? []);
  };

  useEffect(() => {
    fetchMessages();
  }, [page]);

  useEffect(() => {
    markRead(projectId, requestId, conversationId);
    updateRequest();
  }, []);

  return (
    <Box sx={{ display: "flex", flexDirection: "column", maxHeight: "95%", height: "100%" }}>
      <Box
        sx={{
          width: "100%",
          display: "flex",
          flexDirection: "column-reverse",
          justifyContent: "flex-start",
          border: "1px solid grey",
          borderRadius: "5px",
          padding: "10px",
          overflow: "scroll",
          resize: "vertical",
          height: "500px",
        }}
      >
        {readBy.length > 0 && (
          <Typography sx={{ textAlign: "center", mt: "10px" }} variant="caption">
            Seen by: {readBy.join(", ")}
          </Typography>
        )}
        {messages.map((message) => (
          <MessageHandler key={message.id} message={message} />
        ))}
        {loading && <CircularProgress size={60} sx={{ margin: "auto" }} />}
        {morePages && <Button onClick={() => setPage(page + 1)}>Load More</Button>}
      </Box>
      <MessageSender
        projectId={projectId}
        requestId={requestId}
        conversationId={conversationId}
        onMessageSent={handleMessageSent}
        disabled={!openConversation}
      />
    </Box>
  );
};

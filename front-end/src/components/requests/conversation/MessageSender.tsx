import { useState } from "react";
import { Box, Button } from "@mui/material";
import { toast } from "react-toastify";
import { isAxiosError } from "axios";
import ReactQuill from "react-quill-new";
import "react-quill-new/dist/quill.snow.css";
import "./quill-overrides.css";

import { sendMessage, Message } from "./Conversation";

export const MessageSender = ({
  projectId,
  requestId,
  conversationId,
  onMessageSent,
  disabled,
}: {
  projectId: number;
  requestId: number;
  conversationId: number;
  onMessageSent: (message: Message) => void;
  disabled: boolean;
}) => {
  const [draftMessage, setDraftMessage] = useState<string>("");

  const handleSendMessage = async () => {
    if (!["", "<empty string />", "<p><br></p>"].includes(draftMessage)) {
      try {
        const newMessage = await sendMessage(projectId, requestId, conversationId, draftMessage);
        setDraftMessage("");
        onMessageSent(newMessage);
      } catch (error) {
        if (isAxiosError(error) && error?.response?.status === 409) {
          toast.error("Message cannot be sent because conversation is closed");
          return;
        }
        toast.error("Message failed to send");
      }
    }
  };

  const modules = {
    toolbar: [
      [{ header: [1, 2, 3, false] }],
      ["bold", "italic", "underline", "strike"],
      [{ list: "ordered" }, { list: "bullet" }, { indent: "-1" }, { indent: "+1" }],
      ["clean"],
    ],
  };

  const formats = ["header", "bold", "italic", "underline", "strike", "list", "indent"];

  return (
    <Box sx={{ display: "flex", mt: "10px", flexGrow: 1, maxHeight: "100%" }}>
      <ReactQuill
        theme="snow"
        value={draftMessage}
        onChange={setDraftMessage}
        modules={modules}
        formats={formats}
        readOnly={disabled}
        placeholder={disabled ? "" : "Type a message..."}
      />
      <Button onClick={handleSendMessage} sx={{ ml: "5px", height: "fit-content" }} disabled={disabled}>
        Send
      </Button>
    </Box>
  );
};

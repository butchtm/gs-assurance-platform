import { ChatBubbleOutlineOutlined } from "@mui/icons-material";
import { Box } from "@mui/material";

export const UnreadMessagesPill = ({ unreadMessages }: { unreadMessages: number }) => {
  return (
    <Box
      sx={{
        backgroundColor: "var(--color-accent-teal-primary)",
        color: "var(--color-text-black)",
        borderRadius: "5px",
        display: "flex",
        alignItems: "center",
        gap: "4px",
        fontSize: "11px",
        padding: "2px 4px",
        whiteSpace: "nowrap",
        width: "fit-content",
      }}
      aria-live="polite"
      aria-label={`${unreadMessages} new messages`}
    >
      <ChatBubbleOutlineOutlined sx={{ fontSize: 14, color: "var(--color-text-dark-gray)" }} />
      {unreadMessages} New
    </Box>
  );
};

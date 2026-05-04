import { ChatBubbleOutlineOutlined } from "@mui/icons-material";
import { Badge, Box, IconButton, Tooltip } from "@mui/material";
import { useNavigate } from "react-router-dom";

import { UnreadMessages } from "../../components/requests/conversation/Conversation";

export const UnreadMessagesNotifications = ({ unreadMessages }: { unreadMessages: UnreadMessages[] }) => {
  const navigate = useNavigate();
  const totalUnread = unreadMessages.reduce((sum, current) => sum + current.unreadCount, 0);
  const anyUnread = totalUnread > 0;

  const TOOLTIP_MESSAGE_UNREAD = `${totalUnread} new messages. Click to view in My Requests`;
  const TOOLTIP_MESSAGE_NONE = `No new messages`;

  return (
    <Tooltip title={anyUnread ? TOOLTIP_MESSAGE_UNREAD : TOOLTIP_MESSAGE_NONE}>
      <Box
        sx={{
          mr: "5px",
        }}
        aria-live="polite"
        aria-label={`${totalUnread} new messages`}
      >
        <Badge badgeContent={totalUnread} color="primary" data-testid="unread-messages-badge">
          <IconButton
            sx={{
              border: "1px solid var(--color-accent-teal-primary)",
              ":disabled": { border: "1px solid var(--color-accent-teal-dark)" },
            }}
            onClick={() => navigate("/my-requests")}
            data-testid="unread-messages-button"
            disabled={!anyUnread}
          >
            <ChatBubbleOutlineOutlined
              sx={{
                fontSize: "14px",
                color: anyUnread ? "var(--color-accent-teal-primary)" : "var(--color-accent-teal-dark)",
              }}
            />
          </IconButton>
        </Badge>
      </Box>
    </Tooltip>
  );
};

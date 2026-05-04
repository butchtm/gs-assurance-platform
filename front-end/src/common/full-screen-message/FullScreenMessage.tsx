import { Box, Typography } from "@mui/material";
import React from "react";

interface FullScreenMessageProps {
  headline: string;
  body?: string;
}

const FullScreenMessage: React.FC<FullScreenMessageProps> = ({ headline, body }) => {
  return (
    <Box className="full-screen-message-container">
      <Typography variant="h4" gutterBottom>
        {headline}
      </Typography>
      {body && <Typography variant="body1">{body}</Typography>}
    </Box>
  );
};

export default FullScreenMessage;

import { Box, Card, CardContent, IconButton } from "@mui/material";
import React from "react";

interface InformationCardProps {
  children?: JSX.Element | JSX.Element[];
  title?: string;
  action?: {
    ariaLabel: string;
    icon: JSX.Element;
    onClick: () => void;
  };
}

const InformationCard: React.FC<InformationCardProps> = ({ children, title, action }) => {
  const shouldShowHeader = () => {
    return title ?? action;
  };

  return (
    <Card
      sx={{
        marginBottom: 2,
        backgroundColor: "var(--color-surface-card)",
        ":last-child": {
          marginBottom: 0,
        },
        border: "1px solid var(--border-color-card)",
        borderRadius: "5px",
      }}
    >
      <CardContent>
        {shouldShowHeader() && (
          <Box
            style={{
              display: "flex",
              justifyContent: "space-between",
              marginBottom: "20px",
            }}
          >
            {title && (
              <h3
                style={{
                  margin: "0 0 0 30px",
                  fontSize: "14px",
                  textAlign: "left",
                  color: "var(--color-text-muted)",
                  verticalAlign: "center",
                }}
              >
                {title}
              </h3>
            )}
            {action && (
              <IconButton aria-label={action.ariaLabel} onClick={action.onClick}>
                {action.icon}
              </IconButton>
            )}
          </Box>
        )}
        {children}
      </CardContent>
    </Card>
  );
};

export default InformationCard;

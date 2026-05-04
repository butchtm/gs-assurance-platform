import React from "react";

interface MessagePillProps {
  count: number;
}

export const MessagePill: React.FC<MessagePillProps> = ({ count }) => {
  return (
    <div
      style={{
        display: "flex",
        alignItems: "center",
        backgroundColor: "#00babe",
        color: "white",
        borderRadius: "5px",
        margin: "0.5rem",
        padding: "0 10px",
      }}
    >
      <span>{count} New Messages</span>
    </div>
  );
};

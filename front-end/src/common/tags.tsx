import styled from "@emotion/styled";
import { Chip, ChipProps } from "@mui/material";

const tagColors = {
  info: {
    background: "#00AEFF",
    text: "#000000",
  },
  success: {
    background: "#90ee90",
    text: "#000000",
  },
  warning: {
    background: "#ff9800",
    text: "#000000",
  },
  error: {
    background: "#f44336",
    text: "#ffffff",
  },
  default: {
    background: "#d3d3d3",
    text: "#000000",
  },
};

interface TagProps extends Omit<ChipProps, "variant"> {
  variant: "info" | "success" | "warning" | "error" | "default";
}

export const Tag = ({ variant, label, size = "small", ...props }: TagProps) => {
  const profile = tagColors[variant] || tagColors.default;

  const StyledChip = styled(Chip)(() => ({
    backgroundColor: profile.background,
    color: profile.text,
    borderRadius: "0.3rem",
  }));

  return <StyledChip size={size} label={label} {...props} />;
};

import { Box, useTheme } from "@mui/material";
import React from "react";

import CustomHelperText, { CustomTooltip } from "../../components/Reusable/FormFields/CustomTooltipText";

import { PiiActionCode } from "./PiiAction";

export const OK_ACTIONS = [PiiActionCode.ACCEPTED, PiiActionCode.NO_PII_FOUND];
export const WARNING_ACTIONS = [PiiActionCode.NEW, PiiActionCode.UNDER_INVESTIGATION];
const PROBLEM_ACTIONS = [PiiActionCode.PII_NOT_SUITABLE, PiiActionCode.DOCUMENT_REMOVED];

const PiiActionMessage = {
  NEW: "IDENTIFIED PERSONAL INFORMATION (PII)",
  UNDER_INVESTIGATION: "PII UNDER INVESTIGATION",
  PII_NOT_SUITABLE: "PII NOT SUITABLE FOR PUBLIC",
  DOCUMENT_REMOVED: "DOCUMENT BEING REMOVED DUE TO PII",
  ACCEPTED: "",
  NO_PII_FOUND: "",
};

export const PiiActionWarningPill: React.FC<{ action?: PiiActionCode; helperText?: JSX.Element }> = ({
  action,
  helperText,
}) => {
  const theme = useTheme();

  const getColorForAction = (action: PiiActionCode) => {
    if (WARNING_ACTIONS.includes(action)) {
      return theme.palette.warning.light;
    }
    if (PROBLEM_ACTIONS.includes(action)) {
      return "var(--color-text-pii-warning-action)";
    }
  };

  const getTextColorForAction = (action: PiiActionCode) => {
    if (WARNING_ACTIONS.includes(action)) {
      return "black";
    }
    if (PROBLEM_ACTIONS.includes(action)) {
      return "white";
    }
  };

  if (!action || OK_ACTIONS.includes(action)) {
    return <></>;
  }
  const textColourForAction = getTextColorForAction(action);
  return (
    <Box
      sx={{
        display: "inline-flex",
        borderRadius: "5px",
        padding: "1px 6px",
        alignItems: "center",
        marginLeft: "11px",
        fontSize: "13px",
        color: textColourForAction,
        backgroundColor: getColorForAction(action),
      }}
      data-testid="pii-warning-pill"
    >
      {PiiActionMessage[action]}
      {helperText && (
        <CustomHelperText
          text={helperText}
          type={CustomTooltip.HELP}
          styleOverrides={{ scale: "0.8", padding: "0 0 0 4px", color: textColourForAction }}
        />
      )}
    </Box>
  );
};

import { Box, FormControl, FormControlLabel, FormLabel, Radio, RadioGroup } from "@mui/material";
import React, { ChangeEvent } from "react";

interface InRoundCompletionRejectIssueProps {
  requestIsRejected: boolean | null;
  setRequestIsRejected: (event: boolean) => void;
}

const InRoundCompletionRejectIssue: React.FC<InRoundCompletionRejectIssueProps> = ({
  requestIsRejected,
  setRequestIsRejected,
}) => {
  const handleRejectRequestChange = (event: ChangeEvent<HTMLInputElement>) => {
    if (event.target.value === "true") {
      setRequestIsRejected(true);
    } else {
      setRequestIsRejected(false);
    }
  };

  const toDefaultStringValue = (b: boolean | null): string | null => {
    if (b == null) {
      return null;
    }
    return String(b);
  };

  return (
    <Box mb={2}>
      <FormControl>
        <FormLabel>Reject this request?</FormLabel>
        <RadioGroup
          name="request-issue"
          value={toDefaultStringValue(requestIsRejected)}
          onChange={handleRejectRequestChange}
        >
          <FormControlLabel value="true" control={<Radio />} label="Yes" data-testid="reject-request-yes" />
          <FormControlLabel value="false" control={<Radio />} label="No" data-testid="reject-request-no" />
        </RadioGroup>
      </FormControl>
    </Box>
  );
};

export default InRoundCompletionRejectIssue;

import { Box, FormControl, FormControlLabel, FormLabel, Radio, RadioGroup } from "@mui/material";
import React, { ChangeEvent } from "react";

interface InRoundCompletionOpenFindingsProps {
  requestHasOpenFindings: boolean | null;
  setRequestHasOpenFindings: (event: boolean) => void;
}

const InRoundCompletionOpenFindings: React.FC<InRoundCompletionOpenFindingsProps> = ({
  requestHasOpenFindings,
  setRequestHasOpenFindings,
}) => {
  const toDefaultStringValue = (b: boolean | null): string | null => {
    if (b == null) {
      return null;
    }
    return String(b);
  };

  const handleInRoundCompletionRadioChange = (event: ChangeEvent<HTMLInputElement>) => {
    if (event.target.value === "true") {
      setRequestHasOpenFindings(true);
    } else {
      setRequestHasOpenFindings(false);
    }
  };

  return (
    <Box mb={2}>
      <FormControl>
        <FormLabel>Does this request have any open findings?</FormLabel>
        <RadioGroup
          name="request-issue"
          value={toDefaultStringValue(requestHasOpenFindings)}
          onChange={handleInRoundCompletionRadioChange}
        >
          <FormControlLabel value="true" control={<Radio />} label="Yes" data-testid="request-issue-yes" />
          <FormControlLabel value="false" control={<Radio />} label="No" data-testid="request-issue-no" />
        </RadioGroup>
      </FormControl>
    </Box>
  );
};

export default InRoundCompletionOpenFindings;

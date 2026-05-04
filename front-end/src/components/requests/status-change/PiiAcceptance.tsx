import { Box, FormControl, FormControlLabel, FormLabel, Radio, RadioGroup } from "@mui/material";
import React, { ChangeEvent } from "react";

interface PiiAcceptanceProps {
  piiAccepted: boolean;
  setPiiAccepted: (event: boolean) => void;
  setFastTrack?: (event: boolean) => void;
  setInternal?: (event: boolean | undefined) => void;
}

const PiiAcceptance: React.FC<PiiAcceptanceProps> = ({ piiAccepted, setPiiAccepted, setFastTrack, setInternal }) => {
  const handleRadioChange = (event: ChangeEvent<HTMLInputElement>) => {
    if (event.target.value === "true") {
      setPiiAccepted(true);
    } else {
      setPiiAccepted(false);
      setFastTrack?.(false);
      setInternal?.(undefined);
    }
  };

  return (
    <Box mb={2}>
      <FormControl>
        <FormLabel>Is the remaining PII which is present accepted?</FormLabel>
        <RadioGroup name="request-issue" value={piiAccepted ? "true" : "false"} onChange={handleRadioChange}>
          <FormControlLabel value="true" control={<Radio />} label="Yes" data-testid="request-pii-issue-yes" />
          <FormControlLabel value="false" control={<Radio />} label="No" data-testid="request-pii-issue-no" />
        </RadioGroup>
      </FormControl>
    </Box>
  );
};

export default PiiAcceptance;

import { Box, FormControl, FormControlLabel, FormLabel, Radio, RadioGroup } from "@mui/material";
import React, { ChangeEvent } from "react";

interface InRoundCompletionFormContentsProps {
  isReviewComplete: boolean | null;
  handleReviewCompleteChange: (event: ChangeEvent<HTMLInputElement>) => void;
}

const toDefaultStringValue = (b: boolean | null): string | null => {
  if (b == null) {
    return null;
  }
  return String(b);
};

const InRoundCompletionFormContents: React.FC<InRoundCompletionFormContentsProps> = ({
  isReviewComplete,
  handleReviewCompleteChange,
}) => {
  return (
    <>
      <Box mb={2}>
        <FormControl>
          <FormLabel>Is Review Complete?</FormLabel>
          <RadioGroup
            name="review-complete"
            value={toDefaultStringValue(isReviewComplete)}
            onChange={handleReviewCompleteChange}
          >
            <FormControlLabel value="true" control={<Radio />} label="Yes" data-testid="review-complete-yes" />
            <FormControlLabel value="false" control={<Radio />} label="No" data-testid="review-complete-no" />
          </RadioGroup>
        </FormControl>
      </Box>
    </>
  );
};

export default InRoundCompletionFormContents;

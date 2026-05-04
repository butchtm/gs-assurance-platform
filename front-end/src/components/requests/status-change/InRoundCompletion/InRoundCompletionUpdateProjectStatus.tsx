import {
  Box,
  FormControl,
  FormControlLabel,
  FormLabel,
  MenuItem,
  Radio,
  RadioGroup,
  Select,
  SelectChangeEvent,
  Typography,
} from "@mui/material";
import React, { ChangeEvent, useState } from "react";

import { useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";

interface InRoundCompletionUpdateProjectStatusProps {
  chosenProjectStatus: number | "";
  setChosenProjectStatus: (event: number | "") => void;
}

const InRoundCompletionUpdateProjectStatus: React.FC<InRoundCompletionUpdateProjectStatusProps> = ({
  chosenProjectStatus,
  setChosenProjectStatus,
}) => {
  const [shouldUpdateProjectStatus, setShouldUpdateProjectStatus] = useState<boolean>(false);
  const { activeReferenceTypes } = useReferenceTypes();

  const handleProjectStatusChange = (event: SelectChangeEvent<number>) => {
    setChosenProjectStatus(Number(event.target.value));
  };

  const handleUpdateProjectStatusChange = (event: ChangeEvent<HTMLInputElement>) => {
    if (event.target.value === "true") {
      setShouldUpdateProjectStatus(true);
    } else {
      setShouldUpdateProjectStatus(false);
      setChosenProjectStatus("");
    }
  };

  return (
    <Box mb={2} data-testid="update-project-status-section">
      <FormControl>
        <FormLabel>Update project status?</FormLabel>
        <RadioGroup
          name="update-project-status"
          value={shouldUpdateProjectStatus ? "true" : "false"}
          onChange={handleUpdateProjectStatusChange}
        >
          <FormControlLabel value="true" control={<Radio />} label="Yes" data-testid="reject-request-yes" />
          <FormControlLabel value="false" control={<Radio />} label="No" data-testid="reject-request-no" />
        </RadioGroup>
      </FormControl>

      {shouldUpdateProjectStatus && (
        <>
          <Typography variant="body1">
            <strong>Update Project Status</strong>
          </Typography>
          <FormControl fullWidth>
            <Select
              labelId="project-status-label"
              value={chosenProjectStatus}
              onChange={handleProjectStatusChange}
              required
              data-testid="project-status-select"
            >
              {activeReferenceTypes.ProjectState.map((status) => (
                <MenuItem key={status.id} value={status.id} data-testid={`project-status-option-2`}>
                  {status.description}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </>
      )}
    </Box>
  );
};

export default InRoundCompletionUpdateProjectStatus;

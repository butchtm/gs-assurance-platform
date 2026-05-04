import { Box, Checkbox, FormControl, FormControlLabel, FormLabel } from "@mui/material";

import { PiiMetadata } from "../../../common/piiAnalysis/PiiAnalysis";

export const PiiNotFound = ({
  piiMetadata,
  titlePiiNotFound,
  descriptionPiiNotFound,
  notesPiiNotFound,
  setTitlePiiNotFound,
  setDescriptionPiiNotFound,
  setNotesPiiNotFound,
}: {
  piiMetadata: PiiMetadata;
  titlePiiNotFound: boolean;
  descriptionPiiNotFound: boolean;
  notesPiiNotFound: boolean;
  setTitlePiiNotFound: (variable: boolean) => void;
  setDescriptionPiiNotFound: (variable: boolean) => void;
  setNotesPiiNotFound: (variable: boolean) => void;
}) => {
  return (
    <Box mb={2}>
      <FormControl>
        <FormLabel>Please review the project for PII and check any false positives</FormLabel>
        {piiMetadata.piiDetail.find((detail) => detail == "title") && (
          <FormControlLabel
            control={<Checkbox checked={titlePiiNotFound} onChange={(e) => setTitlePiiNotFound(e.target.checked)} />}
            label="Project Title"
            data-testid="project-title-false-positive"
          />
        )}
        {piiMetadata.piiDetail.find((detail) => detail == "description") && (
          <FormControlLabel
            control={
              <Checkbox
                checked={descriptionPiiNotFound}
                onChange={(e) => setDescriptionPiiNotFound(e.target.checked)}
              />
            }
            label="Project Description"
            data-testid="project-description-false-positive"
          />
        )}
        {piiMetadata.piiDetail.find((detail) => detail == "notes") && (
          <FormControlLabel
            control={<Checkbox checked={notesPiiNotFound} onChange={(e) => setNotesPiiNotFound(e.target.checked)} />}
            label="Project Notes"
            data-testid="project-notes-false-positive"
          />
        )}
      </FormControl>
    </Box>
  );
};

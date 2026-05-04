import {
  Button,
  Checkbox,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  FormControlLabel,
  TextField,
  Typography,
} from "@mui/material";
import dayjs from "dayjs";
import { useState } from "react";

import { CurrentRiskSummary } from "../../../../common/project/Project";
import { updateProjectRisk } from "../../Project";

export const RiskModal = ({
  isOpen,
  onClose,
  project,
  currentRisk,
}: {
  isOpen: boolean;
  onClose: (updated: boolean) => void;
  project: {
    id: number;
    name: string;
    gsid: string;
  };
  currentRisk?: CurrentRiskSummary;
}) => {
  const [summary, setSummary] = useState<string>(currentRisk?.summary ?? "");
  const [highRisk, setHighRisk] = useState<boolean>(currentRisk?.risk ?? false);

  const handleSubmit = async () => {
    await updateProjectRisk(project.id, {
      risk: highRisk,
      summary: summary,
      dateApplied: dayjs.utc().toISOString(),
    });
    onClose(true);
  };

  return (
    <Dialog open={isOpen} onClose={() => onClose(false)}>
      <DialogTitle>Update Project Risk</DialogTitle>
      <DialogContent sx={{ width: "600px" }}>
        <FormControl fullWidth>
          <Typography variant="body1">
            <b>Project:</b> {project.gsid} - {project.name}
          </Typography>
          <FormControlLabel
            control={<Checkbox checked={highRisk} onChange={(e) => setHighRisk(e.target.checked)} />}
            label="High Risk"
            data-testid="project-risk-checkbox"
          />
          <TextField
            multiline
            label="Summary"
            rows={6}
            value={summary}
            onChange={(e) => setSummary(e.target.value)}
            required={true}
            data-testid="project-risk-summary"
            sx={{ marginTop: "10px" }}
            inputProps={{ maxLength: 2000 }}
            helperText={`${summary.length}/2000 characters`}
          />
        </FormControl>
      </DialogContent>
      <DialogActions>
        <Button variant="outlined" onClick={() => onClose(false)}>
          Cancel
        </Button>
        <Button variant="contained" onClick={() => handleSubmit()} disabled={!summary}>
          Update
        </Button>
      </DialogActions>
    </Dialog>
  );
};

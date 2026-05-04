import { ThemeProvider } from "@emotion/react";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import {
  Box,
  Button,
  Card,
  Container,
  FormControl,
  Grid,
  InputLabel,
  MenuItem,
  Select,
  Typography,
} from "@mui/material";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import React, { useState } from "react";
import { Helmet } from "react-helmet-async";
import { useLocation, useNavigate, type Location } from "react-router-dom";
import { toast } from "react-toastify";

import { ReferenceType, useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";
import { theme } from "../../../theme";
import { updateProjectStatus } from "../../Project/Project";

interface UpdateProjectStatusProps {
  status?: ReferenceType;
  projectId?: number;
}

const UpdateProjectStatus: React.FC<UpdateProjectStatusProps> = () => {
  const getOptions = (): ReferenceType[] => {
    const options = [status];
    const nextOption = activeReferenceTypes.ProjectState.find((x) => +x.id == +status.id + 1);
    if (nextOption) {
      options.push(nextOption);
    }
    return options;
  };

  const handleUpdateProjectStatus = async () => {
    if (updatedStatus && projectId) {
      const payload = { id: updatedStatus.id };
      const response = await updateProjectStatus(payload, projectId);
      if (response) {
        toast.success("Project Status Updated Successfully.");
        navigate("/update-project", { state: { projectId: projectId } });
      } else {
        console.error("Failed to update project status.");
      }
    }
  };

  const location: Location<{
    status: ReferenceType;
    projectId: number;
  }> = useLocation();
  const navigate = useNavigate();
  const { activeReferenceTypes } = useReferenceTypes();
  const { status, projectId } = location.state || {};
  const [updatedStatus, setUpdatedStatus] = useState<ReferenceType>(status);

  return (
    <>
      <ThemeProvider theme={theme}>
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <Helmet>
            <title>Update Project</title>
          </Helmet>
          <Container maxWidth="md">
            <Card className="wizard-card">
              <Button
                variant="outlined"
                startIcon={<ArrowBackIcon />}
                onClick={() =>
                  navigate("/update-project", {
                    state: {
                      projectId: projectId,
                    },
                  })
                }
                sx={{ alignSelf: "flex-start", marginBottom: 1 }}
                data-testid="back-button"
              >
                Back
              </Button>
              <Box>
                <Typography variant="h4">Update Project Status</Typography>
                <br></br>
              </Box>
              <Grid item xs={12} md={6}>
                {status && (
                  <FormControl fullWidth>
                    <InputLabel id="project-status-label">Project Status</InputLabel>
                    <Select
                      labelId="project-status-label"
                      label="Project Status"
                      onChange={(e) => {
                        const newStatus = activeReferenceTypes.ProjectState.find(
                          (x) => x.description == e.target.value,
                        );
                        if (newStatus) {
                          setUpdatedStatus(newStatus);
                        }
                      }}
                      value={updatedStatus.description}
                      style={{ textAlign: "left" }}
                      data-testid="project-status-select"
                    >
                      {getOptions().map((option) => (
                        <MenuItem key={option.id} value={option.description} style={{ textAlign: "left" }}>
                          {option.description}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                )}
              </Grid>
              <Box className="wizard-buttons">
                <Button type="submit" onClick={handleUpdateProjectStatus}>
                  Update
                </Button>
              </Box>
            </Card>
          </Container>
        </LocalizationProvider>
      </ThemeProvider>
    </>
  );
};

export default UpdateProjectStatus;

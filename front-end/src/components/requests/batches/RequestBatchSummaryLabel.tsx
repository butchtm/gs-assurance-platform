import { Box, Button, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { useContext } from "react";

import { BatchedProjectRequest } from "../Requests";
import { AuthContext } from "../../Auth/Login/AuthProvider";

import { BatchRiskPill } from "./BatchRiskPill";

export const RequestBatchSummaryLabel = ({
  batchedProjectRequest,
}: {
  batchedProjectRequest: BatchedProjectRequest;
}) => {
  const navigate = useNavigate();
  const authContext = useContext(AuthContext);
  const isArmOrAdmin = authContext.isAdminUser() || authContext.isArmUser();

  return (
    <Box display="flex" alignItems="center">
      <Typography sx={{ flexBasis: "20%" }}>{batchedProjectRequest.gsid}</Typography>
      <Typography sx={{ flexBasis: "35%" }}>{batchedProjectRequest.name}</Typography>
      <Typography sx={{ flexBasis: "35%" }}>{batchedProjectRequest.classification}</Typography>
      <Box sx={{ flexBasis: "10%" }}>
        {isArmOrAdmin && batchedProjectRequest.currentRiskSummary?.risk && <BatchRiskPill />}
      </Box>
      <Button
        variant="outlined"
        onClick={() =>
          navigate(`/projects/${batchedProjectRequest.id}/requests?expand=${batchedProjectRequest.requestId}`)
        }
        data-testid={`view-batch-request-${batchedProjectRequest.id}`}
      >
        View
      </Button>
    </Box>
  );
};

import Edit from "@mui/icons-material/Edit";
import { Box, IconButton, Table, TableBody, TableCell, TableHead, TableRow, Typography } from "@mui/material";
import dayjs from "dayjs";
import React, { useContext, useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";

import { getReferenceValueDescriptionById, useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { CreditingPeriod, Project } from "../../../../common/project/Project";
import { AuthContext } from "../../../Auth/Login/AuthProvider";
import { ProjectInConsultation } from "../../../Public/PublicConsultation/PublicConsultation";

import { MethodologiesHistory } from "./MethodologiesHistory";
import { MonitoringPeriods } from "./MonitoringPeriods";

interface LocationState {
  creditingPeriod: CreditingPeriod;
  project: Partial<Project> | ProjectInConsultation;
}

export const CreditingPeriodDetail: React.FC = () => {
  const { periodId } = useParams<{ periodId: string }>();
  const { state } = useLocation() as { state: LocationState };
  const { referenceMethodologies } = useReferenceTypes();
  const authContext = useContext(AuthContext);
  const [displayMethodologyHistory, setDisplayMethodologyHistory] = useState<boolean>(false);
  const [allowEdit, setAllowEdit] = useState<boolean>(false);
  const navigate = useNavigate();

  const methodologies = state?.creditingPeriod.methodologies || [];
  const monitoringPeriods = state?.creditingPeriod.monitoringPeriods;

  const isArmOrAdmin = authContext.isAdminUser() || authContext.isArmUser();

  const isProjectOwner = () => {
    return (
      state?.project &&
      "accountId" in state.project &&
      authContext.isProjectDeveloperUser() &&
      authContext.isUserOwnerOf(Number(state.project.accountId))
    );
  };

  const isNewProject = () => {
    const status = state.project.projectStatus;
    if (!status) {
      return false;
    }
    if (status == "New") {
      return true;
    }
    if (typeof status == "object" && status.code === "NEW") {
      return true;
    }
    return false;
  };

  const canViewMethodologyHistory = () => {
    return isArmOrAdmin || authContext.isSupportUser() || isProjectOwner();
  };

  const canEditProject = () => {
    return isArmOrAdmin || (isProjectOwner() && isNewProject());
  };

  useEffect(() => {
    setDisplayMethodologyHistory(canViewMethodologyHistory());
    setAllowEdit(canEditProject());
  }, [state]);

  if (!state)
    return (
      <Box
        className="view-container"
        sx={{
          border: (theme) => `1px solid ${theme.palette.error.main}`,
          borderRadius: "4px",
          padding: "16px",
          backgroundColor: "var(--color-error-surface-light)",
          color: (theme) => theme.palette.error.main,
        }}
      >
        Navigate to specific crediting period methodologies through the project information screen.
      </Box>
    );

  return (
    <Box className="view-container">
      <Box className="title" sx={{ display: "flex", flexDirection: "column" }}>
        <Box sx={{ mb: 1 }}>
          {state.project.gsid} - {state.project.name}
          {allowEdit && (
            <IconButton
              aria-label="Edit Project"
              data-testid="edit-project"
              onClick={() => navigate("/update-project", { state: { projectId: state.project.id } })}
              sx={{ color: "white", ml: "5px" }}
            >
              <Edit />
            </IconButton>
          )}
        </Box>
        <Box sx={{ mb: 1 }}>Methodologies for Crediting Period {periodId}</Box>
        <Box>
          {dayjs.utc(state.creditingPeriod.startDate).format("DD/MM/YYYY")} -{" "}
          {dayjs.utc(state.creditingPeriod.endDate).format("DD/MM/YYYY")}
        </Box>
      </Box>
      {state.project.id && monitoringPeriods && (
        <MonitoringPeriods monitoringPeriods={monitoringPeriods} project={state.project} />
      )}
      <Typography variant="h6" align="left" sx={{ color: "var(--color-text-black)", fontWeight: "bold", mt: 2 }}>
        Methodologies
      </Typography>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell sx={{ width: "50%", whiteSpace: "nowrap" }}>Methodology</TableCell>
            <TableCell>Version</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {methodologies.map((methodology) => (
            <TableRow key={methodology.id}>
              <TableCell sx={{ width: "50%", whiteSpace: "nowrap" }}>
                {getReferenceValueDescriptionById(methodology.id, referenceMethodologies)}
              </TableCell>
              <TableCell>{methodology.version ?? "Not set"}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
      {state.project.id && periodId && displayMethodologyHistory && (
        <MethodologiesHistory projectId={state.project.id} periodId={periodId} />
      )}
    </Box>
  );
};

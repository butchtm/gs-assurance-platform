import { WarningAmber } from "@mui/icons-material";
import {
  Box,
  Button,
  Grid,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Tooltip,
  Typography,
} from "@mui/material";
import dayjs from "dayjs";
import { useFormikContext } from "formik";
import React, { useContext } from "react";

import { useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { canRequestTypeCreateMonitoringPeriod } from "../../../../common/request/GSRequest";
import { AuthContext } from "../../../Auth/Login/AuthProvider";
import { BatchedProjectRequest } from "../../Requests";

import { CreateRequestForm } from "./Request";
import { MethodologiesSummaryTable } from "./methodologies/MethodologiesSummaryTable";

interface SummaryProps {
  batchedProjects: BatchedProjectRequest[] | null;
  setChangeStep: (step: number) => void;
  isMethodologyUpdateRequestType: boolean;
  summaryUpdating?: boolean;
}

export const Summary: React.FC<SummaryProps> = ({
  batchedProjects,
  setChangeStep,
  isMethodologyUpdateRequestType,
  summaryUpdating,
}) => {
  const { values } = useFormikContext<CreateRequestForm>();
  const { activeReferenceTypes } = useReferenceTypes();
  const authContext = useContext(AuthContext);
  const isArmOrAdmin = authContext.isAdminUser() || authContext.isArmUser();

  const feeSchedule = activeReferenceTypes.FeeSchedule.find((x) => x.id == values.feeSchedule)?.description;

  const getProjectRisk = (projectId: number) => {
    const matchedProject = batchedProjects?.find((x) => x.id == projectId);
    return matchedProject?.currentRiskSummary?.risk;
  };

  const hasAnyProjectRisk = values.batchProjectIds.find((id) => getProjectRisk(id));

  const getGsid = (projectId: number) => {
    const matchedProject = batchedProjects?.find((x) => x.id == projectId);
    return matchedProject ? matchedProject.gsid : "GSID Unavailable";
  };

  const getCreditingPeriod = (projectId: number) => {
    try {
      const currentBatchProject = values.batchStepProjectValues?.batch[projectId];
      const id = currentBatchProject?.creditingPeriodId;
      const periods = batchedProjects?.find((proj) => proj.id == projectId)?.creditingPeriods;
      if (currentBatchProject?.useNewCreditingPeriod) {
        return `New Period - ${dayjs
          .utc(currentBatchProject.newCreditingPeriodStartDate)
          .format("DD/MM/YYYY")} to ${dayjs.utc(currentBatchProject.newCreditingPeriodEndDate).format("DD/MM/YYYY")}`;
      }
      if (!periods) {
        return "Not available";
      }
      const p = periods.find((p) => p.periodId == id);
      return p
        ? `Period ${p.periodId} - ${dayjs.utc(p.startDate).format("DD/MM/YYYY")} to ${dayjs
            .utc(p.endDate)
            .format("DD/MM/YYYY")}`
        : id;
    } catch (e) {
      console.error("Missing crediting period data", e);
      return "None chosen";
    }
  };

  const shouldDisplayMonitoringPeriod = () => {
    const requestType = activeReferenceTypes.RequestType.find((x) => x.id.toString() == values.requestType);
    if (requestType) {
      return canRequestTypeCreateMonitoringPeriod(requestType?.code);
    }
  };

  const getMonitoringPeriod = (projectId: number) => {
    const currentBatchProject = values.batchStepProjectValues?.batch[projectId];
    if (
      currentBatchProject?.useNewMonitoringPeriod &&
      currentBatchProject.newMonitoringPeriodStartDate &&
      currentBatchProject.newMonitoringPeriodEndDate
    ) {
      return `New Period - ${currentBatchProject.newMonitoringPeriodStartDate.format(
        "DD/MM/YYYY",
      )} to ${currentBatchProject.newMonitoringPeriodEndDate.format("DD/MM/YYYY")}`;
    }
    const creditingPeriods = batchedProjects?.find((proj) => proj.id == projectId)?.creditingPeriods;
    if (!creditingPeriods) {
      return "Not available";
    }
    const creditingPeriodId = currentBatchProject?.creditingPeriodId;
    const monitoringPeriodId = currentBatchProject?.monitoringPeriodId;

    const mp = creditingPeriods
      .flatMap(({ periodId, monitoringPeriods }) =>
        monitoringPeriods?.map((period) => ({ creditingPeriodId: periodId, ...period })),
      )
      .find((period) =>
        period ? period.id == monitoringPeriodId && period.creditingPeriodId == creditingPeriodId : false,
      );

    return mp
      ? `Period ${mp.id} - ${dayjs.utc(mp.startDate).format("DD/MM/YYYY")} to ${dayjs
          .utc(mp.endDate)
          .format("DD/MM/YYYY")}`
      : monitoringPeriodId;
  };

  const getEmissionReductionAmount = (projectId: number) => {
    try {
      return values.batchStepProjectValues?.batch[projectId].emissionReductionAmount.toLocaleString();
    } catch (e) {
      console.error("Missing crediting period data", e);
      return "None chosen";
    }
  };

  const editRequest = (index: number) => {
    if (summaryUpdating) {
      setChangeStep(index);
    } else {
      setChangeStep(index + 2);
    }
  };

  return (
    <Box sx={{ p: 2 }}>
      <Grid container spacing={2}>
        <Grid item xs={12}>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Project</TableCell>
                  <TableCell sx={{ textWrap: "wrap" }}>Request Crediting Period</TableCell>
                  {shouldDisplayMonitoringPeriod() && (
                    <TableCell sx={{ textWrap: "wrap" }}>Request Monitoring Period</TableCell>
                  )}
                  <TableCell sx={{ textWrap: "wrap" }}>Request Emissions Reduction Amount</TableCell>
                  {isArmOrAdmin && hasAnyProjectRisk && <TableCell sx={{ textWrap: "wrap" }}>High Risk</TableCell>}
                </TableRow>
              </TableHead>
              <TableBody>
                {values.batchProjectIds?.map((projectId, index) => (
                  <TableRow key={projectId} sx={{ "&:last-child td, &:last-child th": { border: 0 } }}>
                    <TableCell>{getGsid(projectId)}</TableCell>
                    <TableCell>{getCreditingPeriod(projectId)}</TableCell>
                    {shouldDisplayMonitoringPeriod() && <TableCell>{getMonitoringPeriod(projectId)}</TableCell>}
                    <TableCell>{getEmissionReductionAmount(projectId)}</TableCell>
                    {isArmOrAdmin && hasAnyProjectRisk && (
                      <TableCell>
                        {getProjectRisk(projectId) && (
                          <Tooltip title="This project has been deemed high risk">
                            <WarningAmber sx={{ color: "var(--color-risk-high-border)" }} />
                          </Tooltip>
                        )}
                      </TableCell>
                    )}
                    <TableCell>
                      <Button variant="text" onClick={() => editRequest(index)}>
                        Edit
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
          {isMethodologyUpdateRequestType &&
            batchedProjects?.find((proj) => ["Programme of Activities"].includes(proj.classification)) && (
              <MethodologiesSummaryTable
                batchedProjects={batchedProjects}
                isPoa={true}
                changeStep={() => editRequest(0)}
              />
            )}
          {isMethodologyUpdateRequestType && batchedProjects && (
            <MethodologiesSummaryTable
              batchedProjects={batchedProjects}
              isPoa={false}
              changeStep={() =>
                editRequest(
                  batchedProjects?.find((proj) => ["Programme of Activities"].includes(proj.classification)) ? 1 : 0,
                )
              }
            />
          )}
          {feeSchedule && (
            <Typography variant="body1" sx={{ float: "left", mt: "10px" }}>
              <b>Fee Schedule:</b> {feeSchedule}
            </Typography>
          )}
        </Grid>
      </Grid>
    </Box>
  );
};

import { Table, TableBody, TableCell, TableHead, TableRow, Typography } from "@mui/material";
import dayjs from "dayjs";
import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { MonitoringPeriod, Project } from "../../../../common/project/Project";
import { GSRequest, isNullOrUndefined } from "../../../../common/request/GSRequest";
import { ProjectInConsultation } from "../../../Public/PublicConsultation/PublicConsultation";
import { getRequestsByProject } from "../../../requests/Requests";
export const MonitoringPeriods: React.FC<{
  monitoringPeriods: MonitoringPeriod[];
  project: Partial<Project> | ProjectInConsultation;
}> = ({ monitoringPeriods, project }) => {
  const { activeReferenceTypes } = useReferenceTypes();
  const [requests, setRequests] = useState<GSRequest[]>([]);
  useEffect(() => {
    const fetchRequests = async (): Promise<void> => {
      try {
        if (project.id) {
          const projectRequests = await getRequestsByProject(project.id);
          setRequests(projectRequests);
        }
      } catch (error) {
        console.error("Error fetching requests");
        throw error;
      }
    };

    if (project) {
      fetchRequests();
    }
  }, [project]);
  return (
    <>
      <Typography variant="h6" align="left" sx={{ color: "000000", fontWeight: "bold", mt: 2 }}>
        Monitoring Periods
      </Typography>
      {monitoringPeriods && monitoringPeriods?.length > 0 ? (
        <Table>
          <TableHead>
            <TableRow>
              <TableCell sx={{ width: "50%", whiteSpace: "nowrap" }}>id</TableCell>
              <TableCell>Start Date</TableCell>
              <TableCell>End Date</TableCell>
              <TableCell>Fee Schedule </TableCell>
              <TableCell>Request</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {monitoringPeriods.map((monitoringPeriod) => (
              <TableRow key={monitoringPeriod.id}>
                <TableCell>{monitoringPeriod.id}</TableCell>
                <TableCell>{dayjs.utc(monitoringPeriod.startDate).format("DD/MM/YYYY")}</TableCell>
                <TableCell>{dayjs.utc(monitoringPeriod.endDate).format("DD/MM/YYYY")}</TableCell>
                <TableCell>
                  {!isNullOrUndefined(monitoringPeriod.feeSchedule) &&
                    activeReferenceTypes.FeeSchedule.find(
                      (feeSchedule) => feeSchedule.id === monitoringPeriod.feeSchedule,
                    )?.description}
                </TableCell>
                <TableCell>
                  {project && requests.find((req) => req.id === monitoringPeriod.originRequestId) && (
                    <Link
                      target="_blank"
                      to={`/projects/${project.gsid}/requests?expand=${monitoringPeriod.originRequestId}`}
                      key={monitoringPeriod.originRequestId}
                    >
                      {requests.find((req) => req.id === monitoringPeriod.originRequestId)?.reviewName}
                    </Link>
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      ) : (
        <Typography sx={{ textAlign: "left" }}>No monitoring periods present</Typography>
      )}
    </>
  );
};

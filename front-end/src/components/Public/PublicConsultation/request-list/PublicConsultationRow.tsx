import { List, ListItem, TableCell, TableRow } from "@mui/material";
import dayjs from "dayjs";
import React, { useEffect, useState } from "react";

import { Methodology } from "../../../../common/project/Project";
import { MethodologyConfiguration } from "../../../requests/create-request/Wizard/Request";
import { ProjectInConsultationSummary, RequestInConsultationSummary } from "../PublicConsultation";

interface PublicConsultationRowProps {
  project: ProjectInConsultationSummary;
  request: RequestInConsultationSummary;
  singleRequest: boolean;
  onClick: (gsid: string, requestId: number) => void;
  expanded?: boolean;
}

interface ConsultationMethodologyTableData {
  id: number | "";
  description: string | undefined;
  version: number | null;
}

const PublicConsultationRow: React.FC<PublicConsultationRowProps> = ({
  project,
  request,
  singleRequest,
  onClick,
  expanded,
}) => {
  const [methodologies, setMethodologies] = useState<ConsultationMethodologyTableData[]>();

  useEffect(() => {
    getMethodologies(request);
  }, []);

  const getMethodologies = (request: RequestInConsultationSummary) => {
    const requestMethodologies = request.requestMethodologies;
    const creditingPeriodMethodologies = request.requestCreditingPeriod?.methodologies;

    if (requestMethodologies?.length) {
      setMethodologies(toTableFromMethodologyConfigurations(requestMethodologies));
    } else if (creditingPeriodMethodologies?.length) {
      setMethodologies(toTableFromMethodologies(creditingPeriodMethodologies));
    }
  };

  const toTableFromMethodologyConfigurations = (methodologyConfigs: MethodologyConfiguration[]) => {
    return methodologyConfigs.map((methodologyConfig) => toTableFromMethodologyConfiguration(methodologyConfig));
  };

  const toTableFromMethodologyConfiguration = (methodologyConfig: MethodologyConfiguration) => {
    const tableMethodology: ConsultationMethodologyTableData = {
      id: methodologyConfig.methodologyId,
      description: methodologyConfig.methodologyDescription,
      version: methodologyConfig.methodologyVersion,
    };

    return tableMethodology;
  };

  const toTableFromMethodologies = (methodologies: Methodology[]) => {
    return methodologies.map((methodology) => toTableFromMethodology(methodology));
  };

  const toTableFromMethodology = (methodology: Methodology) => {
    const tableMethodology: ConsultationMethodologyTableData = {
      id: methodology.id,
      description: methodology.description,
      version: methodology.version,
    };

    return tableMethodology;
  };

  return singleRequest || (!singleRequest && expanded) ? (
    <TableRow
      key={project.gsid}
      className="table-row"
      onClick={() => onClick(project.gsid, request.requestId)}
      sx={{ cursor: "pointer" }}
    >
      <TableCell className="table-cell">{singleRequest ? project.gsid : ""}</TableCell>
      <TableCell className="table-cell">{singleRequest ? project.name : ""}</TableCell>
      <TableCell></TableCell>
      <TableCell className="table-cell">{request.requestName}</TableCell>
      <TableCell className="table-cell">{request.requestStatus}</TableCell>
      <TableCell className="table-cell">
        {methodologies && methodologies.length > 0 ? (
          <List sx={{ width: "100%", padding: "0" }}>
            <ListItem
              sx={{
                justifyContent: "space-between",
                padding: "0",
                fontWeight: "600",
              }}
            >
              <span>Methodology</span>
              <span>Version</span>
            </ListItem>
            {methodologies.map((methodology) => (
              <ListItem
                key={methodology.id}
                sx={{
                  justifyContent: "space-between",
                  gap: "32px",
                  padding: "0",
                }}
              >
                <span>{methodology.description}</span>
                <span>{methodology.version}</span>
              </ListItem>
            ))}
          </List>
        ) : (
          ""
        )}
      </TableCell>
      <TableCell className="table-cell">{singleRequest ? project.projectLocations.join(", ") : ""}</TableCell>
      <TableCell className="table-cell">
        {request.consultationStartDate && dayjs.utc(request.consultationStartDate).format("DD/MM/YYYY")}
      </TableCell>
      <TableCell className="table-cell">
        {request.consultationEndDate && dayjs.utc(request?.consultationEndDate).format("DD/MM/YYYY")}
      </TableCell>
      <TableCell>{request.batchId !== null ? "Yes" : "No"}</TableCell>
    </TableRow>
  ) : (
    <></>
  );
};

export default PublicConsultationRow;

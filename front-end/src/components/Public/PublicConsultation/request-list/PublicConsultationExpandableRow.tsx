import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { IconButton, TableCell, TableRow } from "@mui/material";
import React, { useEffect, useState } from "react";

import { ProjectInConsultationSummary } from "../PublicConsultation";

import PublicConsultationRow from "./PublicConsultationRow";

interface PublicConsultationExpandableRowProps {
  project: ProjectInConsultationSummary;
  allExpanded: boolean;
  onClick: (gsid: string, requestId: number) => void;
  updateExpansionTracker: (gsid: string, expanded: boolean) => void;
}

const PublicConsultationExpandableRow: React.FC<PublicConsultationExpandableRowProps> = ({
  project,
  allExpanded,
  onClick,
  updateExpansionTracker,
}) => {
  const [expanded, setExpanded] = useState<boolean>(true);

  const handleExpandClick = () => {
    setExpanded(!expanded);
    updateExpansionTracker(project.gsid, !expanded);
  };

  useEffect(() => {
    setExpanded(allExpanded);
  }, [allExpanded]);

  return (
    <>
      <TableRow key={project.gsid} className="table-row">
        <TableCell className="table-cell">{project.gsid}</TableCell>
        <TableCell className="table-cell">{project.name}</TableCell>
        <TableCell>
          <IconButton
            sx={(theme) => ({
              transform: !expanded ? "rotate(0deg)" : "rotate(180deg)",
              marginLeft: "auto",
              transition: theme.transitions.create("transform", {
                duration: theme.transitions.duration.shortest,
              }),
            })}
            onClick={handleExpandClick}
            data-testid="consultation-expand-button"
          >
            <ExpandMoreIcon data-testid="consultation-expand-row" />
          </IconButton>
        </TableCell>
        <TableCell className="table-cell"></TableCell>
        <TableCell className="table-cell"></TableCell>
        <TableCell className="table-cell"></TableCell>
        <TableCell className="table-cell">{project.projectLocations.join(", ")}</TableCell>
        <TableCell className="table-cell"></TableCell>
        <TableCell className="table-cell"></TableCell>
        <TableCell></TableCell>
      </TableRow>
      {project.requests.map((request) => (
        <PublicConsultationRow
          key={request.requestId}
          project={project}
          request={request}
          singleRequest={false}
          expanded={expanded}
          onClick={onClick}
        />
      ))}
    </>
  );
};

export default PublicConsultationExpandableRow;

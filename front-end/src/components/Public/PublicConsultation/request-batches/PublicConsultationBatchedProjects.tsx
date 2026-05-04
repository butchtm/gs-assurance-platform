import { Table, TableBody, TableCell, TableHead, TableRow, Typography } from "@mui/material";
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import { getCreditingPeriod, ProjectInConsultation, RequestInConsultation } from "../PublicConsultation";

interface PublicConsultationBatchedProjectsProps {
  parentProject: ProjectInConsultation;
  requestInConsultation: RequestInConsultation;
}

const PublicConsultationBatchedProjects: React.FC<PublicConsultationBatchedProjectsProps> = ({
  parentProject,
  requestInConsultation,
}) => {
  const navigate = useNavigate();
  const [childProjects, setChildProjects] = useState<ProjectInConsultation[]>([]);

  useEffect(() => {
    setChildProjects(requestInConsultation.batchedProjectChildren);
  }, [requestInConsultation]);

  const getChildEstimatedAnnualEmissions = (project: ProjectInConsultation) => {
    const request = project.requests[0];
    return request.emissionReductionAmount;
  };

  const routeToProjectInformation = (project: ProjectInConsultation, childRequest?: RequestInConsultation) => {
    if (childRequest) {
      childRequest.consultationStartDate = requestInConsultation.consultationStartDate;
      childRequest.consultationEndDate = requestInConsultation.consultationEndDate;
    }
    navigate(`/public-consultations/project/${project.gsid}`, {
      state: {
        project: project,
        request: childRequest ?? requestInConsultation,
      },
    });
  };

  return (
    <div style={{ padding: "10px 0" }}>
      <Typography variant="h5" sx={{ textAlign: "left" }}>
        Projects in Batch Request
      </Typography>
      <Table sx={{ whiteSpace: "normal", wordWrap: "break-word" }}>
        <TableHead>
          <TableRow className="table-headings">
            <TableCell className="row-text">GSID</TableCell>
            <TableCell className="row-text">Project Classification</TableCell>
            <TableCell className="row-text">Crediting Period</TableCell>
            <TableCell className="row-text">Estimated Annual Emission Reduction</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          <TableRow
            key={parentProject.gsid}
            className="table-headings"
            onClick={() => routeToProjectInformation(parentProject)}
            sx={{ cursor: "pointer" }}
          >
            <TableCell className="row-text">{parentProject.gsid}</TableCell>
            <TableCell className="row-text">{parentProject.projectClassification}</TableCell>
            <TableCell className="row-text">{getCreditingPeriod(requestInConsultation)}</TableCell>
            <TableCell className="row-text">{requestInConsultation.emissionReductionAmount}</TableCell>
          </TableRow>
          {childProjects.map((project) => (
            <TableRow
              key={project.gsid}
              className="table-headings"
              onClick={() => routeToProjectInformation(project, project.requests[0])}
              sx={{ cursor: "pointer" }}
            >
              <TableCell className="row-text">{project.gsid}</TableCell>
              <TableCell className="row-text">{project.projectClassification}</TableCell>
              <TableCell className="row-text">{getCreditingPeriod(project.requests[0])}</TableCell>
              <TableCell className="row-text">{getChildEstimatedAnnualEmissions(project)}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  );
};

export default PublicConsultationBatchedProjects;

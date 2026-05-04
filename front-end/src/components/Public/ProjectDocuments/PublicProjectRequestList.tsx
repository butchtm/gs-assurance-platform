import { Typography } from "@mui/material";
import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import dayjs from "dayjs";

import { getPublicProjectDocuments, PublicProjectRequest } from "../../../common/document/ProjectDocuments";

import "./ProjectDocuments.css";
import { PublicRequestTabs } from "./PublicRequestTabs";

const ProjectDocumentsList: React.FC = () => {
  const [projectName, setProjectName] = useState<string>("");
  const [requests, setRequests] = useState<PublicProjectRequest[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const { gsId } = useParams();

  function formatDate(dateString: string): string {
    return dayjs.utc(dateString).format("DD/MM/YYYY");
  }

  useEffect(() => {
    async function fetchDocuments(projectGsid: string) {
      setIsLoading(true);
      const response = await getPublicProjectDocuments(projectGsid.toUpperCase());
      if (response) {
        setRequests(response.requests);
        setProjectName(response.name);
      }
      setIsLoading(false);
    }
    if (gsId) {
      fetchDocuments(gsId);
    }
  }, [gsId]);

  if (isLoading) {
    return null;
  }

  if (!projectName) {
    return (
      <Typography variant="h4" component="div" sx={{ textAlign: "center" }}>
        Project not found
      </Typography>
    );
  }

  return (
    <div className="table-container">
      <Typography className="pub-project-name" variant="h4">
        {gsId && gsId.toUpperCase() + " - "}
        {projectName}
      </Typography>
      {requests.length === 0 && (
        <Typography className="pub-project-name" variant="h6">
          Project does not have any public documents or stakeholder consultation information
        </Typography>
      )}
      {requests.map((request) => (
        <React.Fragment key={request.id}>
          <Typography className="pub-request-name" variant="h6">
            {request.name}
          </Typography>
          {request.consultationStartDate && request.consultationEndDate && (
            <Typography className="pub-request-name" variant="h5">
              Stakeholder Consultation {formatDate(request.consultationStartDate)} -{" "}
              {formatDate(request.consultationEndDate)}
            </Typography>
          )}
          <PublicRequestTabs
            documents={request.documents}
            comments={request.comments}
            isPublicConsultationPage={false}
            requestId={request.id}
          />
        </React.Fragment>
      ))}
    </div>
  );
};

export default ProjectDocumentsList;

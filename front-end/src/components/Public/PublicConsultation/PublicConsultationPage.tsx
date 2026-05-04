import { CircularProgress, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import { PublicRequestTabs } from "../ProjectDocuments/PublicRequestTabs";

import PublicConsultationProjectDetails from "./project-details/PublicConsultationProjectDetails";
import {
  getSpecificProjectConsultation,
  ProjectInConsultation,
  RequestInConsultation,
  getRequestProjectGsid,
} from "./PublicConsultation";
import PublicConsultationBatchedProjects from "./request-batches/PublicConsultationBatchedProjects";
import PublicConsultationRequestDetails from "./request-details/PublicConsultationRequestDetails";

const PublicConsultationPage: React.FC = () => {
  const [project, setProject] = useState<ProjectInConsultation>();
  const [request, setRequest] = useState<RequestInConsultation>();
  const [loading, setLoading] = useState<boolean>(true);
  const params = useParams();
  const requestId = Number(params.requestId);

  useEffect(() => {
    const fetchDetails = async () => {
      const gsid = await getRequestProjectGsid(requestId);
      const projectData = await getSpecificProjectConsultation(gsid);
      setProject(projectData);
      const fetchedRequest = projectData.requests.find((r) => r.requestId === requestId);
      if (fetchedRequest) {
        setRequest(fetchedRequest);
      }
      setLoading(false);
    };
    fetchDetails();
  }, [location]);

  if (loading) {
    return (
      <div style={{ padding: "20px" }}>
        <CircularProgress color="primary" size={75} />
      </div>
    );
  }

  return (
    <div style={{ padding: "20px" }}>
      {project && request ? (
        <>
          <PublicConsultationRequestDetails request={request}></PublicConsultationRequestDetails>
          <PublicRequestTabs
            documents={request.documents}
            comments={request.comments}
            isPublicConsultationPage={true}
            requestId={request.requestId}
          />
          {request.batchedProjectChildren.length > 0 ? (
            <PublicConsultationBatchedProjects
              parentProject={project}
              requestInConsultation={request}
            ></PublicConsultationBatchedProjects>
          ) : (
            <PublicConsultationProjectDetails project={project}></PublicConsultationProjectDetails>
          )}
        </>
      ) : (
        <Typography>Unable to find request details</Typography>
      )}
    </div>
  );
};

export default PublicConsultationPage;

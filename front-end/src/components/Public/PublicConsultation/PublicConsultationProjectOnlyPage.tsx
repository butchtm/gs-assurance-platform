import { useLocation, type Location } from "react-router-dom";

import PublicConsultationProjectDetails from "./project-details/PublicConsultationProjectDetails";
import { ProjectInConsultation, RequestInConsultation } from "./PublicConsultation";
import PublicConsultationRequestDetails from "./request-details/PublicConsultationRequestDetails";

const PublicConsultationProjectOnlyPage: React.FC = () => {
  const location: Location<{
    project: ProjectInConsultation;
    request: RequestInConsultation;
  }> = useLocation();
  const { project, request } = location.state || {};

  return (
    <div style={{ padding: "20px" }}>
      <PublicConsultationRequestDetails request={request}></PublicConsultationRequestDetails>
      <PublicConsultationProjectDetails project={project}></PublicConsultationProjectDetails>
    </div>
  );
};

export default PublicConsultationProjectOnlyPage;

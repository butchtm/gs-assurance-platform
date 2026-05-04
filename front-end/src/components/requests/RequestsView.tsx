import Button from "@mui/material/Button";
import React, { useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import { Project } from "../../common/project/Project";
import { GSRequest } from "../../common/request/GSRequest";
import { AuthContext } from "../Auth/Login/AuthProvider";

import { RequestDropdown } from "./RequestDropdown";
import { getRequestsByProject } from "./Requests";
import "./RequestsView.scss";

interface RequestViewProps {
  project: Partial<Project>;
  specificRequest?: number;
  refreshProject?: () => void;
}

export const RequestsView: React.FC<RequestViewProps> = (props: {
  project: Partial<Project>;
  specificRequest?: number;
  refreshProject?: () => void;
}) => {
  const navigate = useNavigate();

  const [requests, setRequests] = useState<GSRequest[]>([]);
  const [refreshRequest, setRefreshRequest] = useState<boolean>(false);
  const authContext = useContext(AuthContext);

  useEffect(() => {
    setRefreshRequest(false);
    const fetchRequestByProject = async () => {
      if (props.project.id) {
        try {
          const response = await getRequestsByProject(props.project.id);
          setRequests(response);
        } catch (error) {
          console.error("Error fetching requests", error);
        }
      }
    };

    fetchRequestByProject();
  }, [props.project, refreshRequest]);

  const scrollToRequest = (requestId: number) => {
    const timer = setTimeout(() => {
      const element = document.getElementById(`request-${requestId}`);
      if (element) {
        const precedingElement = element.previousElementSibling?.previousElementSibling; //tagging onto 2 elements behind due to nav bar
        if (precedingElement) {
          precedingElement.scrollIntoView({ behavior: "auto" });
        } else {
          element.scrollIntoView({ behavior: "auto" });
        }
      }
    }, 100);
    return () => clearTimeout(timer);
  };

  useEffect(() => {
    if (props.specificRequest && requests.length > 0) {
      scrollToRequest(props.specificRequest);
    }
  }, [requests, props.specificRequest]);

  const createNewRequest = () => {
    navigate("/create-request", { state: { project: props.project } });
  };

  const canCreateRequest = (project: Partial<Project>) => {
    if (authContext.isProjectDeveloperUser()) {
      return project.accountId && authContext.isUserOwnerOf(project.accountId);
    }
    return authContext.isAdminUser() || authContext.isArmUser();
  };

  return (
    <div className="requests-view">
      {canCreateRequest(props.project) && (
        <Button variant="outlined" onClick={createNewRequest}>
          Create New Request
        </Button>
      )}
      {requests.map((request) => (
        <RequestDropdown
          key={request.id}
          request={request}
          opened={request.id === props.specificRequest}
          projectId={props.project.id ?? 0}
          dashboardFormat={false}
          refreshProject={props.refreshProject}
        />
      ))}
    </div>
  );
};

import KeyboardArrowUpIcon from "@mui/icons-material/KeyboardArrowUp";
import TabContext from "@mui/lab/TabContext";
import TabList from "@mui/lab/TabList";
import TabPanel from "@mui/lab/TabPanel";
import { Box, Grid, Tab } from "@mui/material";
import { useContext, useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";

import { Tag } from "@/common/tags";
import { getRequestAssignments, Assignment } from "@/components/requests/assignment/Assignment";
import { GSDocument } from "@/common/document/GSDocument";
import { Project } from "@/common/project/Project";
import { GSRequest, RequestConsultation } from "@/common/request/GSRequest";
import { AuthContext } from "@/components/Auth/Login/AuthProvider";
import { getProject } from "@/components/Project/Project";
import { TabLabel } from "@/components/Reusable/Tab/TabLabel";

import { ActionsDropdown } from "./actions/ActionsDropdown";
import { ActivityDropdown } from "./activity/ActivityDropdown";
import { ActivityHistory } from "./activity/ActivityHistory";
import { ActivityRequestHeadline } from "./activity/ActivityRequestHeadline";
import { RequestBatchSummary } from "./batches/RequestBatchSummary";
import { getRequestComments, RequestComment } from "./comments/RequestComments";
import { RequestCommentsList } from "./comments/RequestCommentsList";
import { DocumentLists } from "./documents/DocumentLists";
import { PathwayGroupTimeline } from "./pathway/PathwayGroupTimeline";
import { CreditingPeriodSection } from "./periods/CreditingPeriodSection";
import {
  BatchedProjectRequests,
  getAllDocumentsForProjectRequest,
  getConsultationsForRequest,
  getPrivateBatchProjectRequests,
  getRequestWithId,
  getRequiredDocumentsForRequest,
  RequiredDocumentsResponse,
} from "./Requests";
import "./RequestsView.scss";
import { RequestHeadline } from "./reusable/RequestHeadline";
import { StatusDropdown } from "./status-details/StatusDropdown";
import { MessagePill } from "./reusable/MessagePill";

enum RequestInformationTabs {
  Documents,
  History,
  Periods,
  Comments,
}

export const RequestDropdown = (props: {
  request: GSRequest;
  opened: boolean;
  projectId: number;
  dashboardFormat: boolean;
  refreshProject?: () => void;
}) => {
  const [showDropdown, setShowDropdown] = useState(props.opened);
  const [refreshDocuments, setRefreshDocuments] = useState(false);
  const [refreshRequest, setRefreshRequest] = useState<boolean>(false);
  const [requestDocuments, setRequestDocuments] = useState<GSDocument[]>([]);
  const [requestTypeRequiredDocuments, setRequestTypeRequiredDocuments] = useState<RequiredDocumentsResponse>();
  const [batchedProjectRequests, setBatchedProjectRequests] = useState<BatchedProjectRequests>();
  const [selectedTabType, setSelectedTabType] = useState<RequestInformationTabs>(RequestInformationTabs.Documents);
  const [project, setProject] = useState<Project | null>(null);
  const authContext = useContext(AuthContext);
  const [, setSearchParams] = useSearchParams();
  const [requestComments, setRequestComments] = useState<RequestComment[]>([]);
  const [consultations, setConsultations] = useState<RequestConsultation[]>([]);
  const [request, setRequest] = useState<GSRequest>(props.request);
  const isCompletedApproved = request.requestStatus.code === "COMPLETED_APPROVED";
  const latestActivity = request.latestRequestActivity;
  const hasActivity = !!latestActivity;
  const [assignments, setAssignments] = useState<Assignment[]>([]);

  const fetchComments = async () => {
    setRequestComments(await getRequestComments(request.id));
  };

  useEffect(() => {
    const isOwningProjectDeveloper = (project: Project) => {
      if (authContext.isProjectDeveloperUser()) {
        return project.accountId && authContext.isUserOwnerOf(project.accountId);
      }
      return false;
    };

    const fetchProjectAndAssignments = async () => {
      const project = await getProject(props.projectId);
      setProject(project);
      if (isOwningProjectDeveloper(project)) {
        setAssignments(await getRequestAssignments(request.id));
      }
    };

    const fetchDocuments = async () => {
      const [requiredDocuments, requestDocuments] = await Promise.all([
        getRequiredDocumentsForRequest(request.requestType.id),
        getAllDocumentsForProjectRequest(props.projectId, request.id),
      ]);
      setRequestTypeRequiredDocuments(requiredDocuments);
      setRequestDocuments(requestDocuments);
    };

    const fetchBatchedProjects = async () => {
      if (request?.batchId) {
        setBatchedProjectRequests(await getPrivateBatchProjectRequests(request.batchId));
      }
    };

    const fetchConsultations = async () => {
      setConsultations(await getConsultationsForRequest(request.id));
    };

    if (showDropdown) {
      fetchProjectAndAssignments();
      fetchDocuments();
      fetchComments();
      fetchBatchedProjects();
      fetchConsultations();
    }
  }, [request, refreshDocuments, showDropdown, refreshRequest]);

  const changeVisibility = () => {
    const newDropdownState = !showDropdown;
    if (newDropdownState) {
      setSearchParams({ expand: request.id.toString() });
    } else {
      setSearchParams({});
    }
    setShowDropdown(newDropdownState);
  };

  const updateRequest = async () => {
    try {
      setRequest(await getRequestWithId(request.id));
    } catch {
      console.error("Warning: Unable to fetch single request due to privileges");
    }

    if (typeof props.refreshProject === "function") {
      props.refreshProject();
    }

    setRefreshRequest((prev) => !prev);
  };

  const updateDocuments = () => {
    setRefreshDocuments((prev) => !prev);
  };

  const canViewPathwayGroupTimeline = (): boolean => {
    if (authContext.isVvbUser() || authContext.isProjectDeveloperUser()) {
      return request.requestStatus.code !== "COMPLETENESS_CHECK";
    }
    return true;
  };

  return (
    <div className="request-drop-down drop-down" id={"request-" + request.id}>
      <div className="request-key-information-container title-box" onClick={() => changeVisibility()}>
        <Box className="key-information" sx={{ display: "flex", gap: "0.5rem", alignItems: "center" }}>
          <p
            className="request-name key-information"
            data-testid="request-name-key-information"
            onClick={() => changeVisibility()}
          >
            {request.reviewName}
          </p>
          <RequestHeadline
            key={request.requestStatus.code}
            value={request.requestStatus.description}
            completedApproved={isCompletedApproved}
          />
          {request.inStakeholderConsultation && (
            <RequestHeadline value="In Stakeholder Consultation" completedApproved={isCompletedApproved} />
          )}
          {request.fastTracked && <RequestHeadline value="Fast Track" completedApproved={isCompletedApproved} />}
          {request.internal && <RequestHeadline value="Internal" completedApproved={isCompletedApproved} />}
          {request.internal === false && <RequestHeadline value="External" completedApproved={isCompletedApproved} />}
          {request.migrated && <RequestHeadline value="SustainCert" />}
          {request.latestRequestActivity && (
            <ActivityRequestHeadline
              date={request.latestRequestActivity.dueDate}
              activityName={request.latestRequestActivity.name}
            />
          )}
          {!!request.conversation?.unreadMessages && request.conversation?.unreadMessages > 0 && (
            <MessagePill count={request.conversation.unreadMessages} />
          )}
          {request.paa && <Tag variant="info" label="PAA" />}
        </Box>
        <Box
          className="image-container-dropdown-arrow"
          onClick={() => changeVisibility()}
          sx={(theme) => ({
            transform: showDropdown ? "rotate(180deg)" : "rotate(0deg)",
            transition: theme.transitions.create("transform", {
              duration: theme.transitions.duration.shortest,
            }),
          })}
        >
          <KeyboardArrowUpIcon />
        </Box>
      </div>
      {showDropdown && (
        <>
          <div style={{ display: "flex" }}>
            <Grid container spacing={2}>
              {latestActivity && (
                <Grid item xs={12} sm={12} md={4} lg={3} xl={3}>
                  <ActivityDropdown requestActivity={latestActivity} />
                </Grid>
              )}
              <Grid item xs={12} sm={12} md={hasActivity ? 5 : 8} lg={hasActivity ? 6 : 8} xl={hasActivity ? 6 : 8}>
                {requestTypeRequiredDocuments && (
                  <StatusDropdown
                    request={request}
                    dashboardFormat={props.dashboardFormat}
                    refreshRequest={refreshRequest}
                    requestDocuments={requestDocuments}
                    requestTypeRequiredDocs={requestTypeRequiredDocuments}
                    consultations={consultations}
                    assignments={assignments}
                  />
                )}
              </Grid>
              {!!project?.accountId && (
                <Grid item xs={12} sm={12} md={hasActivity ? 3 : 4} lg={hasActivity ? 3 : 4} xl={hasActivity ? 3 : 4}>
                  <ActionsDropdown
                    request={request}
                    updateRequest={updateRequest}
                    updateDocuments={updateDocuments}
                    batchedRequests={batchedProjectRequests}
                    accountId={project?.accountId}
                    requiredDocuments={requestTypeRequiredDocuments}
                  />
                </Grid>
              )}
            </Grid>
          </div>
          {!!request.batchId && batchedProjectRequests && (
            <RequestBatchSummary projectId={props.projectId} batchRequests={batchedProjectRequests} />
          )}
          {canViewPathwayGroupTimeline() && (
            <Box sx={{ margin: "28px 0" }}>
              <Box
                sx={{
                  margin: "6px 0 22px",
                  padding: "10px 10px",
                  borderBottom: "1px solid lightgray",
                  textAlign: "left",
                }}
              >
                <span>Pathway Timeline</span>
              </Box>
              <PathwayGroupTimeline requestId={request.id} refreshRequest={refreshRequest} />
            </Box>
          )}
          <TabContext value={String(selectedTabType)}>
            <TabList onChange={(event, newTabType) => setSelectedTabType(newTabType)}>
              <Tab label="Documents" value={String(RequestInformationTabs.Documents)} />
              {(authContext.isArmUser() || authContext.isAdminUser() || authContext.isSupportUser()) && (
                <Tab label="History" value={String(RequestInformationTabs.History)} />
              )}
              <Tab label="Crediting Period" value={String(RequestInformationTabs.Periods)} />
              <Tab
                sx={{
                  overflow: "visible",
                }}
                label={<TabLabel text="Comments" count={requestComments?.length ?? 0} />}
                value={String(RequestInformationTabs.Comments)}
              />
            </TabList>
            <TabPanel value={String(RequestInformationTabs.Documents)} sx={{ padding: "5px" }}>
              {requestTypeRequiredDocuments && (
                <DocumentLists
                  projectId={props.projectId}
                  request={request}
                  updateDocuments={updateDocuments}
                  requestTypeRequiredDocuments={requestTypeRequiredDocuments}
                  requestDocuments={requestDocuments}
                  refreshDocuments={refreshDocuments}
                />
              )}
            </TabPanel>
            {(authContext.isArmUser() || authContext.isAdminUser() || authContext.isSupportUser()) && (
              <TabPanel value={String(RequestInformationTabs.History)} sx={{ padding: "5px" }}>
                <ActivityHistory request={request} />
              </TabPanel>
            )}
            <TabPanel value={String(RequestInformationTabs.Periods)} sx={{ padding: "5px" }}>
              {project && <CreditingPeriodSection request={request} project={project} />}
            </TabPanel>
            <TabPanel value={String(RequestInformationTabs.Comments)} sx={{ padding: "5px" }}>
              <RequestCommentsList
                consultations={consultations}
                requestComments={requestComments}
                refreshComments={fetchComments}
              />
            </TabPanel>
          </TabContext>
        </>
      )}
    </div>
  );
};

import { Box, CircularProgress } from "@mui/material";
import { useContext, useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { GSRequest } from "../../../common/request/GSRequest";
import { AuthContext } from "../../Auth/Login/AuthProvider";
import { Assignment, getRequestAssignments } from "../assignment/Assignment";
import ViewBatchProjectsAction from "../batches/ViewBatchProjectsAction";
import {
  BatchedProjectRequest,
  BatchedProjectRequests,
  getPublicBatchedProjectRequests,
  RequiredDocumentsResponse,
} from "../Requests";
import { RequestSectionHeader } from "../reusable/RequestSectionHeader";

import { Action, getAvailableActions } from "./Actions";
import AcceptDeclineAction from "./assignments/AcceptDeclineAction";
import { AssignmentAction } from "./assignments/AssignmentAction";
import { CancelRequestAction } from "./CancelRequestAction";
import { CloseActivityAction } from "./CloseActivityAction";
import { CommentCreateAction } from "./comments/CommentCreateAction";
import { EndConsultationAction } from "./EndConsultationAction";
import { CreateInvoiceAction } from "./invoice/CreateInvoiceAction";
import { UpdateInvoiceStatusAction } from "./invoice/UpdateInvoiceStatusAction";
import { NoActionsAvailable } from "./NoActionsAvailable";
import { RequestUpdateAction } from "./RequestUpdateAction";
import { SubmitRequestAction } from "./SubmitRequestAction";
import { ApproveRequestAction } from "./ApproveRequestAction";
import { UpdateRorDateAction } from "./update-ror-date/UpdateRorDateAction";
import { DocumentUploadAction } from "./documents/DocumentUploadAction";
import { ConversationAction } from "./ConversationAction";

export const ActionsDropdown = (props: {
  request: GSRequest;
  updateRequest: () => void;
  updateDocuments: () => void;
  batchedRequests?: BatchedProjectRequests;
  accountId: number;
  requiredDocuments?: RequiredDocumentsResponse;
}) => {
  const [assignments, setAssignments] = useState<Assignment[]>([]);
  const [availableActions, setAvailableActions] = useState<Action[]>([]);
  const [shouldShowViewBatchProjectsButton, setShouldShowViewBatchProjectsButton] = useState<boolean>(false);
  const [isChildOfBatch, setIsChildOfBatch] = useState<boolean>(true);
  const [parentRequestUrl, setParentRequestUrl] = useState<string>("");
  const [parentProjectGsid, setParentProjectGsid] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(true);
  const authContext = useContext(AuthContext);

  useEffect(() => {
    if (loading) {
      setAvailableActions([]);
      return;
    }

    const currentActions = getAvailableActions(
      authContext,
      assignments,
      props.request.requestStatus.code,
      props.request.inStakeholderConsultation,
      props.request.latestRequestActivity,
      props.accountId,
      props.request.conversation?.status,
    );
    setAvailableActions(currentActions);
  }, [loading, assignments, props.request]);

  const fetchAndCheckAssignments = async () => {
    try {
      if (!authContext.isReviewerUser() && !authContext.isVvbUser() && !authContext.isRegistryUser()) {
        return;
      }
      setLoading(true);
      const fetchedAssignments = await getRequestAssignments(props.request.id);
      setAssignments(fetchedAssignments);
    } catch (error) {
      console.error("Error fetching assignments", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAndCheckAssignments();
  }, [props.request.id]);

  useEffect(() => {
    const loadBatchedProjects = async () => {
      if (!props.request.batchId) {
        setIsChildOfBatch(false);
        return;
      }

      try {
        setLoading(true);
        const batchedProjectRequests = await getPublicBatchedProjectRequests(props.request.batchId);
        const parentId = batchedProjectRequests.parent.gsid;

        setIsChildOfBatch(parentId !== undefined && parentId !== props.request.project.gsid);
        setParentRequestUrl(getRequestUrl(batchedProjectRequests.parent));
        setParentProjectGsid(batchedProjectRequests.parent.gsid);
      } catch (error) {
        console.error("Error loading batched projects", error);
      } finally {
        setLoading(false);
      }
    };

    loadBatchedProjects();
  }, [props.request.batchId, props.request.project.gsid]);

  useEffect(() => {
    const handleLegacyCheckAndStatus = () => {
      if (props.request.migrated) {
        if (props.request.legacyBatchProjectIds && props.request.legacyBatchProjectIds?.length > 1) {
          setShouldShowViewBatchProjectsButton(true);
        }
      }
    };

    handleLegacyCheckAndStatus();
  }, [props.request.migrated, props.request.legacyBatchProjectIds]);

  const getRequestUrl = (parentBatchProjectRequest: BatchedProjectRequest) => {
    return `/projects/${parentBatchProjectRequest.gsid}/requests?expand=${parentBatchProjectRequest.requestId}`;
  };

  return (
    <Box sx={{ paddingRight: "10px" }}>
      <RequestSectionHeader name="Actions" />
      {loading ? (
        <div style={{ display: "flex", justifyContent: "center", alignItems: "center", marginTop: "20px" }}>
          <CircularProgress />
        </div>
      ) : (
        <>
          {availableActions.length === 0 ? (
            <NoActionsAvailable />
          ) : (
            <>
              {availableActions.includes(Action.SubmitRequest) && (
                <SubmitRequestAction
                  request={props.request}
                  updateRequest={props.updateRequest}
                  disabled={isChildOfBatch}
                />
              )}
              {availableActions.includes(Action.CancelRequest) && (
                <CancelRequestAction
                  request={props.request}
                  updateRequest={props.updateRequest}
                  disabled={isChildOfBatch}
                />
              )}
              {availableActions.includes(Action.UpdateRequest) && (
                <RequestUpdateAction request={props.request} requestBatches={props.batchedRequests} />
              )}
              {availableActions.includes(Action.UploadDocument) && (
                <DocumentUploadAction
                  request={props.request}
                  onDocumentUpload={props.updateDocuments}
                  disabled={isChildOfBatch}
                  requiredDocuments={props.requiredDocuments}
                />
              )}
              {availableActions.includes(Action.UpdateAssignments) && (
                <AssignmentAction request={props.request} disabled={isChildOfBatch} />
              )}
              {availableActions.includes(Action.UpdateRorDates) && (
                <UpdateRorDateAction
                  request={props.request}
                  updateRequest={props.updateRequest}
                  disabled={isChildOfBatch}
                />
              )}
              {availableActions.includes(Action.CreateComment) && (
                <CommentCreateAction
                  request={props.request}
                  updateRequest={props.updateRequest}
                  disabled={isChildOfBatch}
                />
              )}
              {availableActions.includes(Action.EndConsultation) && (
                <EndConsultationAction request={props.request} updateRequest={props.updateRequest} />
              )}
              {availableActions.includes(Action.CreateInvoice) && (
                <CreateInvoiceAction request={props.request} updateRequest={props.updateRequest} />
              )}
              {availableActions.includes(Action.UpdateInvoice) && (
                <UpdateInvoiceStatusAction request={props.request} updateRequest={props.updateRequest} />
              )}
              {availableActions.includes(Action.AcceptDeclineAssignment) && (
                <AcceptDeclineAction
                  request={props.request}
                  refreshRequest={props.updateRequest}
                  refreshAssignments={fetchAndCheckAssignments}
                />
              )}
              {availableActions.includes(Action.CloseActivity) && props.request.latestRequestActivity?.id && (
                <CloseActivityAction
                  activityId={props.request.latestRequestActivity.id}
                  updateRequest={props.updateRequest}
                />
              )}
              {availableActions.includes(Action.ApproveRequest) && (
                <ApproveRequestAction
                  projectId={props.request.project.id}
                  requestId={props.request.id}
                  updateRequest={props.updateRequest}
                />
              )}
              {(availableActions.includes(Action.OpenConversation) ||
                availableActions.includes(Action.ViewConversation)) && (
                <ConversationAction
                  request={props.request}
                  updateRequest={props.updateRequest}
                  initial={availableActions.includes(Action.OpenConversation)}
                />
              )}
              {isChildOfBatch && parentProjectGsid && (
                <sub>
                  Navigate to <Link to={parentRequestUrl}>{parentProjectGsid}</Link> to use all actions
                </sub>
              )}
            </>
          )}
        </>
      )}
      {shouldShowViewBatchProjectsButton && <ViewBatchProjectsAction request={props.request} />}
    </Box>
  );
};

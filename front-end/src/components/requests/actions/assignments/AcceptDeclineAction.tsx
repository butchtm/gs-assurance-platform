import { ThumbsUpDown } from "@mui/icons-material";
import { Button } from "@mui/material";
import { useContext, useEffect, useState } from "react";

import { ReferenceType, useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { CreateUpdateRequestPayload, GSRequest } from "../../../../common/request/GSRequest";
import { AuthContext } from "../../../Auth/Login/AuthProvider";
import { getRequestAssignments } from "../../assignment/Assignment";
import { updateRequest } from "../../Requests";
import AcceptDeclineModal from "../../status-change/AcceptDeclineModal";

interface AcceptDeclineActionProps {
  request: GSRequest;
  refreshRequest: () => void;
  refreshAssignments: () => void;
}

const AcceptDeclineAction = ({ request, refreshRequest, refreshAssignments }: AcceptDeclineActionProps) => {
  const { activeReferenceTypes } = useReferenceTypes();
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [currentAssignmentType, setCurrentAssignmentType] = useState<ReferenceType>();
  const [requestAssignmentId, setRequestAssignmentId] = useState<number>();
  const [refreshState, setRefreshState] = useState<boolean>();
  const authContext = useContext(AuthContext);

  const inPreparationStatus = activeReferenceTypes.RequestStatus.find((status) => status.code == "IN_PREPARATION");
  const inAssignmentStatus = activeReferenceTypes.RequestStatus.find((status) => status.code == "IN_ASSIGNMENT");

  const inAuditAssignmentStatus = activeReferenceTypes.AssignmentType.find((status) => status.code == "AUDIT");
  const inReviewAssignmentStatus = activeReferenceTypes.AssignmentType.find((status) => status.code == "REVIEW");

  const handleOpenModal = () => {
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
  };

  const handleRefresh = () => {
    setRefreshState((prev) => !prev);
  };

  const handleAssignmentAccept = () => {
    if (!currentAssignmentType || !inReviewAssignmentStatus) {
      throw Error("Assignment data not set");
    }

    refreshAssignments();
  };

  const getRequestStatusToRevertTo = (): ReferenceType => {
    if (!currentAssignmentType || !inPreparationStatus || !inAssignmentStatus) {
      throw Error("Assignment type not set");
    }
    if (currentAssignmentType.code === "AUDIT") {
      return inPreparationStatus;
    } else if (currentAssignmentType.code === "REVIEW") {
      return inAssignmentStatus;
    } else {
      throw Error("Unable to decline assignment type");
    }
  };

  const handleAssignmentDecline = async () => {
    const requestStatusToRevertTo = getRequestStatusToRevertTo();
    if (requestStatusToRevertTo.id !== request.requestStatus.id) {
      const requestToSubmit: Partial<CreateUpdateRequestPayload> = {
        id: request.id,
        requestStatusId: requestStatusToRevertTo.id,
      };
      await updateRequest(requestToSubmit, request.project.id);
      refreshRequest();
      handleRefresh();
    }
    refreshAssignments();
  };

  useEffect(() => {
    const showAcceptDeclineButton = async () => {
      try {
        const requestAssignments = await getRequestAssignments(request.id);
        if (authContext.isVvbUser()) {
          const auditAssignment = requestAssignments.find(
            (requestAssignment) =>
              requestAssignment.assignmentType.code === "AUDIT" &&
              requestAssignment.assignmentStatus.code === "PENDING",
          );
          setCurrentAssignmentType(inAuditAssignmentStatus);
          if (auditAssignment?.id) {
            setRequestAssignmentId(auditAssignment.id);
          }
        }
        if (authContext.isReviewerUser() || authContext.isRegistryUser()) {
          const reviewAssignment = requestAssignments.find(
            (requestAssignment) =>
              requestAssignment.assignmentType.code === "REVIEW" &&
              requestAssignment.assignmentStatus.code === "PENDING",
          );
          setCurrentAssignmentType(inReviewAssignmentStatus);
          if (reviewAssignment?.id) {
            setRequestAssignmentId(reviewAssignment.id);
          }
        }
      } catch (error) {
        console.error("Error fetching assignments:", error);
      }
    };
    showAcceptDeclineButton();
  }, [refreshState]);

  return (
    <>
      <Button variant="outlined" className="action-button" onClick={handleOpenModal}>
        <ThumbsUpDown data-testid="thumbs-up-down" />
        <span className="action-text">Accept / Decline Request</span>
      </Button>

      {requestAssignmentId && isModalOpen && (
        <AcceptDeclineModal
          assignmentId={requestAssignmentId}
          isOpen={isModalOpen}
          onClose={handleCloseModal}
          onDecline={handleAssignmentDecline}
          onAccept={handleAssignmentAccept}
        />
      )}
    </>
  );
};

export default AcceptDeclineAction;

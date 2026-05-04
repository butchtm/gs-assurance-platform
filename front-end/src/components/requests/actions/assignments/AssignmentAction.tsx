import AssignmentIcon from "@mui/icons-material/Assignment";
import { Button } from "@mui/material";
import { useState } from "react";

import { GSRequest, hasClosedStatus } from "../../../../common/request/GSRequest";
import { AssignmentModal } from "../../assignment/AssignmentModal";
import { AssignmentSummaryModal } from "../../assignment/AssignmentSummaryModal";
import "../../RequestsView.scss";

export const AssignmentAction = ({
  request,
  disabled,
  onSave,
}: {
  request: GSRequest;
  disabled: boolean;
  onSave?: () => void;
}) => {
  const [isAssignmentModalOpen, setIsAssignmentModalOpen] = useState(false);
  const [isSummaryModalOpen, setIsSummaryModalOpen] = useState(false);

  const handleOpenAssignmentModal = () => {
    setIsAssignmentModalOpen(true);
  };

  const handleCloseAssignmentModal = () => {
    setIsAssignmentModalOpen(false);
  };

  const handleSummaryModal = () => {
    setIsSummaryModalOpen((prev) => !prev);
  };

  return (
    <>
      <Button disabled={disabled} onClick={handleOpenAssignmentModal} variant="outlined" className="action-button">
        <AssignmentIcon />
        <span className="action-text">View Assignments</span>
      </Button>

      {isAssignmentModalOpen && (
        <AssignmentModal
          readonlyOnOpen={hasClosedStatus(request)}
          isOpen={isAssignmentModalOpen}
          onClose={handleCloseAssignmentModal}
          handleOpenSummaryModal={handleSummaryModal}
          requestId={request.id}
          requestProjectName={request.project.name}
          requestTypeId={request.requestType.id}
          requestTypeDescription={request.requestType.description}
          requestStatusId={request.requestStatus.id}
          onSave={onSave}
        />
      )}
      {isSummaryModalOpen && <AssignmentSummaryModal isOpen={isSummaryModalOpen} onClose={handleSummaryModal} />}
    </>
  );
};

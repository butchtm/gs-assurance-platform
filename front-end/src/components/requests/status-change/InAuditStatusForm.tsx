import { Box, Button } from "@mui/material";
import React, { useCallback, useEffect, useState } from "react";

import { useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";
import { CreateUpdateRequestPayload, GSRequest } from "../../../common/request/GSRequest";
import { handleApiError } from "../../APIClient/APIErrorHandler";
import LoadingSpinner from "../../Reusable/LoadingSpinner";
import { updateRequest } from "../Requests";

import RequestStatusChangeDisplay from "./RequestStatusChangeDisplay";

interface RequestStatusFormProps {
  request: GSRequest;
  onSubmit: () => void;
}

const InAuditStatusForm: React.FC<RequestStatusFormProps> = ({ request, onSubmit }) => {
  const [chosenNextStatus, setChosenNextStatus] = useState<number>(request.requestStatus.id);
  const [completenessExists, setCompletenessExists] = useState<boolean>(false);
  const { activeReferenceTypes } = useReferenceTypes();
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);

  const completenessCheckStatus = activeReferenceTypes.RequestStatus.find(
    (requestStatus) => requestStatus.code === "COMPLETENESS_CHECK",
  );
  const inRoundCompletionStatus = activeReferenceTypes.RequestStatus.find(
    (requestStatus) => requestStatus.code === "IN_ROUND_COMPLETION",
  );

  const completenessCompletedStatusHistory =
    request.requestStatusHistory?.filter((status) => status.previousStatusId === completenessCheckStatus?.id) ?? [];
  const roundCompletionStatusHistory =
    request.requestStatusHistory?.filter((status) => status.previousStatusId === inRoundCompletionStatus?.id) ?? [];

  const checkCurrentStatusChange = useCallback(() => {
    const inProgressStatus = activeReferenceTypes.RequestStatus.find(
      (requestStatus) => requestStatus.code === "IN_PROGRESS",
    );

    if (request.currentRoundIndex && request.currentRoundIndex >= 1) {
      setCompletenessExists(true);
    }

    if (request.fastTracked && inRoundCompletionStatus) {
      setChosenNextStatus(inRoundCompletionStatus.id);
    } else if (request.currentRoundIndex && request.currentRoundIndex >= 1 && inProgressStatus) {
      setChosenNextStatus(inProgressStatus.id);
    } else if (!completenessExists && completenessCheckStatus) {
      setChosenNextStatus(completenessCheckStatus.id);
    } else if (
      completenessCompletedStatusHistory.length > 0 &&
      roundCompletionStatusHistory.length > 0 &&
      inProgressStatus
    ) {
      setChosenNextStatus(inProgressStatus.id);
    }
  }, [
    activeReferenceTypes.RequestStatus,
    completenessCheckStatus,
    completenessCompletedStatusHistory.length,
    completenessExists,
    inRoundCompletionStatus?.id,
    request.fastTracked,
    request.currentRoundIndex,
    roundCompletionStatusHistory.length,
  ]);

  useEffect(() => {
    checkCurrentStatusChange();
  }, [checkCurrentStatusChange, completenessExists, request.requestRounds, request.requestStatusHistory]);

  const handleClickSubmit = async () => {
    setIsSubmitting(true);

    if (chosenNextStatus) {
      const requestToSubmit: Partial<CreateUpdateRequestPayload> = {
        id: request.id,
        requestStatusId: chosenNextStatus,
      };

      if (request.project) {
        try {
          await updateRequest(requestToSubmit, request.project?.id);
        } catch (error) {
          handleApiError(error);
        } finally {
          onSubmit();
          setIsSubmitting(false);
        }
      }
    }

    setIsSubmitting(false);
  };

  return (
    <Box display="flex" flexDirection="column" alignItems="center" justifyContent="center" p={2}>
      <RequestStatusChangeDisplay chosenNextStatus={chosenNextStatus} />

      <Box mt={2}>
        <Button onClick={handleClickSubmit} variant="contained" color="primary" data-testid="submit-in-audit-status">
          Submit
        </Button>
      </Box>
      {isSubmitting && <LoadingSpinner />}
    </Box>
  );
};

export default InAuditStatusForm;

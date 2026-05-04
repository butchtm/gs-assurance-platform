import { Loop } from "@mui/icons-material";
import { Button } from "@mui/material";
import { useEffect, useState } from "react";

import { GSRequest } from "../../../common/request/GSRequest";
import { RequestStatusResponse, getAvailableRequestStatusTransitions } from "../Requests";
import "../RequestsView.scss";
import { ChangeRequestStatusModal } from "../status-change/SubmitRequestModal";

interface CancelRequestActionProps {
  request: GSRequest;
  updateRequest: () => void;
  disabled: boolean;
}

export const SubmitRequestAction = ({ request, updateRequest, disabled }: CancelRequestActionProps) => {
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [availableTransitionStatuses, setAvailableTransitionStatuses] = useState<RequestStatusResponse>();

  const handleOpenModal = () => {
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    updateRequest();
  };

  useEffect(() => {
    async function fetchData() {
      const availableStatuses = await getAvailableRequestStatusTransitions(request.requestStatus.id);

      if (availableStatuses) {
        setAvailableTransitionStatuses(availableStatuses);
      }
    }
    fetchData();
  }, [request]);

  return (
    <>
      <Button disabled={disabled} onClick={handleOpenModal} variant="outlined" className="action-button">
        <Loop />
        <span className="action-text">Submit Request</span>
      </Button>

      {isModalOpen && (
        <ChangeRequestStatusModal
          isOpen={isModalOpen}
          onClose={handleCloseModal}
          request={request}
          availableTransitionStatuses={availableTransitionStatuses}
          updateRequest={updateRequest}
        />
      )}
    </>
  );
};

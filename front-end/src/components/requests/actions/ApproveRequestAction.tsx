import { Check } from "@mui/icons-material";
import { Button } from "@mui/material";
import { useState } from "react";
import { toast } from "react-toastify";

import { CreateUpdateRequestPayload } from "../../../common/request/GSRequest";
import { updateRequest as doUpdateRequest } from "../../requests/Requests";

import "../RequestsView.scss";

import { ApproveRequestConfirmationDialog } from "./ApproveRequestConfirmationDialog";

export const ApproveRequestAction = ({
  projectId,
  requestId,
  updateRequest,
}: {
  projectId: number;
  requestId: number;
  updateRequest: () => void;
}) => {
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);

  const openModal = () => {
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
  };

  const approveRequest = async () => {
    try {
      const approvedRequest: Partial<CreateUpdateRequestPayload> = {
        id: requestId,
        requestStatusId: 4, // completed approved status id,
      };
      await doUpdateRequest(approvedRequest, projectId);
      updateRequest();
    } catch (error) {
      console.error("Error approving request:", error);
      toast.error("Error approving request");
    }
  };

  return (
    <>
      <Button onClick={openModal} className="action-button">
        <Check />
        <span className="action-text">Approve Request</span>
      </Button>
      {isModalOpen && (
        <ApproveRequestConfirmationDialog
          isOpen={isModalOpen}
          onClose={closeModal}
          onConfirm={() => {
            approveRequest();
            closeModal();
            updateRequest();
          }}
        />
      )}
    </>
  );
};

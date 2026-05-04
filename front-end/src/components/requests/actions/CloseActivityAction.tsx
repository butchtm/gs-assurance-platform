import { Close } from "@mui/icons-material";
import { Button } from "@mui/material";
import { useState } from "react";
import { toast } from "react-toastify";

import { apiClient } from "../../APIClient/Client";
import "../RequestsView.scss";

import { CloseActivityConfirmationDialog } from "./CloseActivityConfirmationDialog";

export const CloseActivityAction = ({
  activityId,
  updateRequest,
}: {
  activityId: number;
  updateRequest: () => void;
}) => {
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);

  const openModal = () => {
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
  };

  const closeActivity = async () => {
    try {
      await apiClient.makeRequest(`/request-activities/${activityId}/completions`, "POST");
      updateRequest();
    } catch (error) {
      console.error("Error marking activity as done:", error);
      toast.error("Error marking activity as done");
    }
  };

  return (
    <>
      <Button onClick={openModal} className="action-button">
        <Close />
        <span className="action-text">Close Activity</span>
      </Button>
      {isModalOpen && (
        <CloseActivityConfirmationDialog
          isOpen={isModalOpen}
          onClose={closeModal}
          onConfirm={() => {
            closeActivity();
            closeModal();
            updateRequest();
          }}
        />
      )}
    </>
  );
};

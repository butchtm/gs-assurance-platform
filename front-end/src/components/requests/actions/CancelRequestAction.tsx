import { Cancel } from "@mui/icons-material";
import { Button } from "@mui/material";
import { useState } from "react";

import { GSRequest } from "../../../common/request/GSRequest";
import { CancelRequestModal } from "../cancel-request/CancelRequestModal";
import "../RequestsView.scss";

interface CancelRequestActionProps {
  request: GSRequest;
  updateRequest: () => void;
  disabled: boolean;
}

export const CancelRequestAction = ({ request, updateRequest, disabled }: CancelRequestActionProps) => {
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleOpenModal = () => {
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
  };

  return (
    <>
      <Button disabled={disabled} onClick={handleOpenModal} variant="outlined" className="action-button">
        <Cancel />
        <span className="action-text">Cancel Request</span>
      </Button>

      {isModalOpen && (
        <CancelRequestModal
          isOpen={isModalOpen}
          onClose={handleCloseModal}
          request={request}
          updateRequestRefresh={updateRequest}
        />
      )}
    </>
  );
};
